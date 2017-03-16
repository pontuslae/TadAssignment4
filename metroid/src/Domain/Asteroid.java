package Domain;

import Domain.Main;

import java.applet.AudioClip;
import java.awt.Polygon;

public class Asteroid {

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
	  boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
	  int       asteroidsCounter;                            // Break-time counter.
	  double    asteroidsSpeed;                              // Asteroid speed.
	  int       asteroidsLeft;                               // Number of active asteroids.

	  // Explosion data.

	  int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
	  int   explosionIndex;                         // Next available explosion sprite.
	  
	  //Arrays
	  Framework[] photons    = new Framework[MAX_SHOTS];
	  Framework[] asteroids  = new Framework[MAX_ROCKS];
	  Framework[] explosions = new Framework[MAX_SCRAP];
	  // Sound clips.

	  AudioClip crashSound;
	  AudioClip explosionSound;


	  public void initAsteroids() {

	    int i, j;
	    int s;
	    double theta, r;
	    int x, y;

	    // Create random shapes, positions and movements for each asteroid.

	    for (i = 0; i < MAX_ROCKS; i++) {

	      // Create a jagged shape for the asteroid and give it a random rotation.

	      asteroids[i].shape = new Polygon();
	      s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
	      for (j = 0; j < s; j ++) {
	        theta = 2 * Math.PI / s * j;
	        r = MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE));
	        x = (int) -Math.round(r * Math.sin(theta));
	        y = (int)  Math.round(r * Math.cos(theta));
	        asteroids[i].shape.addPoint(x, y);
	      }
	      asteroids[i].active = true;
	      asteroids[i].angle = 0.0;
	      asteroids[i].deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;

	      // Place the asteroid at one edge of the screen.

	      if (Math.random() < 0.5) {
	        asteroids[i].x = -Framework.width / 2;
	        if (Math.random() < 0.5)
	          asteroids[i].x = Framework.width / 2;
	        asteroids[i].y = Math.random() * Framework.height;
	      }
	      else {
	        asteroids[i].x = Math.random() * Framework.width;
	        asteroids[i].y = -Framework.height / 2;
	        if (Math.random() < 0.5)
	          asteroids[i].y = Framework.height / 2;
	      }

	      // Set a random motion for the asteroid.

	      asteroids[i].deltaX = Math.random() * asteroidsSpeed;
	      if (Math.random() < 0.5)
	        asteroids[i].deltaX = -asteroids[i].deltaX;
	      asteroids[i].deltaY = Math.random() * asteroidsSpeed;
	      if (Math.random() < 0.5)
	        asteroids[i].deltaY = -asteroids[i].deltaY;

	      asteroids[i].render();
	      asteroidIsSmall[i] = false;
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
	    tempX = asteroids[n].x;
	    tempY = asteroids[n].y;
	    do {
	      if (!asteroids[i].active) {
	        asteroids[i].shape = new Polygon();
	        s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
	        for (j = 0; j < s; j ++) {
	          theta = 2 * Math.PI / s * j;
	          r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) / 2;
	          x = (int) -Math.round(r * Math.sin(theta));
	          y = (int)  Math.round(r * Math.cos(theta));
	          asteroids[i].shape.addPoint(x, y);
	        }
	        asteroids[i].active = true;
	        asteroids[i].angle = 0.0;
	        asteroids[i].deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;
	        asteroids[i].x = tempX;
	        asteroids[i].y = tempY;
	        asteroids[i].deltaX = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
	        asteroids[i].deltaY = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
	        asteroids[i].render();
	        asteroidIsSmall[i] = true;
	        count++;
	        asteroidsLeft++;
	      }
	      i++;
	    } while (i < MAX_ROCKS && count < 2);
	  }

	  public void updateAsteroids() {

	    int i, j;

	    // Move any active asteroids and check for collisions.

	    for (i = 0; i < MAX_ROCKS; i++)
	      if (asteroids[i].active) {
	        asteroids[i].advance();
	        asteroids[i].render();

	        // If hit by photon, kill asteroid and advance score. If asteroid is
	        // large, make some smaller ones to replace it.

	        for (j = 0; j < MAX_SHOTS; j++)
	          if (photons[j].active && asteroids[i].active && asteroids[i].isColliding(photons[j])) {
	            asteroidsLeft--;
	            asteroids[i].active = false;
	            photons[j].active = false;
	            if (Framework.sound)
	              explosionSound.play();
	            explode(asteroids[i]);
	            if (!asteroidIsSmall[i]) {
	              Framework.score += Framework.BIG_POINTS;
	              initSmallAsteroids(i);
	            }
	            else
	              Framework.score += Framework.SMALL_POINTS;
	          }

	        // If the ship is not in hyperspace, see if it is hit.

	        if (UFO.ship.active && Framework.hyperCounter <= 0 &&
	            asteroids[i].active && asteroids[i].isColliding(UFO.ship)) {
	          if (Framework.sound)
	            crashSound.play();
	          explode(Framework.ship);
	          UFO.stopShip();
	          UFO.stopUfo();
	          UFO.stopMissle();
	        }
	    }
	  }

	  public void initExplosions() {

	    int i;

	    for (i = 0; i < MAX_SCRAP; i++) {
	      explosions[i].shape = new Polygon();
	      explosions[i].active = false;
	      explosionCounter[i] = 0;
	    }
	    explosionIndex = 0;
	  }

	  public void explode(Framework s) {

	    int c, i, j;
	    int cx, cy;

	    // Create sprites for explosion animation. The each individual line segment
	    // of the given sprite is used to create a new sprite that will move
	    // outward  from the sprite's original position with a random rotation.

	    s.render();
	    c = 2;
	    if (Framework.detail || s.sprite.npoints < 6)
	      c = 1;
	    for (i = 0; i < s.sprite.npoints; i += c) {
	      explosionIndex++;
	      if (explosionIndex >= MAX_SCRAP)
	        explosionIndex = 0;
	      explosions[explosionIndex].active = true;
	      explosions[explosionIndex].shape = new Polygon();
	      j = i + 1;
	      if (j >= s.sprite.npoints)
	        j -= s.sprite.npoints;
	      cx = (int) ((s.shape.xpoints[i] + s.shape.xpoints[j]) / 2);
	      cy = (int) ((s.shape.ypoints[i] + s.shape.ypoints[j]) / 2);
	      explosions[explosionIndex].shape.addPoint(
	        s.shape.xpoints[i] - cx,
	        s.shape.ypoints[i] - cy);
	      explosions[explosionIndex].shape.addPoint(
	        s.shape.xpoints[j] - cx,
	        s.shape.ypoints[j] - cy);
	      explosions[explosionIndex].x = s.x + cx;
	      explosions[explosionIndex].y = s.y + cy;
	      explosions[explosionIndex].angle = s.angle;
	      explosions[explosionIndex].deltaAngle = 4 * (Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);
	      explosions[explosionIndex].deltaX = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.deltaX) / 2;
	      explosions[explosionIndex].deltaY = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.deltaY) / 2;
	      explosionCounter[explosionIndex] = SCRAP_COUNT;
	    }
	  }

	  public void updateExplosions() {

	    int i;
	    // Move any active explosion debris. Stop explosion when its counter has
	    // expired.

	    for (i = 0; i < MAX_SCRAP; i++)
	      if (explosions[i].active) {
	        explosions[i].advance();
	        explosions[i].render();
	        if (--explosionCounter[i] < 0)
	          explosions[i].active = false;
	      }
	  }

}
