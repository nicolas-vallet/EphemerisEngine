package com.nzv.astro.ephemeris.orbit;

import static java.lang.Math.sqrt;

/**
 * The classical (Keplerian) orbital elements of a minor planet or periodic
 * comet, referred to a standard equinox, as used by the <i>second method</i> of
 * chapter 25 of Jean Meeus' <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * The element set is {@code a, e, i, omega, Omega} together with a description
 * of the body's position along the orbit at a reference instant: the mean
 * anomaly {@code M0} at an epoch, plus the daily mean motion {@code n}. The mean
 * anomaly at any other instant is then {@code M = M0 + n*(jd - epochJd)}
 * (it increases by {@code n} degrees per day and is zero at perihelion).
 * <p>
 * Angles are in degrees, {@code a} is in astronomical units and {@code n} in
 * degrees per day. The orientation angles {@code i, omega, Omega} are referred
 * to whatever standard equinox the caller works in (typically 1950.0); the
 * engine reduces the Sun's rectangular coordinates to that same equinox, so the
 * resulting right ascension and declination come out referred to it. Keeping the
 * elements and the Sun on the same equinox is the caller's responsibility — the
 * one hard rule of chapter 25's second method.
 *
 * @param semiMajorAxisAU the semimajor axis {@code a}, in astronomical units.
 * @param eccentricity the orbital eccentricity {@code e} (0 &le; e &lt; 1).
 * @param inclinationDegrees the inclination {@code i}, in degrees.
 * @param argumentOfPerihelionDegrees the argument of perihelion {@code omega},
 *            in degrees.
 * @param ascendingNodeLongitudeDegrees the longitude of the ascending node
 *            {@code Omega}, in degrees.
 * @param epochJulianDay the Julian Day of the epoch at which {@code M0} holds.
 * @param meanAnomalyAtEpochDegrees the mean anomaly {@code M0} at the epoch, in
 *            degrees.
 * @param meanMotionDegreesPerDay the daily mean motion {@code n}, in degrees per
 *            day.
 */
public record OrbitalElements(double semiMajorAxisAU, double eccentricity,
		double inclinationDegrees, double argumentOfPerihelionDegrees,
		double ascendingNodeLongitudeDegrees, double epochJulianDay,
		double meanAnomalyAtEpochDegrees, double meanMotionDegreesPerDay) {

	/** Gaussian-derived constant of formula (25.12): n = 0.985609 / (a*sqrt(a)). */
	private static final double MEAN_MOTION_CONSTANT = 0.985609d;

	/**
	 * Mean motion derived from the semimajor axis with formula (25.12),
	 * {@code n = 0.985609 / (a*sqrt(a))}, in degrees per day. Useful when the
	 * orbit is supplied with {@code a} but no explicit {@code n}.
	 *
	 * @param semiMajorAxisAU the semimajor axis, in astronomical units.
	 * @return the mean motion, in degrees per day.
	 */
	public static double meanMotionFromSemiMajorAxis(double semiMajorAxisAU) {
		return MEAN_MOTION_CONSTANT / (semiMajorAxisAU * sqrt(semiMajorAxisAU));
	}

	/**
	 * Semimajor axis derived from the perihelion distance with formula (25.12),
	 * {@code a = q / (1 - e)}.
	 *
	 * @param perihelionDistanceAU the perihelion distance {@code q}, in AU.
	 * @param eccentricity the orbital eccentricity {@code e}.
	 * @return the semimajor axis, in astronomical units.
	 */
	public static double semiMajorAxisFromPerihelionDistance(double perihelionDistanceAU,
			double eccentricity) {
		return perihelionDistanceAU / (1d - eccentricity);
	}

	/**
	 * Builds an element set from a mean anomaly given at an epoch, with an
	 * explicit mean motion.
	 */
	public static OrbitalElements fromMeanAnomalyAtEpoch(double a, double e, double i,
			double omega, double node, double epochJulianDay, double meanAnomalyAtEpoch,
			double meanMotion) {
		return new OrbitalElements(a, e, i, omega, node, epochJulianDay,
				meanAnomalyAtEpoch, meanMotion);
	}

	/**
	 * Builds an element set from a mean anomaly given at an epoch, deriving the
	 * mean motion from the semimajor axis with formula (25.12).
	 */
	public static OrbitalElements fromMeanAnomalyAtEpoch(double a, double e, double i,
			double omega, double node, double epochJulianDay, double meanAnomalyAtEpoch) {
		return new OrbitalElements(a, e, i, omega, node, epochJulianDay,
				meanAnomalyAtEpoch, meanMotionFromSemiMajorAxis(a));
	}

	/**
	 * Builds an element set from a time of perihelion passage. The mean anomaly
	 * is zero at that instant, so the epoch is the perihelion instant and
	 * {@code M0 = 0}.
	 */
	public static OrbitalElements fromPerihelionPassage(double a, double e, double i,
			double omega, double node, double perihelionJulianDay, double meanMotion) {
		return new OrbitalElements(a, e, i, omega, node, perihelionJulianDay, 0d, meanMotion);
	}

	/**
	 * The mean anomaly at the given instant, {@code M = M0 + n*(jd - epochJd)},
	 * normalised to the range [0, 360) degrees.
	 *
	 * @param julianDay the instant, as a Julian Day (Ephemeris Time).
	 * @return the mean anomaly, in degrees, in [0, 360).
	 */
	public double meanAnomalyAt(double julianDay) {
		double m = meanAnomalyAtEpochDegrees
				+ meanMotionDegreesPerDay * (julianDay - epochJulianDay);
		m %= 360d;
		if (m < 0d) {
			m += 360d;
		}
		return m;
	}
}
