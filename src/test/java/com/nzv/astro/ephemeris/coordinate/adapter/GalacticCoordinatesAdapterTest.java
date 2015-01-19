package com.nzv.astro.ephemeris.coordinate.adapter;


import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import com.nzv.astro.ephemeris.coordinate.impl.GalacticCoordinates;

public class GalacticCoordinatesAdapterTest {

	@Test
	public void testConvertGalacticCoordinatesToEquatorialCoordinates() {
		// Coordonnées équatoriaux de Nova Serpentis 1978 (l=12.9593° / b=6.0463°)
		GalacticCoordinatesAdapter galacticCoordinatesAdapter = new GalacticCoordinatesAdapter(
				new GalacticCoordinates(12.9593d, 6.0463d));
		Sexagesimal ra = new Sexagesimal(galacticCoordinatesAdapter.getRightAscension() / 15);
		Sexagesimal dec = new Sexagesimal(galacticCoordinatesAdapter.getDeclinaison());
		Assert.assertTrue("17H 48m 59.7459159287496s".equals(ra.toString(SexagesimalType.HOURS)));
		Assert.assertTrue("-14° 43' 8.042993478516\"".equals(dec.toString(SexagesimalType.DEGREES)));
	}
}
