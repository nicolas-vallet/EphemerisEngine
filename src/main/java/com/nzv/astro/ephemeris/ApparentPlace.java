package com.nzv.astro.ephemeris;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Apparent place of a star, following chapter 16 of Jean Meeus'
 * <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * The apparent place is obtained from a mean catalogue position by composing, in
 * order: the star's own proper motion, the precession of the equinoxes
 * (chapter 14), and finally the corrections for nutation (chapter 15) and annual
 * aberration. The Sun's true longitude required by the aberration terms comes
 * from the solar coordinates of chapter 18.
 * <p>
 * <b>Units and conventions.</b> Right ascension and declination are expressed in
 * <b>degrees</b> throughout, consistent with the rest of the library's
 * coordinate-transformation code. Proper motions are supplied in
 * <b>arcseconds per year</b> for both coordinates, as plain coordinate rates
 * (the right-ascension rate is <i>not</i> multiplied by cos&delta;); a proper
 * motion quoted by Meeus in seconds of time per year must therefore be
 * multiplied by 15 before being passed in.
 */
public interface ApparentPlace {

	/**
	 * Returns the apparent place of a star at a given instant, starting from its
	 * mean catalogue position and applying proper motion, precession, nutation and
	 * annual aberration.
	 *
	 * @param meanCatalogue the mean equatorial coordinates (right ascension and
	 *            declination, both in degrees) referred to {@code catalogueEpoch}.
	 * @param catalogueEpoch the equinox/epoch of the catalogue position, expressed
	 *            as a decimal year (for example {@code 2000.0}).
	 * @param muAlpha the annual proper motion in right ascension, in arcseconds per
	 *            year, as a coordinate rate (not multiplied by cos&delta;).
	 * @param muDelta the annual proper motion in declination, in arcseconds per
	 *            year.
	 * @param julianDay the instant for which the apparent place is wanted, as a
	 *            Julian day.
	 * @return the apparent equatorial coordinates at {@code julianDay}, with the
	 *         right ascension normalised to the range [0, 360) degrees.
	 */
	public EquatorialCoordinates apparentPlace(EquatorialCoordinates meanCatalogue,
			double catalogueEpoch, double muAlpha, double muDelta, double julianDay);

	/**
	 * Applies proper motion to a mean position over a given number of years.
	 *
	 * @param mean the mean equatorial coordinates (degrees).
	 * @param muAlpha the annual proper motion in right ascension (arcsec/year,
	 *            coordinate rate).
	 * @param muDelta the annual proper motion in declination (arcsec/year).
	 * @param years the elapsed time in years (may be negative).
	 * @return the position carried forward by proper motion, right ascension
	 *         normalised to [0, 360) degrees.
	 */
	public EquatorialCoordinates applyProperMotion(EquatorialCoordinates mean, double muAlpha,
			double muDelta, double years);

	/**
	 * Returns the correction to be added to a mean place to account for nutation
	 * (chapter 15), evaluated at the given instant.
	 *
	 * @param mean the mean equatorial coordinates of date (degrees).
	 * @param julianDay the considered instant as a Julian day.
	 * @return an array {@code {deltaAlpha, deltaDelta}} in <b>degrees</b>.
	 */
	public double[] nutationCorrection(EquatorialCoordinates mean, double julianDay);

	/**
	 * Returns the correction to be added to a mean place to account for the annual
	 * aberration, including the terms in the eccentricity of the Earth's orbit,
	 * evaluated at the given instant.
	 *
	 * @param mean the mean equatorial coordinates of date (degrees).
	 * @param julianDay the considered instant as a Julian day.
	 * @return an array {@code {deltaAlpha, deltaDelta}} in <b>degrees</b>.
	 */
	public double[] aberrationCorrection(EquatorialCoordinates mean, double julianDay);
}
