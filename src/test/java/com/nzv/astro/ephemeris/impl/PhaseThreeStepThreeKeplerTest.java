package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.KeplerEquation;

/**
 * Tests for Phase 3, step 3a of the implementation plan: chapter 22, the
 * equation of Kepler.
 * <p>
 * Each of the chapter's three methods is anchored on its worked example (loose
 * check against the book) with a pinned high-precision value (regression
 * anchor), and the solver is checked for the obvious traps: e = 0 must return
 * E = M, and a returned E must actually satisfy Kepler's equation.
 */
public class PhaseThreeStepThreeKeplerTest {

	private static final double PIN = 1e-9;

	// =====================================================================
	// Chapter 22 - Equation of Kepler
	// =====================================================================

	@Test
	public void testChapter22FirstMethodIteration() {
		// Example 22.a: e = 0.100, M = 5 deg, fixed-point iteration to 1e-6 deg.
		// Book: E = 5.554589.
		double E = KeplerEquation.solveEccentricAnomalyByIteration(5.0d, 0.100d, 1e-6d);
		Assert.assertEquals(5.554589d, E, 1e-6d);
		Assert.assertEquals(5.554589200183587d, E, PIN);
	}

	@Test
	public void testChapter22SecondMethodNewton() {
		// Example 22.b: same data via Newton's correction (formula 22.3).
		// Book: E = 5.554589253 after only three iterations.
		double E = KeplerEquation.solveEccentricAnomaly(5.0d, 0.100d);
		Assert.assertEquals(5.554589253d, E, 1e-9d);
		Assert.assertEquals(5.554589253872315d, E, PIN);
	}

	@Test
	public void testChapter22NewtonHandlesHighEccentricity() {
		// The chapter's hard case: e = 0.990, M = 2 deg. The fixed-point iteration
		// is still wrong after 50 steps, but Newton converges. Book correct value:
		// E = 32.361007.
		double E = KeplerEquation.solveEccentricAnomaly(2.0d, 0.990d);
		Assert.assertEquals(32.361007d, E, 1e-6d);
		Assert.assertEquals(32.36100747203112d, E, PIN);
	}

	@Test
	public void testChapter22ApproximateThirdMethod() {
		// Formula 22.4 (closed-form approximation), valid for small e.
		// Example data e = 0.100, M = 5 deg: book E = 5.554599 (exact 5.554589).
		double E = KeplerEquation.approximateEccentricAnomaly(5.0d, 0.100d);
		Assert.assertEquals(5.554599d, E, 1e-6d);
		Assert.assertEquals(5.554598871530979d, E, PIN);
		// The approximation's error here is well under a thousandth of a degree.
		double exact = KeplerEquation.solveEccentricAnomaly(5.0d, 0.100d);
		Assert.assertTrue(Math.abs(E - exact) < 1e-4d);
	}

	@Test
	public void testChapter22ZeroEccentricityReturnsMeanAnomaly() {
		// With a circular orbit the eccentric anomaly equals the mean anomaly.
		for (double M : new double[] { 0.0d, 5.0d, 123.0d, 280.0d }) {
			Assert.assertEquals(M, KeplerEquation.solveEccentricAnomaly(M, 0.0d), PIN);
			Assert.assertEquals(M, KeplerEquation.solveEccentricAnomalyByIteration(M, 0.0d, 1e-9d),
					PIN);
		}
	}

	@Test
	public void testChapter22SolutionSatisfiesKeplersEquation() {
		// A returned E must satisfy E = M + e0 sin E (formula 22.2) across a range
		// of eccentricities and mean anomalies.
		double[] eccentricities = { 0.0d, 0.0167d, 0.25d, 0.5d, 0.9d, 0.99d };
		double[] meanAnomalies = { 2.0d, 47.0d, 130.0d, 200.0d, 310.0d };
		for (double e : eccentricities) {
			double e0 = Math.toDegrees(e);
			for (double M : meanAnomalies) {
				double E = KeplerEquation.solveEccentricAnomaly(M, e);
				double residual = E - e0 * Math.sin(Math.toRadians(E)) - M;
				Assert.assertEquals("Kepler residual for e=" + e + " M=" + M, 0.0d, residual, 1e-7d);
			}
		}
	}
}
