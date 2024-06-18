package util;

import java.util.Random;

public class RandomWalk {
	public static final Random random = new Random();

	public static float step(float value, float step) {
		return value + Math.abs(step) * (float)Math.round(Math.signum(random.nextDouble() - 0.5D));
	}
}
