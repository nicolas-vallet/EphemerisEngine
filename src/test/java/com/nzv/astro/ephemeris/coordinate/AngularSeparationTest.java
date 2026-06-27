package com.nzv.astro.ephemeris.coordinate;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

public class AngularSeparationTest {

	private static final double DELTA = 1e-9;

	@Test
	public void testArcturusSpicaSeparation() {
		// Chapter 9 canonical example: the angular separation of Arcturus and
		// Spica is about 32.79 degrees.
		EquatorialCoordinates arcturus = new EquatorialCoordinates(213.9154, 19.1825);
		EquatorialCoordinates spica = new EquatorialCoordinates(201.2983, -11.1614);
		Assert.assertEquals(32.793026837094914, AngularSeparation.between(arcturus, spica), DELTA);
		Assert.assertEquals(32.79, AngularSeparation.between(arcturus, spica), 0.01);
	}

	@Test
	public void testSeparationIsSymmetric() {
		EquatorialCoordinates a = new EquatorialCoordinates(10, 20);
		EquatorialCoordinates b = new EquatorialCoordinates(40, -5);
		Assert.assertEquals(AngularSeparation.between(a, b), AngularSeparation.between(b, a), DELTA);
	}

	@Test
	public void testSeparationOfCoincidentPointsIsZero() {
		EquatorialCoordinates a = new EquatorialCoordinates(123.4, -45.6);
		Assert.assertEquals(0.0, AngularSeparation.between(a, a), 1e-7);
	}

	@Test
	public void testPositionAngleRange() {
		EquatorialCoordinates arcturus = new EquatorialCoordinates(213.9154, 19.1825);
		EquatorialCoordinates spica = new EquatorialCoordinates(201.2983, -11.1614);
		double p = AngularSeparation.positionAngle(arcturus, spica);
		Assert.assertTrue(p >= 0 && p < 360);
		Assert.assertEquals(203.30843147585406, p, DELTA);
	}
}
