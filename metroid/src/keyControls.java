import java.awt.event.KeyEvent;
import java.net.URL;

public class keyControls {

		// Key flags.
		public static boolean left  = false;
		public static boolean right = false;
		public static boolean up    = false;
		public static boolean down  = false;

	  public void keyPressed(KeyEvent e) {

		    char c;

		    // Check if any cursor keys have been pressed and set flags.

		    if (e.getKeyCode() == KeyEvent.VK_LEFT)
		      left = true;
		    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		      right = true;
		    if (e.getKeyCode() == KeyEvent.VK_UP)
		      up = true;
		    if (e.getKeyCode() == KeyEvent.VK_DOWN)
		      down = true;

		    if ((up || down) && ship.active && !thrustersPlaying) {
		      if (sound && !paused)
		        thrustersSound.loop();
		      thrustersPlaying = true;
		    }

		    // Spacebar: fire a photon and start its counter.

		    if (e.getKeyChar() == ' ' && ship.active) {
		      if (sound & !paused)
		        fireSound.play();
		      photonTime = System.currentTimeMillis();
		      photonIndex++;
		      if (photonIndex >= Framework.MAX_SHOTS)
		        photonIndex = 0;
		      photons[photonIndex].active = true;
		      photons[photonIndex].x = ship.x;
		      photons[photonIndex].y = ship.y;
		      photons[photonIndex].deltaX = 2 * Framework.MAX_ROCK_SPEED * -Math.sin(ship.angle);
		      photons[photonIndex].deltaY = 2 * Framework.MAX_ROCK_SPEED *  Math.cos(ship.angle);
		    }

		    // Allow upper or lower case characters for remaining keys.

		    c = Character.toLowerCase(e.getKeyChar());

		    // 'H' key: warp ship into hyperspace by moving to a random location and
		    // starting counter.

		    if (c == 'h' && ship.active && Framework.hyperCounter <= 0) {
		      ship.x = Math.random() * AsteroidsSprite.width;
		      ship.y = Math.random() * AsteroidsSprite.height;
		      Framework.hyperCounter = Framework.HYPER_COUNT;
		      if (sound & !paused)
		        warpSound.play();
		    }

		    // 'P' key: toggle pause mode and start or stop any active looping sound
		    // clips.

		    if (c == 'p') {
		      if (paused) {
		        if (sound && misslePlaying)
		          missleSound.loop();
		        if (sound && saucerPlaying)
		          saucerSound.loop();
		        if (sound && thrustersPlaying)
		          thrustersSound.loop();
		      }
		      else {
		        if (misslePlaying)
		          missleSound.stop();
		        if (saucerPlaying)
		          saucerSound.stop();
		        if (thrustersPlaying)
		          thrustersSound.stop();
		      }
		      paused = !paused;
		    }

		    // 'M' key: toggle sound on or off and stop any looping sound clips.

		    if (c == 'm' && loaded) {
		      if (sound) {
		        crashSound.stop();
		        explosionSound.stop();
		        fireSound.stop();
		        missleSound.stop();
		        saucerSound.stop();
		        thrustersSound.stop();
		        warpSound.stop();
		      }
		      else {
		        if (misslePlaying && !paused)
		          missleSound.loop();
		        if (saucerPlaying && !paused)
		          saucerSound.loop();
		        if (thrustersPlaying && !paused)
		          thrustersSound.loop();
		      }
		      sound = !sound;
		    }

		    // 'D' key: toggle graphics detail on or off.

		    if (c == 'd')
		      detail = !detail;

		    // 'S' key: start the game, if not already in progress.

		    if (c == 's' && loaded && !playing)
		      initGame();

		    // 'HOME' key: jump to web site (undocumented).

		    if (e.getKeyCode() == KeyEvent.VK_HOME)
		      try {
		        getAppletContext().showDocument(new URL(Framework.copyLink));
		      }
		      catch (Exception excp) {}
		  }
	
	  
	  public void keyReleased(KeyEvent e) {

		    // Check if any cursor keys where released and set flags.

		    if (e.getKeyCode() == KeyEvent.VK_LEFT)
		      left = false;
		    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		      right = false;
		    if (e.getKeyCode() == KeyEvent.VK_UP)
		      up = false;
		    if (e.getKeyCode() == KeyEvent.VK_DOWN)
		      down = false;

		    if (!up && !down && thrustersPlaying) {
		      thrustersSound.stop();
		      thrustersPlaying = false;
		    }
		  }
	
	  public void keyTyped(KeyEvent e) {}

	  
	 }
	
	
}
