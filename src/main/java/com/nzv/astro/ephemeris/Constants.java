package com.nzv.astro.ephemeris;

import java.math.MathContext;

public final class Constants {

	/**
	 * The precision we want to use when manipulating BigDecimal type.
	 */
	public static final MathContext BIG_DECIMAL_PRECISION = MathContext.DECIMAL64;

	/** 
	 * The Earth flattening
	 */
	public static final double EARTH_FLATTENING = 1 / 298.257;

	public static final double GREGORIAN_CALENDAR_START_DATE = 1582.1015;

	/**
	 * Ecliptic obliquity for year 1950
	 */
	public static final Sexagesimal ECLIPTIC_OBLIQUITY_1950 = new Sexagesimal(23.4457889);
	
	/**
	 * Ecliptic obliquity for year 2000
	 */
	public static final Sexagesimal ECLIPTIC_OBLIQUITY_2000 = new Sexagesimal(23.4392911);
	
	public static final double EARTH_EQUATORIAL_RADIUS_IN_KM = 6378.140;
	
}
