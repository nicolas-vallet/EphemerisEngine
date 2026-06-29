package com.nzv.astro.ephemeris.orbit;

import com.nzv.astro.ephemeris.EphemerisEngine;

/**
 * Geocentric ephemeris of a comet in a parabolic orbit, following chapter 26 of
 * Jean Meeus' <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * The procedure is the second method of chapter 25 with the Kepler solve
 * replaced by Barker's equation (the {@code e = 1} case): from the perihelion
 * distance and the time since perihelion it forms the coefficient {@code W}
 * (26.1), solves {@code s^3 + 3s - W = 0} for {@code s = tan(v/2)}
 * ({@link BarkerEquation}), obtains the true anomaly and radius vector (26.2),
 * and then finishes through the very same geocentric reduction as chapter 25 —
 * the per-orbit Gaussian constants (25.13), the heliocentric rectangular
 * equatorial coordinates (25.14) and the combination with the Sun (25.15),
 * shared via {@link GeocentricReduction}.
 * <p>
 * The position returned is the comet's <i>geometric</i> position, exactly as the
 * chapter's worked example 26.a produces it; nutation and aberration (chapter
 * 16) are left to the caller, and the light-time correction can be applied with
 * {@link #lightTimeCorrected}. Right ascension and declination are referred to
 * the elements' standard equinox. All angles are in degrees, distances in AU.
 */
public final class ParabolicMotion {

	/**
	 * The light-time constant (25.10): {@code tau = 0.0057756 * Delta} days.
	 * Shared with elliptic motion.
	 */
	public static final double LIGHT_TIME_CONSTANT = EllipticMotion.LIGHT_TIME_CONSTANT;

	private ParabolicMotion() {
		// Utility class: no instances.
	}

	/**
	 * Full geocentric position of the comet from its elements, the time since
	 * perihelion, the Sun's geocentric rectangular equatorial coordinates
	 * {@code X, Y, Z} referred to the same standard equinox, and the obliquity of
	 * that equinox.
	 *
	 * @param elements the parabolic elements.
	 * @param daysSincePerihelion the time since perihelion {@code t - T}, in days.
	 * @param sunX the Sun's geocentric equatorial X, in AU.
	 * @param sunY the Sun's geocentric equatorial Y, in AU.
	 * @param sunZ the Sun's geocentric equatorial Z, in AU.
	 * @param obliquityDegrees the obliquity of the elements' equinox, in degrees.
	 * @return the geocentric position.
	 */
	public static OrbitPosition geocentricPosition(ParabolicElements elements,
			double daysSincePerihelion, double sunX, double sunY, double sunZ,
			double obliquityDegrees) {
		double w = BarkerEquation.wCoefficient(elements.perihelionDistanceAU(),
				daysSincePerihelion);
		double s = BarkerEquation.solveTrueAnomalyParameter(w);
		double[] vr = BarkerEquation.trueAnomalyAndRadius(s, elements.perihelionDistanceAU());
		double v = vr[0];
		double r = vr[1];
		double[] gaussian = EllipticMotion.gaussianConstants(
				elements.ascendingNodeLongitudeDegrees(), elements.inclinationDegrees(),
				obliquityDegrees);
		double[] xyz = GeocentricReduction.heliocentricRectangular(gaussian,
				elements.argumentOfPerihelionDegrees(), v, r);
		return GeocentricReduction.reduce(xyz[0], xyz[1], xyz[2], r, sunX, sunY, sunZ);
	}

	/**
	 * Convenience overload that takes the Sun's rectangular coordinates from the
	 * engine (chapter 19), reduced to the given standard equinox, and uses the
	 * elements' time of perihelion passage for the instant.
	 *
	 * @param engine the ephemeris engine (chapter 19 Sun).
	 * @param julianDay the instant, as a Julian Day (Ephemeris Time).
	 * @param elements the parabolic elements (referred to {@code standardEquinox}).
	 * @param standardEquinox the standard equinox of the elements (e.g. 1950.0).
	 * @param obliquityDegrees the obliquity of that equinox.
	 */
	public static OrbitPosition geocentricPosition(EphemerisEngine engine, double julianDay,
			ParabolicElements elements, double standardEquinox, double obliquityDegrees) {
		double[] sun = engine.sunRectangularEquatorialCoordinates(julianDay, standardEquinox);
		return geocentricPosition(elements, elements.daysSincePerihelion(julianDay), sun[0], sun[1],
				sun[2], obliquityDegrees);
	}

	/**
	 * As {@link #geocentricPosition(EphemerisEngine, double, ParabolicElements,
	 * double, double)} but with the light-time correction (25.10) applied: the
	 * comet is taken at the retarded instant {@code t - tau} while the Sun is kept
	 * at {@code t}, iterating until {@code tau} is stable. The result is the
	 * astrometric place; nutation and aberration (chapter 16) are not applied.
	 *
	 * @param engine the ephemeris engine (chapter 19 Sun).
	 * @param julianDay the instant of observation {@code t}, as a Julian Day (ET).
	 * @param elements the parabolic elements (referred to {@code standardEquinox}).
	 * @param standardEquinox the standard equinox of the elements.
	 * @param obliquityDegrees the obliquity of that equinox.
	 */
	public static OrbitPosition lightTimeCorrected(EphemerisEngine engine, double julianDay,
			ParabolicElements elements, double standardEquinox, double obliquityDegrees) {
		double[] sun = engine.sunRectangularEquatorialCoordinates(julianDay, standardEquinox);
		double tau = 0d;
		OrbitPosition position = null;
		for (int iteration = 0; iteration < 10; iteration++) {
			double daysSincePerihelion = elements.daysSincePerihelion(julianDay - tau);
			position = geocentricPosition(elements, daysSincePerihelion, sun[0], sun[1], sun[2],
					obliquityDegrees);
			double newTau = LIGHT_TIME_CONSTANT * position.distanceToEarthAU();
			if (Math.abs(newTau - tau) < 1e-9d) {
				tau = newTau;
				break;
			}
			tau = newTau;
		}
		return position;
	}
}
