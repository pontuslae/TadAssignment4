package Domain;

import Application.Main;
import Foundation.*;

import static Application.Main.loaded;
import static Application.Main.sound;

/**
 * Created by tim on 3/16/2017.
 */
public class Saucer extends UFO {

    //public static Framework ufo;

    @Override
    public void draw(){
        // Create shape for the flying saucer.

        //ufo = new Framework();
        shape.addPoint(-15, 0);
        shape.addPoint(-10, -5);
        shape.addPoint(-5, -5);
        shape.addPoint(-5, -8);
        shape.addPoint(5, -8);
        shape.addPoint(5, -5);
        shape.addPoint(10, -5);
        shape.addPoint(15, 0);
        shape.addPoint(10, 5);
        shape.addPoint(-10, 5);
    }

    @Override
    public void init() {

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        active = true;
        x = -Framework.width / 2;
        y = Math.random() * 2 * Framework.height - Framework.height;
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = Framework.MAX_ROCK_SPEED / 2 + Math.random() * (Framework.MAX_ROCK_SPEED / 2);
        deltaX = speed * -Math.sin(angle);
        deltaY = speed *  Math.cos(angle);
        if (Math.random() < 0.5) {
            x = Framework.width / 2;
            deltaX = -deltaX;
        }
        if (y > 0)
            deltaY = deltaY;
        render();
        Audio.saucerPlaying = true;
        if (Audio.sound)
        	Audio.saucerSound.loop();
        counter = (int) Math.abs(Framework.width / deltaX);
    }

    @Override
    public void update() {

        int i, d;
        boolean wrapped;

        // Move the flying saucer and check for collision with a photon. Stop it
        // when its counter has expired.

        if (active) {
            if (--counter <= 0) {
                if (--ufoPassesLeft > 0)
                    init();
                else
                    stop();
            }
            if (active) {
                advance();
                render();
                for (i = 0; i < Framework.MAX_SHOTS; i++)
                    if (Asteroid.photons[i].active && isColliding(Asteroid.photons[i])) {
                        if (Audio.sound)
                        	Audio.crashSound.play();
                        Explosion.explode(this);
                        stop();

                        Main.score += Framework.UFO_POINTS;

                    }

                // On occassion, fire a missle at the ship if the saucer is not too
                // close to it.

	            d = (int) Math.max(Math.abs(x - Main.ship.x), Math.abs(y - Main.ship.y));
	            for (Missle e: Framework.missles){
		            if (e.active){
			            if (Main.ship.active && Main.hyperCounter <= 0 && active &&
					            d > Framework.MAX_ROCK_SPEED * FPS / 2 &&
					            Math.random() < Framework.MISSLE_PROBABILITY)
			            	new Missle(Main.ship);
		            }
	            }

            }
        }
    }

    @Override
    public void stop() {

        active = false;
        counter = 0;
        ufoPassesLeft = 0;
        if (Main.loaded)
        	Audio.saucerSound.stop();
        Audio.saucerPlaying = false;
    }
}
