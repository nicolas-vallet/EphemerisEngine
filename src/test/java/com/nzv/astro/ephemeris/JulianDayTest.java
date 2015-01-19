package com.nzv.astro.ephemeris;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class JulianDayTest {
	
	private static String CALENDAR_DATE_AND_TIME_PATTERN = "dd/MM/yyy HH:mm:ss";
	private static String CALENDAR_DATE_AND_TIME_AND_ERA_PATTERN = "dd/MM/yyy GGG HH:mm:ss";

	@Test
	public void testGetJulianDayFromDateAsDoubleAfterGregorianEra() {
		// Jour julien pour le 4.81 octobre 1957
		double result = JulianDay.getJulianDayFromDateAsDouble(1957.100481d);
		Assert.assertEquals(2436116.31, result, 0);
	}
	
	@Test
	public void testGetDateFromJulianDayAfterGregorianEra() {
		// Date pour le jour julian 2436116.31
		SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_PATTERN);
		Date date = JulianDay.getDateFromJulianDay(2436116.31);
		Assert.assertTrue("04/10/1957 19:26:24".equals(sdf.format(date)));
	}
	
	@Test
	public void testGetJulianDayFromDateAsDoubleBeforeGregorianEra() {
		// Jour julien pour le 27.5 janvier 333
		double result = JulianDay.getJulianDayFromDateAsDouble(333.01275d);
		Assert.assertEquals(1842713.0, result, 0);
		
	}
	
	@Test
	public void testGetDateFromJulianDayBeforeGregorianEra() {
		// Date pour le jour julian 1842713.0
		SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_PATTERN);
		Date date = JulianDay.getDateFromJulianDay(1842713.0);
		Assert.assertTrue("27/01/333 12:00:00".equals(sdf.format(date)));
	}
	
	@Test
	public void testGetJulianDayFromDateAsDoubleBeforeJC() {
		// Jour julien pour le 28.63 mai -584
		double result = JulianDay.getJulianDayFromDateAsDouble(-584.052863d);
		Assert.assertEquals(1507900.13, result, 0);
	}
	
	@Test
	public void testGetDateFromJulianDayBeforeJC() {
		// Date pour le jour julian 1507900.13
		SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_AND_ERA_PATTERN);
		Date date = JulianDay.getDateFromJulianDay(1507900.13);
		Assert.assertTrue("28/05/585 BC 15:07:12".equals(sdf.format(date)));
	}
	
	@Test
	public void testIntervalInDaysBetweenTwoDates() {
		// Intervalle de temps entre le 16/11/1835 et le 20/04/1910
		double result = JulianDay.getJulianDayFromDateAsDouble(1910.0420d) - JulianDay
				.getJulianDayFromDateAsDouble(1835.1116d);
		Assert.assertEquals(27183.0, result, 0);
	}
}
