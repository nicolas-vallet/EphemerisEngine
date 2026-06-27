package com.nzv.astro.ephemeris;

/**
 * Holds the result of a rising / transit / setting computation for a celestial
 * body, as produced by {@link RiseTransitSetCalculator} (chapter 42 of Jean
 * Meeus' <i>Astronomical Formulae for Calculators</i>).
 * <p>
 * All times are expressed as Universal Time in decimal hours in the range
 * [0, 24). When the body never crosses the chosen horizon the corresponding
 * rise and set times are {@link Double#NaN} and one of {@link #isCircumpolar()}
 * or {@link #isAlwaysBelowHorizon()} is {@code true}; the transit time is always
 * defined.
 */
public class RiseTransitSet {

	private final double riseTimeUT;
	private final double transitTimeUT;
	private final double setTimeUT;
	private final double riseAzimuth;
	private final double setAzimuth;
	private final double transitAltitude;
	private final boolean circumpolar;
	private final boolean alwaysBelowHorizon;

	public RiseTransitSet(double riseTimeUT, double transitTimeUT, double setTimeUT,
			double riseAzimuth, double setAzimuth, double transitAltitude, boolean circumpolar,
			boolean alwaysBelowHorizon) {
		this.riseTimeUT = riseTimeUT;
		this.transitTimeUT = transitTimeUT;
		this.setTimeUT = setTimeUT;
		this.riseAzimuth = riseAzimuth;
		this.setAzimuth = setAzimuth;
		this.transitAltitude = transitAltitude;
		this.circumpolar = circumpolar;
		this.alwaysBelowHorizon = alwaysBelowHorizon;
	}

	/**
	 * @return the time of rising, in Universal Time decimal hours, or
	 *         {@link Double#NaN} if the body does not rise.
	 */
	public double getRiseTimeUT() {
		return riseTimeUT;
	}

	/**
	 * @return the time of (upper) transit, in Universal Time decimal hours.
	 */
	public double getTransitTimeUT() {
		return transitTimeUT;
	}

	/**
	 * @return the time of setting, in Universal Time decimal hours, or
	 *         {@link Double#NaN} if the body does not set.
	 */
	public double getSetTimeUT() {
		return setTimeUT;
	}

	/**
	 * @return the azimuth of the body at rising, measured eastwards from the
	 *         north, in degrees, or {@link Double#NaN} if the body does not rise.
	 */
	public double getRiseAzimuth() {
		return riseAzimuth;
	}

	/**
	 * @return the azimuth of the body at setting, measured eastwards from the
	 *         north, in degrees, or {@link Double#NaN} if the body does not set.
	 */
	public double getSetAzimuth() {
		return setAzimuth;
	}

	/**
	 * @return the altitude of the body at upper transit, in degrees.
	 */
	public double getTransitAltitude() {
		return transitAltitude;
	}

	/**
	 * @return {@code true} when the body never sets below the chosen horizon.
	 */
	public boolean isCircumpolar() {
		return circumpolar;
	}

	/**
	 * @return {@code true} when the body never rises above the chosen horizon.
	 */
	public boolean isAlwaysBelowHorizon() {
		return alwaysBelowHorizon;
	}
}
