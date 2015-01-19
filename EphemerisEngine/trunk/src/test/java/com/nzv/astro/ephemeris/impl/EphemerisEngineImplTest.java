package com.nzv.astro.ephemeris.impl;


import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.Sexagesimal;

public class EphemerisEngineImplTest {

	EphemerisEngineImpl underTest = new EphemerisEngineImpl();
	
	@Test
	public void testGetNutationValues() {
		// Pour le 13 novembre 1978 à 4h 34m TU...
		double  jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113, new Sexagesimal(4, 34, 0));
		Assert.assertEquals(-3.3780416912980327, underTest.getNutationInLongitude(jd), 0);
		Assert.assertEquals(-9.324567698351453, underTest.getNutationInObliquity(jd), 0);
	}
}
