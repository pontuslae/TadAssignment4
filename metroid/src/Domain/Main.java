package Domain;

public class Main implements Runnable {

	  public void run() {

		    int i, j;
		    long startTime;

		    // Lower this thread's priority and get the current time.

		    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		    startTime = System.currentTimeMillis();

		    // Run thread for loading sounds.

		    if (!loaded && Thread.currentThread() == loadThread) {
		      loadSounds();
		      loaded = true;
		      loadThread.stop();
		    }

		    // This is the main loop.

		    while (Thread.currentThread() == loopThread) {

		      if (!paused) {

		        // Move and process all sprites.

		        updateShip();
		        updatePhotons();
		        updateUfo();
		        updateMissle();
		        updateAsteroids();
		        updateExplosions();

		        // Check the score and advance high score, add a new ship or start the
		        // flying saucer as necessary.

		        if (score > highScore)
		          highScore = score;
		        if (score > newShipScore) {
		          newShipScore += NEW_SHIP_POINTS;
		          shipsLeft++;
		        }
		        if (playing && score > newUfoScore && !ufo.active) {
		          newUfoScore += NEW_UFO_POINTS;
		          ufoPassesLeft = UFO_PASSES;
		          initUfo();
		        }

		        // If all asteroids have been destroyed create a new batch.

		        if (asteroidsLeft <= 0)
		            if (--asteroidsCounter <= 0)
		              initAsteroids();
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
			    if (loadThread != null) {
			      loadThread.stop();
			      loadThread = null;
			    }
			  }
			  public void endGame() {

				    // Stop ship, flying saucer, guided missle and associated sounds.

				    playing = false;
				    stopShip();
				    stopUfo();
				    stopMissle();
				  }
	
}
