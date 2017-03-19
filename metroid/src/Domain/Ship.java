package Domain;

import Application.Main;
import Foundation.Audio;

import static Application.keyControls.*;

/**
 * Created by tim on 3/16/2017.
 */
public class Ship extends UFO {

    //public static Framework ship;
    public static Framework fwdThruster, revThruster;

    public Ship(){
    	fwdThruster = new Framework();
    	revThruster = new Framework();
    }
    @Override
    public void draw(){
        // Create shape for the ship sprite.

       // ship = new Framework();
        shape.addPoint(0, -10);
        shape.addPoint(7, 10);
        shape.addPoint(-7, 10);
    }

    public void drawThrusters(){
            // Create shapes for the ship thrusters.

        //fwdThruster = new Framework();
        fwdThruster.shape.addPoint(0, 12);
        fwdThruster.shape.addPoint(-3, 16);
        fwdThruster.shape.addPoint(0, 26);
        fwdThruster.shape.addPoint(3, 16);
        //revThruster = new Framework();
        revThruster.shape.addPoint(-2, 12);
        revThruster.shape.addPoint(-4, 14);
        revThruster.shape.addPoint(-2, 20);
        revThruster.shape.addPoint(0, 14);
        revThruster.shape.addPoint(2, 12);
        revThruster.shape.addPoint(4, 14);
        revThruster.shape.addPoint(2, 20);
        revThruster.shape.addPoint(0, 14);
    }

    @Override
    public void init() {

        // Reset the ship sprite at the center of the screen.

        active = true;
        angle = 0.0;
        deltaAngle = 0.0;
        x = 0.0;
        y = 0.0;
        deltaX = 0.0;
        deltaY = 0.0;
        render();

        // Initialize thruster sprites.

        fwdThruster.x = x;
        fwdThruster.y = y;
        fwdThruster.angle = angle;
        fwdThruster.render();
        revThruster.x = x;
        revThruster.y = y;
        revThruster.angle = angle;
        revThruster.render();

        if (Main.loaded)
        	Audio.thrustersSound.stop();
        Audio.thrustersPlaying = false;
        Main.hyperCounter = 0;
    }

    @Override
    public void update() {

        double dx, dy, speed;

        if (!Main.playing)
            return;

        // Rotate the ship if left or right cursor key is down.

        if (left) {
            angle += Framework.SHIP_ANGLE_STEP;
            if (angle > 2 * Math.PI)
                angle -= 2 * Math.PI;
        }
        if (right) {
            angle -= Framework.SHIP_ANGLE_STEP;
            if (angle < 0)
                angle += 2 * Math.PI;
        }

        // Fire thrusters if up or down cursor key is down.

        dx = Framework.SHIP_SPEED_STEP * -Math.sin(angle);
        dy = Framework.SHIP_SPEED_STEP *  Math.cos(angle);
        if (up) {
            deltaX += dx;
            deltaY += dy;
        }
        if (down) {
            deltaX -= dx;
            deltaY -= dy;
        }

        // Don't let ship go past the speed limit.

        if (up || down) {
            speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (speed > Framework.MAX_SHIP_SPEED) {
                dx = Framework.MAX_SHIP_SPEED * -Math.sin(angle);
                dy = Framework.MAX_SHIP_SPEED *  Math.cos(angle);
                if (up)
                    deltaX = dx;
                else
                    deltaX = -dx;
                if (up)
                    deltaY = dy;
                else
                    deltaY = -dy;
            }
        }

        // Move the ship. If it is currently in hyperspace, advance the countdown.

        if (active) {
            advance();
            render();
            if (Main.hyperCounter > 0)
            	Main.hyperCounter--;

            // Update the thruster sprites to match the ship sprite.

            fwdThruster.x = x;
            fwdThruster.y = y;
            fwdThruster.angle = angle;
            fwdThruster.render();
            revThruster.x = x;
            revThruster.y = y;
            revThruster.angle = angle;
            revThruster.render();
        }

        // Domain.Ship is exploding, advance the countdown or create a new ship if it is
        // done exploding. The new ship is added as though it were in hyperspace.
        // (This gives the player time to move the ship if it is in imminent
        // danger.) If that was the last ship, end the game.

        else
        if (--counter <= 0)
            if (Main.shipsLeft > 0) {
                init();
                Main.hyperCounter = Framework.HYPER_COUNT;
            }
            else
                Main.endGame();
    }

    @Override
    public void stop() {

        active = false;
        counter = Framework.SCRAP_COUNT;
        if (Main.shipsLeft > 0)
        	Main.shipsLeft--;
        if (Main.loaded)
        	Audio.thrustersSound.stop();
        Audio.thrustersPlaying = false;
    }
}
