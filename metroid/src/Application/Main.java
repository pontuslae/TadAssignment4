package Application;

import Domain.*;
import Foundation.Audio;
import UI.*;

import java.applet.Applet;

import static Domain.Framework.*;
import static Domain.Photon.initPhotons;


public class Main extends Applet implements Runnable {
	public static Ship ship = new Ship();
	public static Saucer saucer = new Saucer();
	public static Missle missle = new Missle(ship);
	Audio audio = new Audio();
	// Flags for game state and options.

	public static boolean loaded = false;
	public static boolean paused;
	public static boolean playing;
	//public static boolean sound;

	// Game data.

	public static int score;
	public static int highScore;
	public static int newShipScore;
	public static int newUfoScore;
	
	public static int shipsLeft;       // Number of ships left in game, including current one.
	public int shipCounter;     // Timer counter for ship explosion.
	public static int hyperCounter;    // Timer counter for hyperspace.
	
	// Flying saucer data.

	public int ufoPassesLeft;    // Counter for number of flying saucer passes.
	public int ufoCounter;       // Timer counter used to track each flying saucer pass.

	// Thread control variables.
	public static Thread loadThread;
	public static Thread loopThread;

	public void run() {

		int i, j;
		long startTime;

		// Lower this thread's priority and get the current time.

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		startTime = System.currentTimeMillis();

		// Run thread for loading sounds.

		if (!loaded && Thread.currentThread() == loadThread) {
			audio.loadSounds();
			loaded = true;
			loadThread.stop();
		}

		// This is the main loop.

		while (Thread.currentThread() == loopThread) {

			if (!paused) {

				// Move and process all sprites.

				ship.update();

				/// :)))))))))))))))))))))))))))))))))))))))))))))
				for (Domain.Photon e:Framework.photons)
					e.updatePhotons();
				for (Domain.Missle e:Framework.missles)
					e.updateMissle();

				saucer.update();
				Asteroid.updateAsteroids();
				Explosion.updateExplosions();

				// Check the score and advance high score, add a new ship or start the
				// flying saucer as necessary.

				if (score > highScore)
					highScore = score;
				if (score > newShipScore) {
					newShipScore += NEW_SHIP_POINTS;
					shipsLeft++;
				}
				if (playing && score > newUfoScore && !saucer.active) {
					newUfoScore += NEW_UFO_POINTS;
					ufoPassesLeft = UFO_PASSES;
					saucer.init();
				}

				// If all asteroids have been destroyed create a new batch.

				if (Asteroid.asteroidsLeft <= 0)
					if (--Asteroid.asteroidsCounter <= 0)
						Asteroid.initAsteroids();
			}

			// Update the screen and set the timer for the next loop.

				repaint();
			try {
				startTime += DELAY;
				Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}
	public void start() {

		if (loopThread == null) {
			loopThread = new Thread(this);
			loopThread.start();
		}
		if (!loaded && loadThread == null) {
			loadThread = new Thread(this);
			loadThread.start();
		}
	}

	public void stop() {

		if (loopThread != null) {
			loopThread.stop();
			loopThread = null;
		}
	}

	public static void initGame() {
		// Initialize game data and sprites.

		score = 0;
		shipsLeft = MAX_SHIPS;
		// asteroidsSpeed = MIN_ROCK_SPEED;
		newShipScore = NEW_SHIP_POINTS;
		newUfoScore = NEW_UFO_POINTS;
		ship.init();
		initPhotons();
		saucer.stop();
		for (Domain.Missle e:Framework.missles)
			e.stopMissle();
		// initAsteroids();// TODO: 19/03/2017 @Anthony
		Explosion.initExplosions(); // Not mine
		playing = true;
		paused = false;
		//photonTime = System.currentTimeMillis();
	}


	public static void endGame() {

		// Stop ship, flying saucer, guided missle and associated sounds.

		playing = false;
		ship.stop();
		saucer.stop();
		for (Domain.Missle e:Framework.missles)
			e.stopMissle();
	}

}
