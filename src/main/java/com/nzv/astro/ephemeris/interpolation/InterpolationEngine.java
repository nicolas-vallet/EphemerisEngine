package com.nzv.astro.ephemeris.interpolation;

import java.util.List;

public interface InterpolationEngine {

	/**
	 * Returns an interpolated value of Y for a given X value, given three InterpolationData.
	 * 
	 * @param InterpolationData input1
	 * @param InterpolationData input2
	 * @param InterpolationData input3
	 * @param The X value for which we want the interpolated Y value.
	 * @return the interpolated Y value.
	 * @throws InterpolationException
	 */
	public double interpolate(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, double searchValue) throws InterpolationException;

	/**
	 * Returns an interpolated value of Y for a given X value, given five InterpolationData.
	 * 
	 * @param InterpolationData input1
	 * @param InterpolationData input2
	 * @param InterpolationData input3
	 * @param InterpolationData input4
	 * @param InterpolationData input5
	 * @param The X value for which we want the interpolated Y value.
	 * @return the interpolated Y value.
	 * @throws InterpolationException
	 */
	public double interpolate(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, InterpolationData input4, InterpolationData input5,
			double searchValue) throws InterpolationException;

	/**
	 * Returns a pair X-Y for for which Y reaches a maximum, given three InterpolationData
	 * 
	 * @param InterpolationData input1
	 * @param InterpolationData input2
	 * @param InterpolationData input3
	 * @return the pair X-Y corresponding to the reached maximum.
	 * @throws InterpolationException
	 */
	public InterpolationData findExtremum(InterpolationData input1, InterpolationData input2,
			InterpolationData input3) throws InterpolationException;

	/**
	 * Returns a pair X-Y for for which Y reaches a maximum, given five InterpolationData
	 * 
	 * @param InterpolationData input1
	 * @param InterpolationData input2
	 * @param InterpolationData input3
	 * @return the pair X-Y corresponding to the reached maximum.
	 * @throws InterpolationException
	 */
	public InterpolationData findExtremum(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, InterpolationData input4, InterpolationData input5)
			throws InterpolationException;

	/**
	 * Returns the X value for which Y equals 0, given three InterpolationData.
	 * 
	 * @param InterpolationData input1
	 * @param InterpolationData input2
	 * @param InterpolationData input3
	 * @return the value of X for which Y equals 0.
	 * @throws InterpolationException
	 */
	public double findZero(InterpolationData input1, InterpolationData input2,
			InterpolationData input3) throws InterpolationException;

	/**
	 * Returns the X value for which Y equals 0, given five InterpolationData.
	 * 
	 * @param InterpolationData input1
	 * @param InterpolationData input2
	 * @param InterpolationData input3
	 * @param InterpolationData input4
	 * @param InterpolationData input5
	 * @return the value of X for which Y equals 0.
	 * @throws InterpolationException
	 */
	public double findZero(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, InterpolationData input4, InterpolationData input5)
			throws InterpolationException;

	public double interpolate(List<InterpolationData> samples, double searchValueFor)
			throws InterpolationException;

	public InterpolationData findExtremum(List<InterpolationData> samples)
			throws InterpolationException;

	public double findZero(List<InterpolationData> samples) throws InterpolationException;
}
