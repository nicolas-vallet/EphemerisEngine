package com.nzv.astro.ephemeris;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

/**
 * Photometric relations between stellar magnitudes, following chapter 38 of Jean
 * Meeus' <i>Astronomical Formulae for Calculators</i>: Pogson's relation, the
 * brightness ratio corresponding to a magnitude difference, and the combined
 * magnitude of two or more bodies.
 */
public final class StellarMagnitudes {

	/**
	 * Pogson's ratio: the brightness ratio corresponding to a difference of one
	 * magnitude, namely the fifth root of 100.
	 */
	public static final double POGSON_RATIO = pow(100.0d, 0.2d);

	private StellarMagnitudes() {
		// Utility class: no instances.
	}

	/**
	 * Returns the ratio of brightness corresponding to a magnitude difference
	 * {@code m2 - m1}. A positive difference (body 2 fainter than body 1) yields a
	 * ratio greater than one.
	 *
	 * @param magnitude1 the magnitude of the brighter reference body.
	 * @param magnitude2 the magnitude of the second body.
	 * @return the brightness ratio b1 / b2.
	 */
	public static double brightnessRatio(double magnitude1, double magnitude2) {
		return pow(10.0d, 0.4d * (magnitude2 - magnitude1));
	}

	/**
	 * Returns the magnitude difference {@code m2 - m1} corresponding to a given
	 * brightness ratio b1 / b2 (Pogson's relation).
	 *
	 * @param brightnessRatio the brightness ratio b1 / b2 (must be positive).
	 * @return the magnitude difference m2 - m1.
	 */
	public static double magnitudeDifference(double brightnessRatio) {
		if (brightnessRatio <= 0) {
			throw new IllegalArgumentException("The brightness ratio must be strictly positive.");
		}
		return 2.5d * log10(brightnessRatio);
	}

	/**
	 * Returns the combined magnitude of two bodies seen as a single point source.
	 *
	 * @param magnitude1 the magnitude of the first body.
	 * @param magnitude2 the magnitude of the second body.
	 * @return the combined (total) magnitude.
	 */
	public static double combinedMagnitude(double magnitude1, double magnitude2) {
		return combinedMagnitude(new double[] { magnitude1, magnitude2 });
	}

	/**
	 * Returns the combined magnitude of an arbitrary number of bodies seen as a
	 * single point source.
	 *
	 * @param magnitudes the magnitudes of the individual bodies.
	 * @return the combined (total) magnitude.
	 */
	public static double combinedMagnitude(double... magnitudes) {
		if (magnitudes == null || magnitudes.length == 0) {
			throw new IllegalArgumentException("At least one magnitude must be supplied.");
		}
		double sum = 0;
		for (double magnitude : magnitudes) {
			sum += pow(10.0d, -0.4d * magnitude);
		}
		return -2.5d * log10(sum);
	}
}
