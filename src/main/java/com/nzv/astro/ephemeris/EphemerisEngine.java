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
     * @param greenwichSiderealTime Greenwich sidereal time expressed in hour, minute, second.
     * @param longitude             local longitude of the observer expressed in degrees.
     * @param rightAscension        object's right ascension expressed in hour, minute, second.
     * @return the local hour angle expressed in degrees.
     */
    public double H(double greenwichSiderealTime, double longitude, double rightAscension);

    /**
     * Returns the mean longitude of the Sun for a given instant.
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
     * @return the mean longitude of the Sun as degrees.
     */
    public double sunMeanLongitude(double T);

    /**
     * Returns the mean longitude of the Moon for a given instant.
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
     * @return the mean longitude of the Moon as degrees.
     */
    public double moonMeanLongitude(double T);

    /**
     * Returns the mean anomaly of the Sun for a given instant.
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
     * @return the mean anomaly of the Sun as degrees.
     */
    public double sunMeanAnomaly(double T);

    /**
     * Returns the mean anomaly of the Moon for a given instant.
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
     * @return the mean anomaly of the Moon as degrees.
     */
    public double moonMeanAnomaly(double T);

    /**
     * Returns Moon's ascendant node longitude for a given instant.
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
     * @return Moon's ascendant node longitude as degrees.
     */
    public double moonAscendantNodeLongitude(double T);

    /**
     * Returns Moon's mean elongation for a given instant
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
     * @return Moon's mean elongation expressed as degrees.
     */
    public double moonMeanElongation(double T);

    /**
     * Return Moon's mean distance to it ascendant node for a given instant.
     *
     * @param T the time expressed in julian centuries from 0.5 January 1900 to the considered
     *          instant.
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
     * @param julianDay considered instant as julian day.
     * @return the nutation in longitude expressed in seconds.
     */
    public double getNutationInLongitude(double julianDay);

    /**
     * Returns the nutation in obliquity expressed in seconds for a given instant.
     *
     * @param julianDay considered instant as julian day.
     * @return the nutation in obliquity expressed in seconds.
     */
    public double getNutationInObliquity(double julianDay);

}
