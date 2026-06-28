package com.nzv.astro.ephemeris.lunar;

/**
 * Contract for a model that yields the Moon's geocentric position from a periodic-series theory,
 * independent of the theory's source or epoch. The AFFC chapter-30 abridged theory is the first
 * implementation ({@code affc-1900}); a higher-precision model (e.g. a truncated ELP-2000) can be
 * added later behind the same interface.
 * <p>
 * All returned positions are referred to the mean equinox of date. Longitude and latitude are in
 * degrees; the horizontal parallax is in degrees. Time is the number of Julian centuries from the
 * model's own epoch (for {@code affc-1900}, from 1900.0 / JD 2415020.0).
 *
 * @see TableDrivenMoonModel
 * @see MoonModels
 */
public interface MoonPositionModel {

	/** @return the Moon's geocentric longitude &lambda;, in degrees, normalised to [0, 360). */
	double geocentricLongitude(double T);

	/** @return the Moon's geocentric latitude &beta;, in degrees. */
	double geocentricLatitude(double T);

	/** @return the Moon's equatorial horizontal parallax &pi;, in degrees. */
	double horizontalParallax(double T);

	/** @return the model's fundamental epoch as a decimal year (1900.0 for AFFC). */
	double epochYear();

	/** @return the model identifier (e.g. {@code "affc-1900"}). */
	String id();
}
