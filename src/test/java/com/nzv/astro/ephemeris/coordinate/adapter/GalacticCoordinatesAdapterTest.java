package com.nzv.astro.ephemeris.coordinate.adapter;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.coordinate.impl.GalacticCoordinates;

public class GalacticCoordinatesAdapterTest {

	private static final double DELTA = 1e-6;

	@Test
	public void testConvertGalacticCoordinatesToEquatorialCoordinates() {
		// Equatorial coordinates of Nova Serpentis 1978 from galactic l=12.9593 / b=6.0463.
		// Expected RA approx 17h 48m 59.75s, DEC approx -14 43' 08.04".
		GalacticCoordinatesAdapter adapter = new GalacticCoordinatesAdapter(
				new GalacticCoordinates(12.9593d, 6.0463d));
		Assert.assertEquals(17.816596087757986, adapter.getRightAscension() / 15, DELTA);
		Assert.assertEquals(-14.718900831521825, adapter.getDeclinaison(), DELTA);
	}
}
