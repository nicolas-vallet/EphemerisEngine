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
}
