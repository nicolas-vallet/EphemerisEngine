package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.Sexagesimal;

public class EphemerisEngineImplTest {

	private static final double DELTA = 1e-6;

	private final EphemerisEngineImpl underTest = new EphemerisEngineImpl();

	@Test
	public void testGetNutationValues() {
		// For 13 November 1978 at 4h 34m UT.
		// Reference values from J. Meeus (nutation in longitude approx -3.378",
		// nutation in obliquity approx -9.321"). The obliquity term is the corrected
		// value; the historical code had the final term computed in degrees instead
		// of radians, which gave -9.3246".
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113, new Sexagesimal(4, 34, 0));
		Assert.assertEquals(-3.378041691298254, underTest.getNutationInLongitude(jd), DELTA);
		Assert.assertEquals(-9.3211553654306, underTest.getNutationInObliquity(jd), DELTA);
	}

	@Test
	public void testTimeInJulianCenturies() {
		// T is zero at the 1900.0 epoch (JD 2415020.0).
		Assert.assertEquals(0.0, underTest.T(2415020.0), DELTA);
		Assert.assertEquals(1.0, underTest.T(2415020.0 + 36525), DELTA);
	}
}
