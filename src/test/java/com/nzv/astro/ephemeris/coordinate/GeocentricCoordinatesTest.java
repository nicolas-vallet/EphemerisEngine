package com.nzv.astro.ephemeris.coordinate;

import org.junit.Assert;
import org.junit.Test;

public class GeocentricCoordinatesTest {

	private static final double DELTA = 1e-9;

	@Test
	public void testConstructor() {
		// Observer at latitude +50 47' 55", altitude 105 m (Uccle).
		GeocentricCoordinates gc = new GeocentricCoordinates(50, 47, 55, 105);
		Assert.assertEquals(0.7713061425409441, gc.getAbscissa(), DELTA);
		Assert.assertEquals(0.6333327780316349, gc.getOrdinate(), DELTA);
	}

	@Test
	public void testRhoSinAndCosPhiPrimeForPalomar() {
		// Chapter 6 worked example: Palomar Observatory, latitude +33 21' 22",
		// altitude 1706 m. Meeus gives rho*sin(phi') = +0.546861 and
		// rho*cos(phi') = +0.836339.
		GeocentricCoordinates palomar = new GeocentricCoordinates(33, 21, 22, 1706);
		Assert.assertEquals(0.5468608240604509, palomar.getRhoSinPhiPrime(), DELTA);
		Assert.assertEquals(0.8363392323525684, palomar.getRhoCosPhiPrime(), DELTA);
		Assert.assertEquals(0.546861, palomar.getRhoSinPhiPrime(), 1e-6);
		Assert.assertEquals(0.836339, palomar.getRhoCosPhiPrime(), 1e-6);
		// The clean accessors are aliases of the historical ones.
		Assert.assertEquals(palomar.getAbscissa(), palomar.getRhoSinPhiPrime(), DELTA);
		Assert.assertEquals(palomar.getOrdinate(), palomar.getRhoCosPhiPrime(), DELTA);
	}
}
