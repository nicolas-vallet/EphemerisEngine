package com.nzv.astro.ephemeris;

public interface AtmosphericRefractionCalculator {

	/**
	 * Returns the true elevation of an object the apparent elevation we know.
	 * 
	 * @param the apparent elevation of the object expressed in degrees.
	 * @return the true elevation of an object expressed in degrees.
	 */
	public double getTrueElevation(double apparentElevation);

	/**
	 * Returns the apparent elevation of an object the true elevation we know.
	 * 
	 * @param the true elevation of the object expressed in degrees.
	 * @return the apparent elevation of an object expressed in degrees.
	 */
	public double getApparentElevation(double trueElevation);

	/**
	 * Returns the true elevation of an object the apparent elevation we know.
	 * 
	 * @param the apparent elevation of the object expressed in degrees.
	 * @param the ambient temperature expressed in celsius.
	 * @param the atmospheric pressure expressed in millibars.
	 * @return the true elevation of an object expressed in degrees.
	 */
	public double getTrueElevation(double apparentElevation, double temperature, double pressure);

	/**
	 * Returns the apparent elevation of an object the true elevation we know.
	 * 
	 * @param the apparent elevation of the object expressed in degrees.
	 * @param the ambient temperature expressed in celsius.
	 * @param the atmospheric pressure expressed in millibars.
	 * @return the true elevation of an object expressed in degrees.
	 */
	public double getApparentElevation(double trueElevation, double temperature, double pressure);

}
