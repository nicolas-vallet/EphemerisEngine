package com.nzv.astro.ephemeris.coordinate.adapter;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates;

public class EclipticCoordinatesAdapterTest {

	private static final double DELTA = 1e-6;

	@Test
	public void testConvertEclipticCoordinatesToEquatorialCoordinates() {
		// Equatorial coordinates of Pollux from ecliptic LAMBDA=112.52538 / BETA=6.68058.
		// Expected RA approx 7h 42m 15.527s, DEC approx +28 08' 55.10".
		EclipticCoordinatesAdapter eca = new EclipticCoordinatesAdapter(
				new EclipticCoordinates(112.52538, 6.68058));
		Assert.assertEquals(7.704313001462867, eca.getRightAscension() / 15, DELTA);
		Assert.assertEquals(28.14863933868333, eca.getDeclinaison(), DELTA);
	}
}
