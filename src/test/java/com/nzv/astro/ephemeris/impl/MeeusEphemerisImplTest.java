package com.nzv.astro.ephemeris.impl;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;

public class MeeusEphemerisImplTest {

	private static final double DELTA = 1e-6;

	private final MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();

	private static SimpleDateFormat utcFormat(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf;
	}

	@Test
	public void testGetEasterDayGivenYear() {
		SimpleDateFormat sdf = utcFormat("dd/MM/yyyy");
		Assert.assertEquals("26/03/1978", sdf.format(meeusEngine.getEasterDateForYear(1978)));
		Assert.assertEquals("15/04/1979", sdf.format(meeusEngine.getEasterDateForYear(1979)));
		Assert.assertEquals("06/04/1980", sdf.format(meeusEngine.getEasterDateForYear(1980)));
		Assert.assertEquals("18/04/1954", sdf.format(meeusEngine.getEasterDateForYear(1954)));
		Assert.assertEquals("23/04/2000", sdf.format(meeusEngine.getEasterDateForYear(2000)));
	}

	@Test(expected = InvalidParameterException.class)
	public void testGetEasterDayBeforeGregorianReformThrows() {
		// The algorithm is only valid from 1583 onwards.
		meeusEngine.getEasterDateForYear(1000);
	}

	@Test
	public void testGetMeanSiderealTime() {
		// Mean sidereal time at Greenwich for 13 November 1978 at 0h.
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		double midnight = meeusEngine.getMeanSiderealTimeAsHoursFromJulianDay(jd);
		Assert.assertEquals(3.450369511764393, midnight, DELTA);

		// Mean sidereal time at Greenwich for 13 November 1978 at 4h 34m 0s.
		double atTime = meeusEngine.getMeanSiderealTimeAsHoursFromJulianDay(jd,
				new Sexagesimal(4, 34, 0));
		Assert.assertEquals(8.02953929163106, atTime, DELTA);
	}

	@Test
	public void testGetApparentSiderealTimeAsHoursFromJulianDay() {
		// Apparent sidereal time at Greenwich for 13 November 1978 at 4h 34m UT.
		// This is the mean sidereal time corrected by the equation of the equinoxes
		// (nutation-in-longitude * cos(obliquity) / 15). With the corrected formula the
		// result is approx 8h 01m 46.135s; the historical bug gave 8h 01m 45.339s.
		double apparent = meeusEngine.getApparentSiderealTimeAsHoursFromJulianDay(
				JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));
		Assert.assertEquals(8.029482045266201, apparent, DELTA);
	}

	@Test
	public void testUniversalTimeToEphemerisTime() {
		// 6 February -555 at 6h UT.
		double et = meeusEngine.universalTimeToEphemerisTime(-555.0206, new Sexagesimal(6, 0, 0))
				.getValueAsUnits();
		Assert.assertEquals(new Sexagesimal(11, 0, 28.100050000002).getValueAsUnits(), et, DELTA);
	}

	@Test
	public void testEphemerisTimeToUniversalTime() {
		// 4 April 1977 at 4h 19m ET.
		double ut = meeusEngine.ephemerisTimeToUniversalTime(1977.0404, new Sexagesimal(4, 19, 0))
				.getValueAsUnits();
		Assert.assertEquals(new Sexagesimal(4, 18, 12.1417528085172).getValueAsUnits(), ut, DELTA);
	}

	@Test
	public void testComputeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond() {
		assertDeltaT(11.877875897303616, -2000.0101);
		assertDeltaT(8640.309030098942, 200.0101);
		assertDeltaT(4738.848120923205, 600.0101);
		assertDeltaT(-4419.33432217818, 1673.0101);
		assertDeltaT(13.375979008768493, 1750.0101);
		assertDeltaT(7.112129821862254, 1850.0101);
		assertDeltaT(0.902134179753771, 1870.0101);
		assertDeltaT(10.445380968083992, 1910.0101);
		assertDeltaT(24.130835825057865, 1930.0101);
		assertDeltaT(29.086950910613957, 1950.0101);
		assertDeltaT(56.92137028332472, 1990.0101);
		assertDeltaT(68.54577795312504, 2014.0101);
		assertDeltaT(202.8381222222219, 2100.0101);
		assertDeltaT(442.18133888888855, 2200.0101);
	}

	private void assertDeltaT(double expected, double encodedDate) {
		Assert.assertEquals(expected, meeusEngine
				.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(encodedDate)), DELTA);
	}
}
