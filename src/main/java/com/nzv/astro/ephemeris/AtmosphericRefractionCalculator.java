package com.nzv.astro.ephemeris;

public interface AtmosphericRefractionCalculator {

    /**
     * Returns the true elevation of an object the apparent elevation we know.
     *
     * @param apparentElevation apparent elevation of the object expressed in degrees.
     * @return the true elevation of an object expressed in degrees.
     */
    double getTrueElevation(double apparentElevation);

    /**
     * Returns the apparent elevation of an object the true elevation we know.
     *
     * @param trueElevation true elevation of the object expressed in degrees.
     * @return the apparent elevation of an object expressed in degrees.
     */
    double getApparentElevation(double trueElevation);

    /**
     * Returns the true elevation of an object the apparent elevation we know.
     *
     * @param apparentElevation apparent elevation of the object expressed in degrees.
     * @param temperature       ambient temperature expressed in celsius.
     * @param pressure          atmospheric pressure expressed in millibars.
     * @return the true elevation of an object expressed in degrees.
     */
    double getTrueElevation(double apparentElevation, double temperature, double pressure);

    /**
     * Returns the apparent elevation of an object the true elevation we know.
     *
     * @param trueElevation apparent elevation of the object expressed in degrees.
     * @param temperature   ambient temperature expressed in celsius.
     * @param pressure      atmospheric pressure expressed in millibars.
     * @return the true elevation of an object expressed in degrees.
     */
    double getApparentElevation(double trueElevation, double temperature, double pressure);
}
