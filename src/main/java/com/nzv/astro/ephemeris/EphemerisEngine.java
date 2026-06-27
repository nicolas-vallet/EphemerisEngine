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

	// public double moonGeocentricLongitude(double T);

	// public double moonGeocentricLatitude(double T);

	// public double moonEquatorialHorizontalParallaxe(double T);

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

}
