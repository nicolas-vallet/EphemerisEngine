package com.nzv.astro.ephemeris.orbit;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * The geocentric-reduction tail shared by the second method of chapter 25
 * (elliptic motion) and chapter 26 (parabolic motion). Both chapters, once they
 * have the body's true anomaly {@code v} and radius vector {@code r}, finish in
 * exactly the same way: form the heliocentric rectangular equatorial coordinates
 * with the per-orbit Gaussian constants (25.14), add the Sun's geocentric
 * rectangular equatorial coordinates and read off the right ascension,
 * declination and Earth distance (25.15), then the elongation and phase angle
 * (page 120). This class holds that common machinery so neither chapter
 * duplicates it.
 * <p>
 * Package-private: an internal helper of the {@code orbit} package, not part of
 * the public API.
 */
final class GeocentricReduction {

	private GeocentricReduction() {
		// Utility class: no instances.
	}

	/**
	 * Heliocentric rectangular equatorial coordinates of the body, formula
	 * (25.14), from the per-orbit Gaussian constants, the argument of perihelion,
	 * the true anomaly and the radius vector.
	 *
	 * @param gaussian the constants {@code {a, b, c, A, B, C}} from
	 *            {@link EllipticMotion#gaussianConstants}.
	 * @param argPerihelionDegrees the argument of perihelion {@code omega}, deg.
	 * @param trueAnomalyDegrees the true anomaly {@code v}, in degrees.
	 * @param radiusVectorAU the radius vector {@code r}, in AU.
	 * @return {@code {x, y, z}} in AU.
	 */
	static double[] heliocentricRectangular(double[] gaussian, double argPerihelionDegrees,
			double trueAnomalyDegrees, double radiusVectorAU) {
		double a = gaussian[0];
		double b = gaussian[1];
		double c = gaussian[2];
		double A = gaussian[3];
		double B = gaussian[4];
		double C = gaussian[5];
		double x = radiusVectorAU * a * sin(toRadians(A + argPerihelionDegrees + trueAnomalyDegrees));
		double y = radiusVectorAU * b * sin(toRadians(B + argPerihelionDegrees + trueAnomalyDegrees));
		double z = radiusVectorAU * c * sin(toRadians(C + argPerihelionDegrees + trueAnomalyDegrees));
		return new double[] { x, y, z };
	}

	/**
	 * Combines the body's heliocentric rectangular equatorial coordinates with
	 * the Sun's geocentric rectangular equatorial coordinates (referred to the
	 * same equinox) to obtain the geocentric position, formula (25.15), plus the
	 * elongation and phase angle of page 120.
	 *
	 * @param x body heliocentric equatorial X, in AU.
	 * @param y body heliocentric equatorial Y, in AU.
	 * @param z body heliocentric equatorial Z, in AU.
	 * @param radiusVectorAU the radius vector {@code r}, in AU.
	 * @param sunX the Sun's geocentric equatorial X, in AU.
	 * @param sunY the Sun's geocentric equatorial Y, in AU.
	 * @param sunZ the Sun's geocentric equatorial Z, in AU.
	 * @return the geocentric position, with right ascension/declination referred
	 *         to the equinox of the inputs.
	 */
	static OrbitPosition reduce(double x, double y, double z, double radiusVectorAU, double sunX,
			double sunY, double sunZ) {
		double xi = sunX + x;
		double eta = sunY + y;
		double zeta = sunZ + z;
		double alpha = normalizeDegrees(toDegrees(atan2(eta, xi)));                // (25.15)
		double delta = sqrt(xi * xi + eta * eta + zeta * zeta);
		double dec = toDegrees(asin(zeta / delta));
		double sunR = sqrt(sunX * sunX + sunY * sunY + sunZ * sunZ);
		double elongation = elongation(radiusVectorAU, delta, sunR);
		double phase = phaseAngle(radiusVectorAU, delta, sunR);
		return new OrbitPosition(new EquatorialCoordinates(alpha, dec), radiusVectorAU, delta,
				elongation, phase);
	}

	/** Elongation psi from the Sun, page 120: cos psi = (R^2 + D^2 - r^2)/(2 R D). */
	static double elongation(double r, double delta, double sunR) {
		return toDegrees(acos(clamp((sunR * sunR + delta * delta - r * r) / (2d * sunR * delta))));
	}

	/** Phase angle (Sun-body-Earth), page 120: cos beta = (r^2 + D^2 - R^2)/(2 r D). */
	static double phaseAngle(double r, double delta, double sunR) {
		return toDegrees(acos(clamp((r * r + delta * delta - sunR * sunR) / (2d * r * delta))));
	}

	/** Normalises an angle in degrees to the range [0, 360). */
	static double normalizeDegrees(double degrees) {
		double d = degrees % 360d;
		if (d < 0d) {
			d += 360d;
		}
		return d;
	}

	private static double clamp(double cosine) {
		if (cosine > 1d) {
			return 1d;
		}
		if (cosine < -1d) {
			return -1d;
		}
		return cosine;
	}
}
