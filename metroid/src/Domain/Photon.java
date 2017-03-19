package Domain;

public class Photon extends Projectile {

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

	public static void initPhotons() {
		for (Domain.Photon e:Framework.photons)
			e.active = false;
	}
}
