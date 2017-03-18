package Domain;

/**
 * Created by tim on 3/16/2017.
 */
public class Saucer extends UFO {

    public static Framework ufo;

    @Override
    public void draw(){
        // Create shape for the flying saucer.

        ufo = new Framework();
        ufo.shape.addPoint(-15, 0);
        ufo.shape.addPoint(-10, -5);
        ufo.shape.addPoint(-5, -5);
        ufo.shape.addPoint(-5, -8);
        ufo.shape.addPoint(5, -8);
        ufo.shape.addPoint(5, -5);
        ufo.shape.addPoint(10, -5);
        ufo.shape.addPoint(15, 0);
        ufo.shape.addPoint(10, 5);
        ufo.shape.addPoint(-10, 5);
    }

    @Override
    public void init() {

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        ufo.active = true;
        ufo.x = -Framework.width / 2;
        ufo.y = Math.random() * 2 * Framework.height - Framework.height;
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = Framework.MAX_ROCK_SPEED / 2 + Math.random() * (Framework.MAX_ROCK_SPEED / 2);
        ufo.deltaX = speed * -Math.sin(angle);
        ufo.deltaY = speed *  Math.cos(angle);
        if (Math.random() < 0.5) {
            ufo.x = Framework.width / 2;
            ufo.deltaX = -ufo.deltaX;
        }
        if (ufo.y > 0)
            ufo.deltaY = ufo.deltaY;
        ufo.render();
        saucerPlaying = true;
        if (sound)
            saucerSound.loop();
        counter = (int) Math.abs(Framework.width / ufo.deltaX);
    }

    @Override
    public void update() {

        int i, d;
        boolean wrapped;

        // Move the flying saucer and check for collision with a photon. Stop it
        // when its counter has expired.

        if (ufo.active) {
            if (--counter <= 0) {
                if (--ufoPassesLeft > 0)
                    ufo.init();
                else
                    ufo.stop();
            }
            if (ufo.active) {
                ufo.advance();
                ufo.render();
                for (i = 0; i < Framework.MAX_SHOTS; i++)
                    if (Asteroid.photons[i].active && ufo.isColliding(Asteroid.photons[i])) {
                        if (sound)
                            crashSound.play();
                        explode(ufo);
                        ufo.stop();
                        score += Framework.UFO_POINTS;
                    }

                // On occassion, fire a missle at the ship if the saucer is not too
                // close to it.

                d = (int) Math.max(Math.abs(ufo.x - ship.x), Math.abs(ufo.y - ship.y));
                if (ship.active && hyperCounter <= 0 &&
                        ufo.active && !missle.active &&
                        d > Framework.MAX_ROCK_SPEED * FPS / 2 &&
                        Math.random() < Framework.MISSLE_PROBABILITY)
                    initMissle();
            }
        }
    }

    @Override
    public void stop() {

        ufo.active = false;
        counter = 0;
        ufoPassesLeft = 0;
        if (loaded)
            saucerSound.stop();
        saucerPlaying = false;
    }
}
