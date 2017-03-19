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

	public static int width;          // Dimensions of the graphics area.
	public static int height;

	Polygon shape = new Polygon();;             // Base sprite shape, centered at the origin (0,0).
	public static boolean active = false;            // Active flag.
	public double  angle = 0.0;             // Current angle of rotation.
	public double  deltaAngle = 0.0;        // Amount to change the rotation angle.
	public double  x = 0.0, y = 0.0;              // Current position on screen.
	public double  deltaX = 0.0, deltaY = 0.0;    // Amount to change the screen position.
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


	public static final int DELAY = 20;             // Milliseconds between screen and
	static final int FPS   =                 // the resulting frame rate.
			Math.round(1000 / DELAY);

	public static final int MAX_SHOTS =  8;          // Maximum number of sprites
	public static final int MAX_ROCKS =  8;          // for photons, asteroids and
	public static final int MAX_SCRAP = 40;          // explosions.

	public static final int SCRAP_COUNT  = 2 * FPS;  // Timer counter starting values
	public static final int HYPER_COUNT  = 3 * FPS;  // calculated using number of
	static final int MISSLE_COUNT = 4 * FPS;  // seconds x frames per second.
	static final int STORM_PAUSE  = 2 * FPS;


	public static final int    MIN_ROCK_SIDES =   6; // Ranges for asteroid shape, size
	public static final int    MAX_ROCK_SIDES =  16; // speed and rotation.
	public static final int    MIN_ROCK_SIZE  =  20;
	public static final int    MAX_ROCK_SIZE  =  40;
	public static final double MIN_ROCK_SPEED =  40.0 / FPS;
	public static final double MAX_ROCK_SPEED = 240.0 / FPS;
	public static final double MAX_ROCK_SPIN  = Math.PI / FPS;

	public static final int MAX_SHIPS = 3;           // Starting number of ships for
	// each game.
	public static final int UFO_PASSES = 3;          // Number of passes for flying
	// saucer per appearance.

	// Domain.Ship's rotation and acceleration rates and maximum speed.


	public static final double SHIP_ANGLE_STEP = Math.PI / FPS;
	public static final double SHIP_SPEED_STEP = 15.0 / FPS;
	public static final double MAX_SHIP_SPEED  = 1.25 * MAX_ROCK_SPEED;

	public static final int FIRE_DELAY = 50;         // Minimum number of milliseconds
	// required between photon shots.


	// Probablility of flying saucer firing a missle during any given frame
	// (other conditions must be met).


	public static final double MISSLE_PROBABILITY = 0.45 / FPS;

	public static final int BIG_POINTS    =  25;     // Points scored for shooting
	public static final int SMALL_POINTS  =  50;     // various objects.
	public static final int UFO_POINTS    = 250;
	public static final int MISSLE_POINTS = 500;


	// Number of points the must be scored to earn a new ship or to cause the
	// flying saucer to appear.


	public static final int NEW_SHIP_POINTS = 5000;
	public static final int NEW_UFO_POINTS  = 2750;


	// Background stars.

	public int     numStars;
	public Point[] stars;


	// Game data.

	public static int score;
	public static int highScore;
	public static int newShipScore;
	public static int newUfoScore;


	// Flags for game state and options.

	public static boolean loaded = false;
	public static boolean paused;
	public static boolean playing;
	public static boolean sound;
	public static boolean detail;




	// Domain.Ship data.


	public static int shipsLeft;       // Number of ships left in game, including current one.
	public static int shipCounter;     // Timer counter for ship explosion.
	public static int hyperCounter;    // Timer counter for hyperspace.


	// Photon data.

	public int   photonIndex;    // Index to next available photon sprite.
	public long  photonTime;     // Time value used to keep firing rate constant.


	// Flying saucer data.

	public static int ufoPassesLeft;    // Counter for number of flying saucer passes.
	public int ufoCounter;       // Timer counter used to track each flying saucer pass.



	// Missle data.


	public int missleCounter;    // Counter for life of missle.


	public static ArrayList<Photon> photons = new ArrayList<>();
	public static ArrayList<Missle> missles = new ArrayList<>();


	// Asteroid data.

	public boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
	public static int       asteroidsCounter;                            // Break-time counter.
	public double    asteroidsSpeed;                              // Asteroid speed.
	public static int       asteroidsLeft;                               // Number of active asteroids.

	// Explosion data.


	public int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
	public int   explosionIndex;                         // Next available explosion sprite.


	// Off screen image.

	Dimension offDimension;
	Image     offImage;
	Graphics  offGraphics;


	// Data for the screen font.


	public Font font      = new Font("Helvetica", Font.BOLD, 12);
	public FontMetrics fm = getFontMetrics(font); // TODO: 16/03/2017 fix
	public int fontWidth  = fm.getMaxAdvance();
	public int fontHeight = fm.getHeight();


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
}

