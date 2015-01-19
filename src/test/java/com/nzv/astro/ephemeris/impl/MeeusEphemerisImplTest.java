package com.nzv.astro.ephemeris.impl;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;

public class MeeusEphemerisImplTest {

	private MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();
	
	@Test
	public void testGetEasterDayGivenYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Assert.assertTrue("26/03/1978".equals(sdf.format(meeusEngine.getEasterDateForYear(1978))));
		Assert.assertTrue("15/04/1979".equals(sdf.format(meeusEngine.getEasterDateForYear(1979))));
		Assert.assertTrue("06/04/1980".equals(sdf.format(meeusEngine.getEasterDateForYear(1980))));
		Assert.assertTrue("18/04/1954".equals(sdf.format(meeusEngine.getEasterDateForYear(1954))));
		Assert.assertTrue("23/04/2000".equals(sdf.format(meeusEngine.getEasterDateForYear(2000))));
		try {
			meeusEngine.getEasterDateForYear(1000);
			Assert.assertTrue(false);
		} catch (InvalidParameterException ex) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testGetMeanSideralTime() {
		// Temps sideral moyen a Greenwich pour le 13 novembre 1978 à 0h
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		Sexagesimal s = Sexagesimal.decimalToSexagesimal(meeusEngine
				.getMeanSiderealTimeAsHoursFromJulianDay(jd));
		Assert.assertEquals(3, s.getUnit(), 0);
		Assert.assertEquals(27, s.getMinute(), 0);
		Assert.assertEquals(1.3302423518148, s.getSecond(), 0);
		
		// Temps sideral moyen a Greenwich pour le 13 novembre 1978 à 4h 34m 0s
		jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		s = Sexagesimal.decimalToSexagesimal(meeusEngine.getMeanSiderealTimeAsHoursFromJulianDay(
				jd, new Sexagesimal(4, 34, 0)));
		Assert.assertEquals(8, s.getUnit(), 0);
		Assert.assertEquals(1, s.getMinute(), 0);
		Assert.assertEquals(46.341449871816, s.getSecond(), 0);
	}
}
