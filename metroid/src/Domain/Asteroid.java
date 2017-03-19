package Domain;

import static Application.Main.saucer;
import static Application.Main.ship;
import static Application.Main.score;
import static Foundation.Audio.sound;
import static Domain.Framework.*;

import java.applet.AudioClip;
import java.awt.Polygon;

import Application.Main;
import Foundation.Audio;

public class Asteroid extends Framework {

	static final int DELAY = 20;             // Milliseconds between screen and
	static final int FPS   =                 // the resulting frame rate.
			Math.round(1000 / DELAY);

	static final int MAX_SHOTS =  8;          // Maximum number of sprites
	static final int MAX_ROCKS =  8;          // for photons, asteroids and
	static final int MAX_SCRAP = 40;          // explosions.

	static final int SCRAP_COUNT  = 2 * FPS;  // Timer counter starting values
	static final int HYPER_COUNT  = 3 * FPS;  // calculated using number of
	static final int MISSLE_COUNT = 4 * FPS;  // seconds x frames per second.
	static final int STORM_PAUSE  = 2 * FPS;

	static final int    MIN_ROCK_SIDES =   6; // Ranges for asteroid shape, size
	static final int    MAX_ROCK_SIDES =  16; // speed and rotation.
	static final int    MIN_ROCK_SIZE  =  20;
	static final int    MAX_ROCK_SIZE  =  40;
	static final double MIN_ROCK_SPEED =  40.0 / FPS;
	static final double MAX_ROCK_SPEED = 240.0 / FPS;
	static final double MAX_ROCK_SPIN  = Math.PI / FPS;
	static boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
	public static int asteroidsCounter;                            // Break-time counter.
	public static double asteroidsSpeed;                              // Asteroid speed.
	public static int asteroidsLeft;                               // Number of active asteroids.


	// Sound clips.

	static AudioClip crashSound;

	public static void initAsteroids() {

		int i, j;
		int s;
		double theta, r;
		int x, y;

		// Create random shapes, positions and movements for each asteroid.

		for (Asteroid e: Framework.asteroids) {

			// Create a jagged shape for the asteroid and give it a random rotation.

			e.shape = new Polygon();
			s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
			for (j = 0; j < s; j ++) {
				theta = 2 * Math.PI / s * j;
				r = MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE));
				x = (int) -Math.round(r * Math.sin(theta));
				y = (int)  Math.round(r * Math.cos(theta));
				e.shape.addPoint(x, y);
			}
			e.active = true;
			e.angle = 0.0;
			e.deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;

			// Place the asteroid at one edge of the screen.

			if (Math.random() < 0.5) {
				e.x = -Framework.width / 2;
				if (Math.random() < 0.5)
					e.x = Framework.width / 2;
				e.y = Math.random() * Framework.height;
			}
			else {
				e.x = Math.random() * Framework.width;
				e.y = -Framework.height / 2;
				if (Math.random() < 0.5)
					e.y = Framework.height / 2;
			}

			// Set a random motion for the asteroid.

			e.deltaX = Math.random() * asteroidsSpeed;
			if (Math.random() < 0.5)
				e.deltaX = -e.deltaX;
			e.deltaY = Math.random() * asteroidsSpeed;
			if (Math.random() < 0.5)
				e.deltaY = -e.deltaY;

			e.render();
			// asteroidIsSmall[i] = false; // wtf does this do.
		}

		asteroidsCounter = STORM_PAUSE;
		asteroidsLeft = MAX_ROCKS;
		if (asteroidsSpeed < MAX_ROCK_SPEED)
			asteroidsSpeed += 0.5;
	}




	public void initSmallAsteroids(int n) {

		int count;
		int i, j;
		int s;
		double tempX, tempY;
		double theta, r;
		int x, y;

		// Create one or two smaller asteroids from a larger one using inactive
		// asteroids. The new asteroids will be placed in the same position as the
		// old one but will have a new, smaller shape and new, randomly generated
		// movements.

		count = 0;
		i = 0;
		tempX = this.x;
		tempY = this.y;
		do {
			if (!active) {
				this.shape = new Polygon();
				s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
				for (j = 0; j < s; j ++) {
					theta = 2 * Math.PI / s * j;
					r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) / 2;
					x = (int) -Math.round(r * Math.sin(theta));
					y = (int)  Math.round(r * Math.cos(theta));
					shape.addPoint(x, y);
				}
				active = true;
				angle = 0.0;
				deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;
				x = (int) tempX;
				y = (int) tempY;
				deltaX = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
				deltaY = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
				render();
				asteroidIsSmall[i] = true;
				count++;
				asteroidsLeft++;
			}
			i++;
		} while (i < MAX_ROCKS && count < 2);
	}

	public void updateAsteroids() {
		// Move any active asteroids and check for collisions.

		for (Asteroid jayZ: asteroids)
			if (jayZ.active) {
				jayZ.advance();
				jayZ.render();

				// If hit by photon, kill asteroid and advance score. If asteroid is
				// large, make some smaller ones to replace it.

				for (Photon e: photons)
					if (e.active && e.active && e.isColliding(e)) {
						asteroidsLeft--;
						e.active = false;
						e.active = false;
						if (sound)
							Audio.explosionSound.play();
						Explosion.explode(e);
						if (!asteroidIsSmall[0]) {
							score += Framework.BIG_POINTS;
							this.initSmallAsteroids(0);
						}
						else
							score += Framework.SMALL_POINTS;
					}

				// If the ship is not in hyperspace, see if it is hit.

				if (ship.active && Main.hyperCounter <= 0 &&
						jayZ.active && jayZ.isColliding(ship)) {
					if (sound)
						crashSound.play();
					Explosion.explode(ship);
					saucer.stop();
					saucer.stop();
					for (Domain.Missle e:Framework.missles)
						e.stopMissle();
				}
			}
	}

	

}
