package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;
import com.nzv.astro.ephemeris.lunar.MoonModels;

/**
 * Integration tests for the chapter-30 Moon methods exposed on {@link EphemerisEngine}, anchored
 * on Meeus' Example 30.a (1979 Dec. 7.0 ET, JD 2444214.5).
 */
public class MoonPositionImplTest {

	private static final double JD = 2444214.5;
	private final EphemerisEngine engine = new EphemerisEngineImpl();

	@Test
	public void testEngineDelegatesToModel() {
		double T = engine.T(JD);
		EphemerisEngine viaModel = new EphemerisEngineImpl(MoonModels.AFFC_1900.getModel());
		Assert.assertEquals(viaModel.moonGeocentricLongitude(T), engine.moonGeocentricLongitude(T), 1e-9);
		Assert.assertEquals(viaModel.moonGeocentricLatitude(T), engine.moonGeocentricLatitude(T), 1e-9);
	}

	@Test
	public void testWorkedExampleThroughEngine() {
		double T = engine.T(JD);
		Assert.assertEquals(113.6604, engine.moonGeocentricLongitude(T), 0.001);
		double aeBeta = -(3 + 9 / 60.0 + 49.22 / 3600.0);
		Assert.assertEquals(aeBeta, engine.moonGeocentricLatitude(T), 3.0 / 3600.0);
		Assert.assertEquals(0.930249, engine.moonEquatorialHorizontalParallaxe(T), 1.0 / 3600.0);
	}

	@Test
	public void testDistanceConsistentAndPlausible() {
		double T = engine.T(JD);
		double fromParallax = engine
				.earthDistanceToMoonInKilometers(engine.moonEquatorialHorizontalParallaxe(T));
		double viaJd = engine.moonDistanceToEarthInKilometers(JD);
		Assert.assertEquals(fromParallax, viaJd, 1e-6);
		// Earth-Moon distance always lies between perigee and apogee extremes.
		Assert.assertTrue("distance in lunar range", viaJd > 356000 && viaJd < 407000);
		Assert.assertEquals(392858.724411, viaJd, 1e-3);
	}

	@Test
	public void testApparentEquatorialReasonableAndPinned() {
		EquatorialCoordinates eq = engine.moonApparentEquatorialCoordinates(JD);
		Assert.assertTrue("RA normalised",
				eq.getRightAscension() >= 0.0 && eq.getRightAscension() < 360.0);
		// The Moon's declination never exceeds ~28.6 degrees in magnitude.
		Assert.assertTrue("declination bounded", Math.abs(eq.getDeclinaison()) <= 28.7);
		Assert.assertEquals(114.953650447, eq.getRightAscension(), 1e-6);
		Assert.assertEquals(18.249741283, eq.getDeclinaison(), 1e-6);
	}

	@Test
	public void testGeocentricEclipticMatchesTrueOfDate() {
		double T = engine.T(JD);
		EclipticCoordinates ec = engine.moonGeocentricEclipticCoordinates(JD);
		Assert.assertEquals(engine.moonGeocentricLongitude(T), ec.getEcliptiqueLongitude(), 1e-9);
		Assert.assertEquals(engine.moonGeocentricLatitude(T), ec.getEcliptiqueLatitude(), 1e-9);
	}
}
