package com.nzv.astro.ephemeris.lunar;

import org.junit.Assert;
import org.junit.Test;

public class PeriodicTermTest {

	private static final double DELTA = 1e-12;

	@Test
	public void testEvaluateMatchesExplicitFormula() {
		// 0.5 · e^1 · sin( 2D + 0M - 1M' + 0F ) at chosen arguments.
		PeriodicTerm term = new PeriodicTerm(1, 0.5, 2, 0, -1, 0);
		double D = 30.0, M = 10.0, Mp = 40.0, F = 5.0, e = 0.9;
		double expected = 0.5 * Math.pow(e, 1) * Math.sin(Math.toRadians(2 * D - Mp));
		Assert.assertEquals(expected, term.evaluate(D, M, Mp, F, e, Math::sin), DELTA);
	}

	@Test
	public void testEccentricityPowerZeroIgnoresE() {
		PeriodicTerm term = new PeriodicTerm(0, 1.3, 0, 0, 1, 0);
		double withE = term.evaluate(0, 0, 90, 0, 0.5, Math::sin);
		Assert.assertEquals(1.3, withE, DELTA); // sin(90°)=1, e^0=1
	}

	@Test
	public void testCosineSeriesUsesSuppliedTrig() {
		PeriodicTerm term = new PeriodicTerm(0, 2.0, 0, 0, 0, 1);
		Assert.assertEquals(2.0, term.evaluate(0, 0, 0, 0, 1.0, Math::cos), DELTA); // cos(0)=1
	}
}
