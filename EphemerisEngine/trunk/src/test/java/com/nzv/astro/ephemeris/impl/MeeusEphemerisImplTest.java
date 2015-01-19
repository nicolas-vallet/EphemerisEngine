package com.nzv.astro.ephemeris.impl;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.MeeusEphemeris;

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
}
