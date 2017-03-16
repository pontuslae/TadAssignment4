package Domain;


/**
 * Created by pontu on 10/03/2017.
 */

public class Projectile extends Framework {

	public static final int MAX_SHOTS =  8;          // Maximum number of sprites

	public Projectile() {
	}
}

class Photon extends Projectile {

	static int max_photons = MAX_SHOTS;

	public void initPhotons() {

		int i;

		for (i = 0; i < MAX_SHOTS; i++)
			active = false;
		photonIndex = 0;
	}

	public void updatePhotons() {
		int i;
		// Move any active photons. Stop it when its counter has expired.
		for (i = 0; i < MAX_SHOTS; i++)
			if (active) {
				if (advance())
					render();
				else
					active = false;
			}
	}
}

class Missle extends Projectile {

	boolean misslePlaying = true;

	private Ship ship; // Target

	public Missle(Ship ship){
		this.ship = ship;
		render();

		if (sound)
			missleSound.loop();
	}

	public void updateMissle() {

		int i;

		// Move the guided missle and check for collision with ship or photon. Stop
		// it when its counter has expired.

		if (active) {
			if (--missleCounter <= 0)
				stopMissle();
			else {
				guideMissle();
				advance();
				render();
				for (i = 0; i < MAX_SHOTS; i++)
					if (active && isColliding(this)) {
						if (sound)
							crashSound.play();
						explode(this);
						stopMissle();
						score += Framework.MISSLE_POINTS;
					}
				if (active && ship.active &&
						hyperCounter <= 0 && ship.isColliding(this)) {
					if (sound)
						crashSound.play();
					ship.explode(ship);
					ship.stop();
					stopMissle();
				}
			}
		}
	}

	public void guideMissle() {

		double dx, dy, angle;

		if (!ship.active || hyperCounter > 0)
			return;

		// Find the angle needed to hit the ship.

		dx = ship.x - x;
		dy = ship.y - y;
		if (dx == 0 && dy == 0) angle = 0;
		if (dx == 0) angle = dy < 0 ? -Math.PI / 2: Math.PI / 2;
		else {
			angle = Math.atan(Math.abs(dy / dx));
			if (dy > 0)
				angle = -angle;
			if (dx < 0)
				angle = Math.PI - angle;
		}

		// Adjust angle for screen coordinates.
		angle = angle - Math.PI / 2;

		// Change the missle's angle so that it points toward the ship.
		deltaX = 0.75 * Framework.MAX_ROCK_SPEED * -Math.sin(angle);
		deltaY = 0.75 * Framework.MAX_ROCK_SPEED *  Math.cos(angle);
	}

	public void stopMissle() {

		active = false;
		missleCounter = 0;
		if (loaded)
			missleSound.stop();
		misslePlaying = false;
	}

}