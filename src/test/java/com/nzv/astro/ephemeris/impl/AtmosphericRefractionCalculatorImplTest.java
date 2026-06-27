package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.Sexagesimal;

public class AtmosphericRefractionCalculatorImplTest {

	private static final double DELTA = 1e-6;

	private final AtmosphericRefractionCalculatorImpl underTest =
			new AtmosphericRefractionCalculatorImpl();

	@Test
	public void testGetTrueElevationFromApparentElevation() {
		// Apparent 32 04' 17" -> true approx 32 02' 44.24" (32.045622 deg).
		double te = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32, 4, 17)));
		Assert.assertEquals(32.045622153441194, te, DELTA);

		// Apparent 2 14' 19.34" -> true approx 1 57' 01.64" (1.950455 deg).
		te = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(2, 14, 19.34)));
		Assert.assertEquals(1.9504552773929564, te, DELTA);

		// Same with T(38 C) and P(1023 hPa).
		te = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(2, 14, 19.34)), 38, 1023);
		Assert.assertEquals(1.9763978024275903, te, DELTA);

		// At the zenith there is no refraction.
		te = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(90, 0, 0)));
		Assert.assertEquals(90.0, te, DELTA);
	}

	@Test
	public void testGetApparentElevationFromTrueElevation() {
		// True 32 02' 44.24" -> apparent approx 32 04' 17" (32.071389 deg).
		double ae = underTest.getApparentElevation(new Sexagesimal(32, 2, 44.24).getValueAsUnits());
		Assert.assertEquals(32.07138892484527, ae, DELTA);

		// True 1 57' -> apparent approx 2 14' 19.34" (2.238706 deg).
		ae = underTest.getApparentElevation(new Sexagesimal(1, 57, 0).getValueAsUnits());
		Assert.assertEquals(2.2387056609825216, ae, DELTA);

		// At the zenith there is no refraction.
		ae = underTest.getApparentElevation(new Sexagesimal(90, 0, 0).getValueAsUnits());
		Assert.assertEquals(90.0, ae, DELTA);
	}

	@Test
	public void testNormalClimaticConditionsAreNeutral() {
		// At T(10 C) and P(1013 hPa) the temperature/pressure correction is a no-op.
		double bare = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32, 4, 17)));
		double withConditions = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32, 4, 17)), 10, 1013);
		Assert.assertEquals(bare, withConditions, DELTA);
	}

	@Test
	public void testGetTrueElevationWithClimaticConditions() {
		// Apparent 32 04' 17" at T(25 C) and P(1020 hPa) -> 32.046822 deg.
		double te = underTest.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32, 4, 17)), 25, 1020);
		Assert.assertEquals(32.046822147120615, te, DELTA);
	}
}
