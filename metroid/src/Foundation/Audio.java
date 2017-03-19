package Foundation;

import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class Audio {
	static boolean sound;
	int DELAY;
	public static int clipTotal = 0;
	public static int clipsLoaded = 0;
	public static AudioClip crashSound;
	public static AudioClip explosionSound;
	public static AudioClip fireSound;
	public static AudioClip missleSound;
	public static AudioClip saucerSound;
	public static AudioClip warpSound;
	public static AudioClip thrustersSound;

	// Sound clips.

	// Flags for looping sound clips.

	static boolean thrustersPlaying;
	static boolean saucerPlaying;
	static boolean misslePlaying;

	public static void loadSounds() {

		// Load all sound clips by playing and immediately stopping them. Update
		// counter and total for display.

		try {
			crashSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "crash.au"));
			clipTotal++;
			explosionSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "explosion.au"));
			clipTotal++;
			fireSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "fire.au"));
			clipTotal++;
			missleSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "missle.au"));
			clipTotal++;
			saucerSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "saucer.au"));
			clipTotal++;
			thrustersSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "thrusters.au"));
			clipTotal++;
			warpSound = Game.basic.getAudioClip(new URL(Game.basic.getCodeBase(), "warp.au"));
			clipTotal++;

		} catch (MalformedURLException e) {
		}

		try {

			crashSound.play();

			crashSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
			explosionSound.play();
			explosionSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
			fireSound.play();
			fireSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
			missleSound.play();
			missleSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
			saucerSound.play();
			saucerSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
			thrustersSound.play();
			thrustersSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
			warpSound.play();
			warpSound.stop();
			clipsLoaded++;
			Game.basic.repaint();
			Thread.currentThread().sleep(Game.DELAY);
		} catch (InterruptedException e) {
		}
	}

}
