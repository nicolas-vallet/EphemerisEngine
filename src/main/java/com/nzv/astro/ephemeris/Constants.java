package com.nzv.astro.ephemeris;

import java.math.MathContext;

public interface Constants {

    /**
     * The precision we want to use when manipulating BigDecimal type.
     */
    MathContext BIG_DECIMAL_PRECISION = MathContext.DECIMAL64;

    /**
     * The Earth flattening
     */
    double EARTH_FLATTENING = 1 / 298.257;

    double GREGORIAN_CALENDAR_START_DATE = 1582.1015;

    /**
     * Ecliptic obliquity for year 1950
     */
    Sexagesimal ECLIPTIC_OBLIQUITY_1950 = new Sexagesimal(23.4457889);

    /**
     * Ecliptic obliquity for year 2000
     */
    Sexagesimal ECLIPTIC_OBLIQUITY_2000 = new Sexagesimal(23.4392911);

    double EARTH_EQUATORIAL_RADIUS_IN_KM = 6378.140;

}
