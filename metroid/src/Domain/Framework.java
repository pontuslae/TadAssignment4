package Domain;

import Application.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

public class Framework extends Applet {

	// Fields:

	static int width;          // Dimensions of the graphics area.
	static int height;

	Polygon shape = new Polygon();;             // Base sprite shape, centered at the origin (0,0).
	public static boolean active = false;            // Active flag.
	double  angle = 0.0;             // Current angle of rotation.
	double  deltaAngle = 0.0;        // Amount to change the rotation angle.
	double  x = 0.0, y = 0.0;              // Current position on screen.
	double  deltaX = 0.0, deltaY = 0.0;    // Amount to change the screen position.
	public Polygon sprite = new Polygon();            // Final location and shape of sprite after
	// applying rotation and translation to get screen
	// position. Used for drawing on the screen and in
	// detecting collisions.

	// Copyright information.


	public String copyName = "Asteroids";
	public String copyVers = "Version 1.3";
	public String copyInfo = "Copyright 1998-2001 by Mike Hall";
	public static String copyLink = "http://www.brainjar.com";
	public String copyText = copyName + '\n' + copyVers + '\n'
			+ copyInfo + '\n' + copyLink;


	// Thread control variables.

	Thread loadThread;
	Thread loopThread;

	// Constants


	static final int DELAY = 20;             // Milliseconds between screen and
	static final int FPS   =                 // the resulting frame rate.
			Math.round(1000 / DELAY);

	static final int MAX_SHOTS =  8;          // Maximum number of sprites
	public static final int MAX_ROCKS =  8;          // for photons, asteroids and
	public static final int MAX_SCRAP = 40;          // explosions.

	public static final int SCRAP_COUNT  = 2 * FPS;  // Timer counter starting values
	public static final int HYPER_COUNT  = 3 * FPS;  // calculated using number of
	static final int MISSLE_COUNT = 4 * FPS;  // seconds x frames per second.
	static final int STORM_PAUSE  = 2 * FPS;

	static final int    MIN_ROCK_SIDES =   6; // Ranges for asteroid shape, size
	static final int    MAX_ROCK_SIDES =  16; // speed and rotation.
	static final int    MIN_ROCK_SIZE  =  20;
	static final int    MAX_ROCK_SIZE  =  40;
	static final double MIN_ROCK_SPEED =  40.0 / FPS;
	static final double MAX_ROCK_SPEED = 240.0 / FPS;
	static final double MAX_ROCK_SPIN  = Math.PI / FPS;

	static final int MAX_SHIPS = 3;           // Starting number of ships for
	// each game.
	static final int UFO_PASSES = 3;          // Number of passes for flying
	// saucer per appearance.

	// Domain.Ship's rotation and acceleration rates and maximum speed.


	static final double SHIP_ANGLE_STEP = Math.PI / FPS;
	static final double SHIP_SPEED_STEP = 15.0 / FPS;
	static final double MAX_SHIP_SPEED  = 1.25 * MAX_ROCK_SPEED;

	static final int FIRE_DELAY = 50;         // Minimum number of milliseconds
	// required between photon shots.


	// Probablility of flying saucer firing a missle during any given frame
	// (other conditions must be met).


	static final double MISSLE_PROBABILITY = 0.45 / FPS;

	static final int BIG_POINTS    =  25;     // Points scored for shooting
	static final int SMALL_POINTS  =  50;     // various objects.
	static final int UFO_POINTS    = 250;
	static final int MISSLE_POINTS = 500;


	// Number of points the must be scored to earn a new ship or to cause the
	// flying saucer to appear.


	static final int NEW_SHIP_POINTS = 5000;
	static final int NEW_UFO_POINTS  = 2750;


	// Background stars.

	int     numStars;
	Point[] stars;


	// Game data.

	int score;
	int highScore;
	int newShipScore;
	int newUfoScore;


	// Flags for game state and options.

	boolean loaded = false;
	boolean paused;
	boolean playing;
	boolean sound;
	boolean detail;




	// Domain.Ship data.


	public int shipsLeft;       // Number of ships left in game, including current one.
	int shipCounter;     // Timer counter for ship explosion.
	public static int hyperCounter;    // Timer counter for hyperspace.


	// Photon data.

	int   photonIndex;    // Index to next available photon sprite.
	long  photonTime;     // Time value used to keep firing rate constant.


	// Flying saucer data.

	int ufoPassesLeft;    // Counter for number of flying saucer passes.
	int ufoCounter;       // Timer counter used to track each flying saucer pass.



	// Missle data.


	public int missleCounter;    // Counter for life of missle.


	// Asteroid data.

	boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
	int       asteroidsCounter;                            // Break-time counter.
	double    asteroidsSpeed;                              // Asteroid speed.
	int       asteroidsLeft;                               // Number of active asteroids.

	// Explosion data.


	public int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
	int   explosionIndex;                         // Next available explosion sprite.

	// Sound clips.

	AudioClip crashSound;
	AudioClip explosionSound;
	AudioClip fireSound;
	AudioClip missleSound;
	AudioClip saucerSound;
	AudioClip thrustersSound;
	AudioClip warpSound;

	// Flags for looping sound clips.

	boolean thrustersPlaying;
	boolean saucerPlaying;
	boolean misslePlaying;


	// Counter and total used to track the loading of the sound clips.

	public int clipTotal   = 0;
	public int clipsLoaded = 0;


	// Off screen image.

	Dimension offDimension;
	Image     offImage;
	Graphics  offGraphics;




	// Data for the screen font.


	Font font      = new Font("Helvetica", Font.BOLD, 12);
	FontMetrics fm = getFontMetrics(font); // TODO: 16/03/2017 fix
	int fontWidth  = fm.getMaxAdvance();
	int fontHeight = fm.getHeight();


	public void update(Graphics g) {

		paint(g);
	}

	public void render() {

		int i;

		// Render the sprite's shape and location by rotating it's base shape and
		// moving it to it's proper screen position.

		this.sprite = new Polygon();
		for (i = 0; i < this.shape.npoints; i++)
			this.sprite.addPoint((int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) + this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
					(int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) + (int) Math.round(this.y) + height / 2);
	}

	public boolean advance() {

		boolean wrapped;

		// Update the rotation and position of the sprite based on the delta
		// values. If the sprite moves off the edge of the screen, it is wrapped
		// around to the other side and TRUE is returnd.

		this.angle += this.deltaAngle;
		if (this.angle < 0)
			this.angle += 2 * Math.PI;
		if (this.angle > 2 * Math.PI)
			this.angle -= 2 * Math.PI;
		wrapped = false;
		this.x += this.deltaX;
		if (this.x < -width / 2) {
			this.x += width;
			wrapped = true;
		}
		if (this.x > width / 2) {
			this.x -= width;
			wrapped = true;
		}
		this.y -= this.deltaY;
		if (this.y < -height / 2) {
			this.y += height;
			wrapped = true;
		}
		if (this.y > height / 2) {
			this.y -= height;
			wrapped = true;
		}

		return wrapped;
	}

	public boolean isColliding(Framework s) {

		int i;
		// Determine if one sprite overlaps with another, i.e., if any vertice
		// of one sprite lands inside the other.

		for (i = 0; i < s.sprite.npoints; i++)
			if (this.sprite.contains(s.sprite.xpoints[i], s.sprite.ypoints[i]))
				return true;
		for (i = 0; i < this.sprite.npoints; i++)
			if (s.sprite.contains(this.sprite.xpoints[i], this.sprite.ypoints[i]))
				return true;
		return false;
	}

	public void explode(Framework s) {

		int c, i, j;
		int cx, cy;

		// Create sprites for explosion animation. The each individual line segment
		// of the given sprite is used to create a new sprite that will move
		// outward  from the sprite's original position with a random rotation.

		s.render();
		c = 2;
		if (Main.detail || s.sprite.npoints < 6)
			c = 1;
		for (i = 0; i < s.sprite.npoints; i += c) {
			explosionIndex++;
			if (explosionIndex >= MAX_SCRAP)
				explosionIndex = 0;
			Asteroid.explosions[explosionIndex].active = true;
			Asteroid.explosions[explosionIndex].shape = new Polygon();
			j = i + 1;
			if (j >= s.sprite.npoints)
				j -= s.sprite.npoints;
			cx = (int) ((s.shape.xpoints[i] + s.shape.xpoints[j]) / 2);
			cy = (int) ((s.shape.ypoints[i] + s.shape.ypoints[j]) / 2);
			Asteroid.explosions[explosionIndex].shape.addPoint(
					s.shape.xpoints[i] - cx,
					s.shape.ypoints[i] - cy);
			Asteroid.explosions[explosionIndex].shape.addPoint(
					s.shape.xpoints[j] - cx,
					s.shape.ypoints[j] - cy);
			Asteroid.explosions[explosionIndex].x = s.x + cx;
			Asteroid.explosions[explosionIndex].y = s.y + cy;
			Asteroid.explosions[explosionIndex].angle = s.angle;
			Asteroid.explosions[explosionIndex].deltaAngle = 4 * (Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);
			Asteroid.explosions[explosionIndex].deltaX = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.deltaX) / 2;
			Asteroid.explosions[explosionIndex].deltaY = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.deltaY) / 2;
			explosionCounter[explosionIndex] = SCRAP_COUNT;
		}
	}


}

