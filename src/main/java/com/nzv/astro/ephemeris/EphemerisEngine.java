package com.nzv.astro.ephemeris;


public interface EphemerisEngine {

	/**
	 * Returns the time expressed in julian centuries from 0.5 January 1900 for a given julian day.
	 */
	public double T(double julianDay);

	/**
	 * Returns the local hour angle of an object at a given instant, for an observer at a given
	 * longitude.
	 * 
	 * @param The Greenwich sidereal time expressed in hour, minute, second.
	 * @param The local longitude of the observer expressed in degrees.
	 * @param The object's right ascension expressed in hour, minute, second.
	 * @return the local hour angle expressed in degrees.
	 */
	public double H(double greenwichSiderealTime, double longitude, double rightAscension);

	/**
	 * Returns the mean longitude of the Sun for a given instant.
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return the mean longitude of the Sun as degrees.
	 */
	public double sunMeanLongitude(double T);

	/**
	 * Returns the mean longitude of the Moon for a given instant.
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return the mean longitude of the Moon as degrees.
	 */
	public double moonMeanLongitude(double T);

	/**
	 * Returns the mean anomaly of the Sun for a given instant.
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return the mean anomaly of the Sun as degrees.
	 */
	public double sunMeanAnomaly(double T);

	/**
	 * Returns the mean anomaly of the Moon for a given instant.
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return the mean anomaly of the Moon as degrees.
	 */
	public double moonMeanAnomaly(double T);

	/**
	 * Returns Moon's ascendant node longitude for a given instant.
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return Moon's ascendant node longitude as degrees.
	 */
	public double moonAscendantNodeLongitude(double T);

	/**
	 * Returns Moon's mean elongation for a given instant
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return Moon's mean elongation expressed as degrees.
	 */
	public double moonMeanElongation(double T);

	/**
	 * Return Moon's mean distance to it ascendant node for a given instant.
	 * 
	 * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
	 *            instant.
	 * @return Moon's mean distance to it ascendant node expressed as degrees.
	 */
	public double moonMeanDistanceToAscendantNode(double T);

	/**
	 * Returns the Moon's geocentric longitude (lambda), referred to the mean equinox of date,
	 * following chapter 30 of AFFC.
	 *
	 * @param T the time in Julian centuries from 1900.0.
	 * @return the geocentric longitude in degrees, normalised to [0, 360).
	 */
	public double moonGeocentricLongitude(double T);

	/**
	 * Returns the Moon's geocentric latitude (beta), referred to the mean equinox of date.
	 *
	 * @param T the time in Julian centuries from 1900.0.
	 * @return the geocentric latitude in degrees.
	 */
	public double moonGeocentricLatitude(double T);

	/**
	 * Returns the Moon's equatorial horizontal parallax (pi).
	 *
	 * @param T the time in Julian centuries from 1900.0.
	 * @return the equatorial horizontal parallax in degrees.
	 */
	public double moonEquatorialHorizontalParallaxe(double T);

	/**
	 * Returns the Moon's apparent equatorial coordinates at a given instant: the geocentric
	 * position of date with nutation in longitude applied and rotated to the equator using the
	 * true obliquity of date.
	 *
	 * @param julianDay the considered instant as a Julian day.
	 * @return the apparent equatorial coordinates (right ascension and declination, in degrees).
	 */
	public com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates moonApparentEquatorialCoordinates(
			double julianDay);

	/**
	 * Returns the Moon's geocentric ecliptic coordinates (true longitude and latitude of date).
	 *
	 * @param julianDay the considered instant as a Julian day.
	 * @return the geocentric ecliptic coordinates in degrees.
	 */
	public com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates moonGeocentricEclipticCoordinates(
			double julianDay);

	/**
	 * Returns the distance between the centres of the Earth and the Moon at a given instant,
	 * derived from the equatorial horizontal parallax.
	 *
	 * @param julianDay the considered instant as a Julian day.
	 * @return the Earth-Moon distance in kilometers.
	 */
	public double moonDistanceToEarthInKilometers(double julianDay);

	public double earthDistanceToMoonInKilometers(double moonEquatorialHorizontalParallaxe);

	/**
	 * Returns the nutation in Longitude expressed in seconds for a given instant.
	 * 
	 * @param the considered instant as julian day.
	 * @return the nutation in longitude expressed in seconds.
	 */
	public double getNutationInLongitude(double julianDay);

	/**
	 * Returns the nutation in obliquity expressed in seconds for a given instant.
	 * 
	 * @param the considered instant as julian day.
	 * @return the nutation in obliquity expressed in seconds.
	 */
	public double getNutationInObliquity(double julianDay);

	// ---------------------------------------------------------------------
	// Chapter 18 - Solar Coordinates
	// ---------------------------------------------------------------------

	/**
	 * Returns the mean obliquity of the ecliptic (epsilon zero) for a given
	 * instant, following the polynomial of Meeus' <i>Astronomical Formulae for
	 * Calculators</i>, chapter 18.
	 * 
	 * @param T the time expressed in Julian centuries from 0.5 January 1900.
	 * @return the mean obliquity of the ecliptic, expressed in degrees.
	 */
	public double meanObliquityOfEcliptic(double T);

	/**
	 * Returns the Sun's equation of centre for a given instant (chapter 18).
	 * 
	 * @param T the time expressed in Julian centuries from 0.5 January 1900.
	 * @return the equation of centre, expressed in degrees.
	 */
	public double sunEquationOfCenter(double T);

	/**
	 * Returns the Sun's <b>true</b> geometric longitude for a given instant: the
	 * mean longitude corrected by the equation of centre (chapter 18).
	 * 
	 * @param T the time expressed in Julian centuries from 0.5 January 1900.
	 * @return the Sun's true longitude, expressed in degrees in the range [0, 360).
	 */
	public double sunTrueLongitude(double T);

	/**
	 * Returns the Sun's true anomaly for a given instant: the mean anomaly
	 * corrected by the equation of centre (chapter 18).
	 * 
	 * @param T the time expressed in Julian centuries from 0.5 January 1900.
	 * @return the Sun's true anomaly, expressed in degrees in the range [0, 360).
	 */
	public double sunTrueAnomaly(double T);

	/**
	 * Returns the Sun's <b>apparent</b> longitude for a given instant: the true
	 * longitude corrected for nutation and aberration (chapter 18).
	 * 
	 * @param T the time expressed in Julian centuries from 0.5 January 1900.
	 * @return the Sun's apparent longitude, expressed in degrees in the range [0, 360).
	 */
	public double sunApparentLongitude(double T);

	/**
	 * Returns the Sun's radius vector (Earth-Sun distance) for a given instant,
	 * expressed in astronomical units (chapter 18).
	 * 
	 * @param T the time expressed in Julian centuries from 0.5 January 1900.
	 * @return the Sun's radius vector, expressed in astronomical units.
	 */
	public double sunRadiusVector(double T);

	/**
	 * Returns the Sun's apparent geocentric equatorial coordinates (right
	 * ascension and declination) for a given instant (chapter 18). The right
	 * ascension is expressed in <b>degrees</b> in the range [0, 360); divide by 15
	 * to obtain hours.
	 * 
	 * @param julianDay the considered instant as Julian day.
	 * @return the Sun's apparent equatorial coordinates (right ascension in
	 *         degrees, declination in degrees).
	 */
	public com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates sunApparentEquatorialCoordinates(
			double julianDay);


	// =====================================================================
	// Chapter 31 - Illuminated Fraction of the Moon's Disk
	// =====================================================================

	/**
	 * Returns the Moon's phase angle (the Sun-Earth angular distance as seen
	 * from the Moon) for a given instant, following chapter 31 (formulae 31.2
	 * and 31.3). The Sun's <b>true</b> longitude is used when forming the
	 * elongation, so that nutation and aberration are not double-counted.
	 *
	 * @param julianDay the considered instant as Julian day.
	 * @return the Moon's phase angle, expressed in degrees.
	 */
	public double moonPhaseAngle(double julianDay);

	/**
	 * Returns the illuminated fraction of the Moon's disk as seen from the
	 * centre of the Earth for a given instant (chapter 31, formula 31.1),
	 * derived from the high-accuracy phase angle. The result lies in [0, 1].
	 *
	 * @param julianDay the considered instant as Julian day.
	 * @return the illuminated fraction of the Moon's disk, in the range [0, 1].
	 */
	public double moonIlluminatedFraction(double julianDay);

	/**
	 * Returns a lower-accuracy estimate of the Moon's phase angle, obtained
	 * from the mean elements alone and neglecting the Moon's latitude (chapter
	 * 31, formula 31.4).
	 *
	 * @param julianDay the considered instant as Julian day.
	 * @return the approximate phase angle, expressed in degrees.
	 */
	public double moonPhaseAngleApproximate(double julianDay);

	// =====================================================================
	// Chapter 13 - Position Angle of the Moon's Bright Limb
	// =====================================================================

	/**
	 * Returns the position angle of the midpoint of the Moon's bright limb,
	 * reckoned eastward from the north point of the disk (chapter 13). The
	 * angle is computed from the apparent equatorial coordinates of the Sun and
	 * the Moon.
	 *
	 * @param julianDay the considered instant as Julian day.
	 * @return the position angle of the bright limb, expressed in degrees in the
	 *         range [0, 360).
	 */
	public double moonBrightLimbPositionAngle(double julianDay);

	// =====================================================================
	// Chapter 19 - Rectangular Coordinates of the Sun
	// =====================================================================

	/**
	 * Returns the Sun's geocentric rectangular equatorial coordinates X, Y, Z
	 * referred to the mean equator and equinox of date (chapter 19, formula
	 * 19.1), expressed in astronomical units. The Sun's latitude is neglected.
	 *
	 * @param julianDay the considered instant as Julian day.
	 * @return an array {@code {X, Y, Z}} in astronomical units, referred to the
	 *         mean equinox of date.
	 */
	public double[] sunRectangularEquatorialCoordinates(double julianDay);

	/**
	 * Returns the Sun's geocentric rectangular equatorial coordinates X, Y, Z
	 * reduced to a chosen standard equinox (chapter 19, formula 19.2), expressed
	 * in astronomical units. The reduction reuses the chapter-14 precessional
	 * angles.
	 *
	 * @param julianDay the considered instant as Julian day.
	 * @param targetEquinox the equinox to which the coordinates are referred,
	 *            expressed as a decimal year (for example {@code 1950.0}).
	 * @return an array {@code {X, Y, Z}} in astronomical units, referred to
	 *         {@code targetEquinox}.
	 */
	public double[] sunRectangularEquatorialCoordinates(double julianDay, double targetEquinox);

	// =====================================================================
	// Chapter 20 - Equinoxes and Solstices
	// =====================================================================

	/**
	 * Returns the instant of the given equinox or solstice of the given year as
	 * a Julian Ephemeris Day (chapter 20). The instant is found iteratively from
	 * the Sun's <b>apparent</b> longitude (formulae 20.1 and 20.2).
	 *
	 * @param year the calendar year.
	 * @param season the equinox or solstice sought.
	 * @return the instant of the event, expressed as a Julian Ephemeris Day.
	 */
	public double equinoxSolsticeJulianDay(int year, Season season);

	// =====================================================================
	// Chapter 32 - Phases of the Moon
	// =====================================================================

	/**
	 * Returns the instant of the principal Moon phase nearest the given time, as
	 * a Julian Ephemeris Day (chapter 32). The mean phase given by formula
	 * (32.1) is corrected to the true phase by the periodic terms of formula
	 * (32.4) for New and Full Moon, or (32.5) for First and Last Quarter.
	 *
	 * @param year the approximate time of the phase, as a decimal year (for
	 *            example {@code 1977.13} for mid-February 1977).
	 * @param phase the phase sought.
	 * @return the instant of the true phase, expressed as a Julian Ephemeris Day.
	 */
	public double moonPhaseJulianDay(double year, MoonPhase phase);

}
