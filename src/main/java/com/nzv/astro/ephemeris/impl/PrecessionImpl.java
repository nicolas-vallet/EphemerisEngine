package com.nzv.astro.ephemeris.impl;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.Precession;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Implementation of the rigorous precession reduction of chapter 14 of Jean
 * Meeus' <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * The precessional angles zeta, z and theta are polynomials in the starting
 * epoch and the elapsed time, both expressed in Julian centuries reckoned from
 * the fundamental epoch 1900.0 used throughout this library.
 */
public class PrecessionImpl implements Precession {

	@Override
	public double[] precessionalAngles(double fromEpoch, double toEpoch) {
		// T is the starting epoch measured in Julian centuries from 1900.0;
		// t is the interval from the starting epoch to the final epoch, in
		// Julian centuries.
		double T = (fromEpoch - 1900.0d) / 100.0d;
		double t = (toEpoch - fromEpoch) / 100.0d;

		// Angles in seconds of arc (Meeus, chapter 14).
		double zeta = (2304.250d + 1.396d * T) * t + 0.302d * t * t + 0.018d * t * t * t;
		double z = (2304.250d + 1.396d * T) * t + 1.093d * t * t + 0.0192d * t * t * t;
		double theta = (2004.682d - 0.853d * T) * t - 0.426d * t * t - 0.042d * t * t * t;

		// Convert from seconds of arc to degrees.
		return new double[] { zeta / 3600.0d, z / 3600.0d, theta / 3600.0d };
	}

	@Override
	public EquatorialCoordinates precess(EquatorialCoordinates coordinates, double fromEpoch,
			double toEpoch) {
		double[] angles = precessionalAngles(fromEpoch, toEpoch);
		double zeta = angles[0];
		double z = angles[1];
		double theta = angles[2];

		double alpha0 = coordinates.getRightAscension();
		double delta0 = coordinates.getDeclinaison();

		double A = cos(toRadians(delta0)) * sin(toRadians(alpha0 + zeta));
		double B = cos(toRadians(theta)) * cos(toRadians(delta0)) * cos(toRadians(alpha0 + zeta))
				- sin(toRadians(theta)) * sin(toRadians(delta0));
		double C = sin(toRadians(theta)) * cos(toRadians(delta0)) * cos(toRadians(alpha0 + zeta))
				+ cos(toRadians(theta)) * sin(toRadians(delta0));

		double alpha = toDegrees(atan2(A, B)) + z;
		// Use atan2 of (C, sqrt(A^2+B^2)) rather than asin(C) for full accuracy
		// near the poles, where cos(delta) becomes small.
		double delta = toDegrees(atan2(C, sqrt(A * A + B * B)));

		double normalizedAlpha = alpha % 360d;
		if (normalizedAlpha < 0) {
			normalizedAlpha += 360d;
		}
		return new EquatorialCoordinates(normalizedAlpha, delta);
	}
}
