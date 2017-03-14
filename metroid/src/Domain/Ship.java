package Domain;

import java.awt.Color;

public class Ship extends UFO{
	 static final int MAX_SHIPS = 3;           // Starting number of ships for
     // each game.
static final int UFO_PASSES = 3;          // Number of passes for flying
     // saucer per appearance.

// Ship's rotation and acceleration rates and maximum speed.

static final double SHIP_ANGLE_STEP = Math.PI / FPS;  //TODO extending FrameWork
static final double SHIP_SPEED_STEP = 15.0 / FPS;
static final double MAX_SHIP_SPEED  = 1.25 * MAX_ROCK_SPEED;

static final int FIRE_DELAY = 50;         // Minimum number of milliseconds
     // required between photon shots.


	  public void initShip() {

		    // Reset the ship sprite at the center of the screen.

		    ship.active = true;
		    ship.angle = 0.0;
		    ship.deltaAngle = 0.0;
		    ship.x = 0.0;
		    ship.y = 0.0;
		    ship.deltaX = 0.0;
		    ship.deltaY = 0.0;
		    ship.render();

		    // Initialize thruster sprites.

		    fwdThruster.x = ship.x;
		    fwdThruster.y = ship.y;
		    fwdThruster.angle = ship.angle;
		    fwdThruster.render();
		    revThruster.x = ship.x;
		    revThruster.y = ship.y;
		    revThruster.angle = ship.angle;
		    revThruster.render();

		    if (loaded)
		      thrustersSound.stop();
		    thrustersPlaying = false;
		    hyperCounter = 0;
		  }
	  public void updateShip() {

		    double dx, dy, speed;

		    if (!playing)
		      return;

		    // Rotate the ship if left or right cursor key is down.

		    if (left) {
		      ship.angle += SHIP_ANGLE_STEP;
		      if (ship.angle > 2 * Math.PI)
		        ship.angle -= 2 * Math.PI;
		    }
		    if (right) {
		      ship.angle -= SHIP_ANGLE_STEP;
		      if (ship.angle < 0)
		        ship.angle += 2 * Math.PI;
		    }

		    // Fire thrusters if up or down cursor key is down.

		    dx = SHIP_SPEED_STEP * -Math.sin(ship.angle);
		    dy = SHIP_SPEED_STEP *  Math.cos(ship.angle);
		    if (up) {
		      ship.deltaX += dx;
		      ship.deltaY += dy;
		    }
		    if (down) {
		        ship.deltaX -= dx;
		        ship.deltaY -= dy;
		    }

		    // Don't let ship go past the speed limit.

		    if (up || down) {
		      speed = Math.sqrt(ship.deltaX * ship.deltaX + ship.deltaY * ship.deltaY);
		      if (speed > MAX_SHIP_SPEED) {
		        dx = MAX_SHIP_SPEED * -Math.sin(ship.angle);
		        dy = MAX_SHIP_SPEED *  Math.cos(ship.angle);
		        if (up)
		          ship.deltaX = dx;
		        else
		          ship.deltaX = -dx;
		        if (up)
		          ship.deltaY = dy;
		        else
		          ship.deltaY = -dy;
		      }
		      public void stopShip() {

		    	    ship.active = false;
		    	    shipCounter = SCRAP_COUNT;
		    	    if (shipsLeft > 0)
		    	      shipsLeft--;
		    	    if (loaded)
		    	      thrustersSound.stop();
		    	    thrustersPlaying = false;
		    	  }

		    }
		    
		 // Draw the ship, counter is used to fade color to white on hyperspace.

		    c = 255 - (255 / HYPER_COUNT) * hyperCounter;
		    if (ship.active) {
		      if (detail && hyperCounter == 0) {
		        offGraphics.setColor(Color.black);
		        offGraphics.fillPolygon(ship.sprite);
		      }
		      offGraphics.setColor(new Color(c, c, c));
		      offGraphics.drawPolygon(ship.sprite);
		      offGraphics.drawLine(ship.sprite.xpoints[ship.sprite.npoints - 1], ship.sprite.ypoints[ship.sprite.npoints - 1],
		                           ship.sprite.xpoints[0], ship.sprite.ypoints[0]);

		      // Draw thruster exhaust if thrusters are on. Do it randomly to get a
		      // flicker effect.

		      if (!paused && detail && Math.random() < 0.5) {
		        if (up) {
		          offGraphics.drawPolygon(fwdThruster.sprite);
		          offGraphics.drawLine(fwdThruster.sprite.xpoints[fwdThruster.sprite.npoints - 1], fwdThruster.sprite.ypoints[fwdThruster.sprite.npoints - 1],
		                               fwdThruster.sprite.xpoints[0], fwdThruster.sprite.ypoints[0]);
		        }
		        if (down) {
		          offGraphics.drawPolygon(revThruster.sprite);
		          offGraphics.drawLine(revThruster.sprite.xpoints[revThruster.sprite.npoints - 1], revThruster.sprite.ypoints[revThruster.sprite.npoints - 1],
		                               revThruster.sprite.xpoints[0], revThruster.sprite.ypoints[0]);
		        }
		      }
		    }

}
