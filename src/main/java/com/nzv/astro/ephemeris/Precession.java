package com.nzv.astro.ephemeris;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Rigorous reduction of equatorial coordinates from one equinox/epoch to another
 * (precession), following chapter 14 of Jean Meeus' <i>Astronomical Formulae for
 * Calculators</i>.
 */
public interface Precession {

	/**
	 * Reduces equatorial coordinates from a starting equinox to a final equinox,
	 * accounting for the precession of the equinoxes.
	 *
	 * @param coordinates the mean equatorial coordinates (right ascension and
	 *            declination, both in degrees) referred to {@code fromEpoch}.
	 * @param fromEpoch the starting equinox, expressed as a decimal year (for
	 *            example {@code 1950.0}).
	 * @param toEpoch the final equinox, expressed as a decimal year (for example
	 *            {@code 2000.0}).
	 * @return the mean equatorial coordinates referred to {@code toEpoch}, with the
	 *         right ascension normalised to the range [0, 360) degrees.
	 */
	public EquatorialCoordinates precess(EquatorialCoordinates coordinates, double fromEpoch,
			double toEpoch);

	/**
	 * Returns the three precessional angles (zeta, z and theta) used to rotate
	 * equatorial coordinates from the starting equinox to the final equinox.
	 *
	 * @param fromEpoch the starting equinox, expressed as a decimal year.
	 * @param toEpoch the final equinox, expressed as a decimal year.
	 * @return an array {@code {zeta, z, theta}} expressed in degrees.
	 */
	public double[] precessionalAngles(double fromEpoch, double toEpoch);
}
