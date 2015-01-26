package com.nzv.astro.ephemeris.coordinate.adapter;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates;

public class EclipticCoordinatesAdapterTest {

	@Test
	public void testConvertEclipticCoordinatesToEquatorialCoordinates() {
		// Coordonnées équatoriaux de Pollux dont les coordonnées ecliptiques sont LAMBDA=112.52538° / BETA=6.68058°
		EclipticCoordinatesAdapter eca = new EclipticCoordinatesAdapter(new EclipticCoordinates(112.52538, 6.68058));
		Sexagesimal RA = new Sexagesimal(eca.getRightAscension() / 15);
		Sexagesimal DEC = new Sexagesimal(eca.getDeclinaison());
		Assert.assertTrue("7H 42m 15.5268052663122s".equals(RA.toString(SexagesimalType.HOURS)));
		Assert.assertTrue("28° 8' 55.101619260024\"".equals(DEC.toString(SexagesimalType.DEGREES)));
	}
}
