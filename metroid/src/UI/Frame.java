package UI;

import Domain.Asteroid;
import Domain.Framework;
import Domain.Projectile;
import Domain.Ship;
import Application.keyControls;
import Domain.Saucer;

import java.applet.Applet;
import java.awt.*;


public class Frame extends Framework {

    /**
     * Variable section
     */

    // Flags for game state and options.
    boolean loaded = false;
    boolean paused;
    boolean playing;
    boolean sound;
    boolean detail;

    // Off screen image.
    Dimension offDimension;
    Image     offImage;
    Graphics  offGraphics;

    // Background stars.
    int     numStars;
    Point[] stars;

    // Data for the screen font.
    Font font      = new Font("Helvetica", Font.BOLD, 12);
    FontMetrics fm = getFontMetrics(font);
    int fontWidth  = fm.getMaxAdvance();
    int fontHeight = fm.getHeight();

    // Game data.
    int score;
    int highScore;
    int newShipScore;
    int newUfoScore;




    /**
     * Method Section
     */



    public void paint(Graphics g) {
        Dimension d = getSize();
        int i;
        int c;
        String s;
        int w, h;
        int x, y;

        // Create the off screen graphics context, if no good one exists.

        if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
            offDimension = d;
            offImage = createImage(d.width, d.height);
            offGraphics = offImage.getGraphics();
        }

        // Fill in background and stars.

        offGraphics.setColor(Color.black);
        offGraphics.fillRect(0, 0, d.width, d.height);
        if (detail) {
            offGraphics.setColor(Color.white);
            for (i = 0; i < numStars; i++)
                offGraphics.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
        }

        // Draw photon bullets.

        offGraphics.setColor(Color.white);
        for (i = 0; i < Projectile.MAX_SHOTS; i++)
            if (Asteroid.photons[i].active)
                offGraphics.drawPolygon(Asteroid.photons[i].sprite);

        // Draw the guided missle, counter is used to quickly fade color to black
        // when near expiration.

        c = Math.min(missleCounter * 24, 255);
        offGraphics.setColor(new Color(c, c, c));
        if (missle.active) {
            offGraphics.drawPolygon(missle.sprite);
            offGraphics.drawLine(missle.sprite.xpoints[missle.sprite.npoints - 1], missle.sprite.ypoints[missle.sprite.npoints - 1],
                   missle.sprite.xpoints[0], missle.sprite.ypoints[0]);
        }

        // Draw the asteroids.

        for (i = 0; i < MAX_ROCKS; i++)
            if (Asteroid.asteroids[i].active) {
                if (detail) {
                    offGraphics.setColor(Color.black);
                    offGraphics.fillPolygon(Asteroid.asteroids[i].sprite);
                }
                offGraphics.setColor(Color.white);
                offGraphics.drawPolygon(Asteroid.asteroids[i].sprite);
                offGraphics.drawLine(Asteroid.asteroids[i].sprite.xpoints[Asteroid.asteroids[i].sprite.npoints - 1], Asteroid.asteroids[i].sprite.ypoints[Asteroid.asteroids[i].sprite.npoints - 1],
                        Asteroid.asteroids[i].sprite.xpoints[0], Asteroid.asteroids[i].sprite.ypoints[0]);
            }

        // Draw the flying saucer.

        if (Saucer.ufo.active) {
            if (detail) {
                offGraphics.setColor(Color.black);
                offGraphics.fillPolygon(Saucer.ufo.sprite);
            }
            offGraphics.setColor(Color.white);
            offGraphics.drawPolygon(Saucer.ufo.sprite);
            offGraphics.drawLine(Saucer.ufo.sprite.xpoints[Saucer.ufo.sprite.npoints - 1], Saucer.ufo.sprite.ypoints[Saucer.ufo.sprite.npoints - 1],
                    Saucer.ufo.sprite.xpoints[0], Saucer.ufo.sprite.ypoints[0]);
        }

        // Draw the ship, counter is used to fade color to white on hyperspace.

        c = 255 - (255 / HYPER_COUNT) * hyperCounter;
        if (Ship.ship.active) {
            if (detail && hyperCounter == 0) {
                offGraphics.setColor(Color.black);
                offGraphics.fillPolygon(Ship.ship.sprite);
            }
            offGraphics.setColor(new Color(c, c, c));
            offGraphics.drawPolygon(Ship.ship.sprite);
            offGraphics.drawLine(Ship.ship.sprite.xpoints[Ship.ship.sprite.npoints - 1], Ship.ship.sprite.ypoints[Ship.ship.sprite.npoints - 1],
                    Ship.ship.sprite.xpoints[0], Ship.ship.sprite.ypoints[0]);

            // Draw thruster exhaust if thrusters are on. Do it randomly to get a
            // flicker effect.

            if (!paused && detail && Math.random() < 0.5) {
                if (keyControls.up) {
                    offGraphics.drawPolygon(Ship.fwdThruster.sprite);
                    offGraphics.drawLine(Ship.fwdThruster.sprite.xpoints[Ship.fwdThruster.sprite.npoints - 1], Ship.fwdThruster.sprite.ypoints[Ship.fwdThruster.sprite.npoints - 1],
                            Ship.fwdThruster.sprite.xpoints[0], Ship.fwdThruster.sprite.ypoints[0]);
                }
                if (keyControls.down) {
                    offGraphics.drawPolygon(Ship.revThruster.sprite);
                    offGraphics.drawLine(Ship.revThruster.sprite.xpoints[Ship.revThruster.sprite.npoints - 1], Ship.revThruster.sprite.ypoints[Ship.revThruster.sprite.npoints - 1],
                            Ship.revThruster.sprite.xpoints[0], Ship.revThruster.sprite.ypoints[0]);
                }
            }
        }

        // Draw any explosion debris, counters are used to fade color to black.

        for (i = 0; i < MAX_SCRAP; i++)
            if (Asteroid.explosions[i].active) {
                c = (255 / SCRAP_COUNT) * explosionCounter [i];
                offGraphics.setColor(new Color(c, c, c));
                offGraphics.drawPolygon(Asteroid.explosions[i].sprite);
            }

        // Display status and messages.

        offGraphics.setFont(font);
        offGraphics.setColor(Color.white);

        offGraphics.drawString("Score: " + score, fontWidth, fontHeight);
        offGraphics.drawString("Ships: " + shipsLeft, fontWidth, d.height - fontHeight);
        s = "High: " + highScore;
        offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);
        if (!sound) {
            s = "Mute";
            offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
        }

        if (!playing) {
            s = copyName;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
            s = copyVers;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
            s = copyInfo;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
            s = copyLink;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
            if (!loaded) {
                s = "Loading sounds...";
                w = 4 * fontWidth + fm.stringWidth(s);
                h = fontHeight;
                x = (d.width - w) / 2;
                y = 3 * d.height / 4 - fm.getMaxAscent();
                offGraphics.setColor(Color.black);
                offGraphics.fillRect(x, y, w, h);
                offGraphics.setColor(Color.gray);
                if (clipTotal > 0)
                    offGraphics.fillRect(x, y, (int) (w * clipsLoaded / clipTotal), h);
                offGraphics.setColor(Color.white);
                offGraphics.drawRect(x, y, w, h);
                offGraphics.drawString(s, x + 2 * fontWidth, y + fm.getMaxAscent());
            }
            else {
                s = "Game Over";
                offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
                s = "'S' to Start";
                offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4 + fontHeight);
            }
        }
        else if (paused) {
            s = "Game Paused";
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
        }

        // Copy the off screen buffer to the screen.

        g.drawImage(offImage, 0, 0, this);
    }


}
