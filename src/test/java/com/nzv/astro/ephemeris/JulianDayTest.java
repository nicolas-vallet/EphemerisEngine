package com.nzv.astro.ephemeris;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class JulianDayTest {

	private static final String CALENDAR_DATE_AND_TIME_PATTERN = "dd/MM/yyy HH:mm:ss";
	private static final String CALENDAR_DATE_AND_TIME_AND_ERA_PATTERN = "dd/MM/yyy GGG HH:mm:ss";

	/** Tolerance for Julian-day values (well below one second of a day). */
	private static final double DELTA = 1e-6;

	private static SimpleDateFormat utcFormat(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf;
	}

	@Test
	public void testGetJulianDayFromDateAsDoubleAfterGregorianEra() {
		// Julian day for 4.81 October 1957
		double result = JulianDay.getJulianDayFromDateAsDouble(1957.100481d);
		Assert.assertEquals(2436116.31, result, DELTA);
	}

	@Test
	public void testGetDateFromJulianDayAfterGregorianEra() {
		SimpleDateFormat sdf = utcFormat(CALENDAR_DATE_AND_TIME_PATTERN);
		Date date = JulianDay.getDateFromJulianDay(2436116.31);
		Assert.assertEquals("04/10/1957 19:26:24", sdf.format(date));
	}

	@Test
	public void testGetJulianDayFromDateAsDoubleBeforeGregorianEra() {
		// Julian day for 27.5 January 333
		double result = JulianDay.getJulianDayFromDateAsDouble(333.01275d);
		Assert.assertEquals(1842713.0, result, DELTA);
	}

	@Test
	public void testGetDateFromJulianDayBeforeGregorianEra() {
		SimpleDateFormat sdf = utcFormat(CALENDAR_DATE_AND_TIME_PATTERN);
		Date date = JulianDay.getDateFromJulianDay(1842713.0);
		Assert.assertEquals("27/01/333 12:00:00", sdf.format(date));
	}

	@Test
	public void testGetJulianDayFromDateAsDoubleBeforeJC() {
		// Julian day for 28.63 May -584
		double result = JulianDay.getJulianDayFromDateAsDouble(-584.052863d);
		Assert.assertEquals(1507900.13, result, DELTA);
	}

	@Test
	public void testGetDateFromJulianDayBeforeJC() {
		SimpleDateFormat sdf = utcFormat(CALENDAR_DATE_AND_TIME_AND_ERA_PATTERN);
		Date date = JulianDay.getDateFromJulianDay(1507900.13);
		Assert.assertEquals("28/05/585 BC 15:07:12", sdf.format(date));
	}

	@Test
	public void testIntervalInDaysBetweenTwoDates() {
		double result = JulianDay.getJulianDayFromDateAsDouble(1910.0420d)
				- JulianDay.getJulianDayFromDateAsDouble(1835.1116d);
		Assert.assertEquals(27183.0, result, DELTA);
	}

	@Test
	public void testGetDateNDaysAfterGivenDate() {
		Date date = JulianDay.getDateFromJulianDay(JulianDay
				.getJulianDayFromDateAsDouble(1954.0630d) + 10000);
		SimpleDateFormat sdf = utcFormat(CALENDAR_DATE_AND_TIME_PATTERN);
		Assert.assertEquals("15/11/1981 00:00:00", sdf.format(date));
	}

	@Test
	public void testGetDayOfWeekGivenDate() {
		Assert.assertSame(DayOfWeek.WEDNESDAY, JulianDay.getDayOfWeekFromDayAsDouble(1954.0630d));
	}

	@Test
	public void testGetDayOfYearGivenDate() {
		Assert.assertEquals(318, JulianDay.getDayOfYearFromDateAsDouble(1978.1114d));
		Assert.assertEquals(113, JulianDay.getDayOfYearFromDateAsDouble(1980.0422d));
	}

	@Test
	public void testIsLeapYear() {
		Assert.assertTrue(JulianDay.isLeapYear(2000));
		Assert.assertTrue(JulianDay.isLeapYear(2024));
		Assert.assertFalse(JulianDay.isLeapYear(1900));
		Assert.assertFalse(JulianDay.isLeapYear(2023));
	}
}
