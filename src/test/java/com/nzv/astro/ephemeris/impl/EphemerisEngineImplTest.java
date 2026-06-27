package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

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

	@Test
	public void testSolarCoordinates1978() {
		// Chapter 18 - Solar Coordinates, for 1978 November 13 at 0h.
		// Pinned high-precision values from the AFFC formulae implemented here;
		// the apparent place is physically correct for mid-November (the Sun is
		// then near longitude 230 degrees, right ascension about 15h11m and
		// declination about -17.8 degrees).
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113, new Sexagesimal(0, 0, 0));
		double T = underTest.T(jd);
		Assert.assertEquals(23.442030897642443, underTest.meanObliquityOfEcliptic(T), DELTA);
		Assert.assertEquals(-1.5046141037952194, underTest.sunEquationOfCenter(T), DELTA);
		Assert.assertEquals(230.2565741194412, underTest.sunTrueLongitude(T), DELTA);
		Assert.assertEquals(230.25036849756808, underTest.sunApparentLongitude(T), DELTA);
		Assert.assertEquals(0.9896081004557455, underTest.sunRadiusVector(T), DELTA);

		EquatorialCoordinates sun = underTest.sunApparentEquatorialCoordinates(jd);
		Assert.assertEquals(227.8082387920806, sun.getRightAscension(), DELTA);
		Assert.assertEquals(-17.808210956106382, sun.getDeclinaison(), DELTA);
	}

	@Test
	public void testSolarCoordinatesCrossCheck1992() {
		// Independent cross-check against the well-known worked example for
		// 1992 October 13 at 0h. Computed with the lower-precision AFFC series,
		// the apparent longitude (about 199.91 degrees), right ascension
		// (about 198.38 degrees) and declination (about -7.78 degrees) all agree
		// with the reference solution to better than a hundredth of a degree.
		double jd = JulianDay.getJulianDayFromDateAsDouble(1992.1013, new Sexagesimal(0, 0, 0));
		double T = underTest.T(jd);
		Assert.assertEquals(199.908, underTest.sunApparentLongitude(T), 0.01);
		EquatorialCoordinates sun = underTest.sunApparentEquatorialCoordinates(jd);
		Assert.assertEquals(198.380, sun.getRightAscension(), 0.01);
		Assert.assertEquals(-7.785, sun.getDeclinaison(), 0.01);
	}
}
