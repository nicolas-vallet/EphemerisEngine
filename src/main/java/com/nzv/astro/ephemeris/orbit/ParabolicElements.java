package com.nzv.astro.ephemeris.orbit;

/**
 * The orbital elements of a comet moving in a parabolic orbit, referred to a
 * standard equinox, as used by chapter 26 of Jean Meeus' <i>Astronomical
 * Formulae for Calculators</i>.
 * <p>
 * A parabolic orbit has eccentricity exactly 1, so there is no semimajor axis
 * and no mean motion: the body's place along the orbit is fixed entirely by the
 * perihelion distance {@code q} and the time of perihelion passage {@code T}.
 * The orientation angles {@code i, omega, Omega} are referred to whatever
 * standard equinox the caller works in (typically 1950.0); as with the second
 * method of chapter 25, the engine reduces the Sun's coordinates to that same
 * equinox, so the resulting right ascension and declination come out referred to
 * it.
 * <p>
 * Angles are in degrees, {@code q} is in astronomical units, and {@code T} is a
 * Julian Day (Ephemeris Time).
 *
 * @param perihelionDistanceAU the perihelion distance {@code q}, in AU.
 * @param inclinationDegrees the inclination {@code i}, in degrees.
 * @param argumentOfPerihelionDegrees the argument of perihelion {@code omega},
 *            in degrees.
 * @param ascendingNodeLongitudeDegrees the longitude of the ascending node
 *            {@code Omega}, in degrees.
 * @param perihelionPassageJulianDay the time of perihelion passage {@code T}, as
 *            a Julian Day (Ephemeris Time).
 */
public record ParabolicElements(double perihelionDistanceAU, double inclinationDegrees,
		double argumentOfPerihelionDegrees, double ascendingNodeLongitudeDegrees,
		double perihelionPassageJulianDay) {

	/**
	 * The time since perihelion {@code t - T}, in days (negative before
	 * perihelion), for the given instant.
	 *
	 * @param julianDay the instant, as a Julian Day (Ephemeris Time).
	 * @return {@code t - T}, in days.
	 */
	public double daysSincePerihelion(double julianDay) {
		return julianDay - perihelionPassageJulianDay;
	}
}
