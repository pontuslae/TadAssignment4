package Foundation;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class Audio extends Applet implements Runnable {
	
	
	public static boolean sound;
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

	public static boolean thrustersPlaying;
	public static boolean saucerPlaying;
	public static boolean misslePlaying;

	public void loadSounds() {

		// Load all sound clips by playing and immediately stopping them. Update
		// counter and total for display.

		try {
			crashSound = getAudioClip(new URL(getCodeBase(), "crash.au"));
			clipTotal++;
			explosionSound = getAudioClip(new URL(getCodeBase(), "explosion.au"));
			clipTotal++;
			fireSound = getAudioClip(new URL(getCodeBase(), "fire.au"));
			clipTotal++;
			missleSound = getAudioClip(new URL(getCodeBase(), "missle.au"));
			clipTotal++;
			saucerSound = getAudioClip(new URL(getCodeBase(), "saucer.au"));
			clipTotal++;
			thrustersSound = getAudioClip(new URL(getCodeBase(), "thrusters.au"));
			clipTotal++;
			warpSound = getAudioClip(new URL(getCodeBase(), "warp.au"));
			clipTotal++;

		} catch (MalformedURLException e) {
		}

		try {

			crashSound.play();

			crashSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
			explosionSound.play();
			explosionSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
			fireSound.play();
			fireSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
			missleSound.play();
			missleSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
			saucerSound.play();
			saucerSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
			thrustersSound.play();
			thrustersSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
			warpSound.play();
			warpSound.stop();
			clipsLoaded++;
			repaint();
			Thread.currentThread().sleep(DELAY);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
