package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.ParallaxCorrection;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Tests for Phase 3, step 2 of the implementation plan: chapter 21 (equation of
 * time) and chapter 29 (correction for parallax).
 * <p>
 * As elsewhere, each chapter is checked in two tiers: a loose comparison with
 * the relevant Meeus worked example (the trustworthy anchor) and a pinned
 * high-precision value (the regression anchor). Pure geometry is exercised with
 * the book's own intermediate values so a transcription slip is isolated from a
 * model difference.
 */
public class PhaseThreeStepTwoTest {

	private static final double PIN = 1e-9;
	private final EphemerisEngineImpl underTest = new EphemerisEngineImpl();

	// =====================================================================
	// Chapter 21 - Equation of Time
	// =====================================================================

	@Test
	public void testChapter21Series() {
		// Example 21.b: 1978 January 21 at 0h ET = JD 2443529.5, via the
		// self-contained series (formula 21.1). Book: E = -11m 10.3s.
		double e = underTest.equationOfTime(2443529.5d);
		double bookMinutes = -(11 + 10.3d / 60.0d);
		Assert.assertEquals(bookMinutes, e, 0.01d);
		Assert.assertEquals(-11.171186178539639d, e, PIN);
	}

	@Test
	public void testChapter21FromApparentValues() {
		// Example 21.a: 1978 January 21 at 0h UT, from A.E. values:
		// apparent sidereal time 8h00m01.193s, Sun apparent RA 20h11m10.78s,
		// dT = +48.6s. Book: E = -11m 09.72s.
		double theta0 = 8 + 1.193d / 3600.0d;
		double sunRA = 20 + 11 / 60.0d + 10.78d / 3600.0d;
		double e = EphemerisEngineImpl.equationOfTimeFromApparentValues(theta0, sunRA, 48.6d);
		double bookMinutes = -(11 + 9.72d / 60.0d);
		Assert.assertEquals(bookMinutes, e, 0.01d);
		Assert.assertEquals(-11.162001113333309d, e, PIN);
	}

	@Test
	public void testChapter21StaysWithinAnnualBounds() {
		// The equation of time never leaves roughly +/-16.5 minutes; sample a few
		// instants across a year as a sanity bound on the series.
		double jd = 2443509.5d; // early 1978
		for (int i = 0; i < 12; i++) {
			double e = underTest.equationOfTime(jd + i * 30.0d);
			Assert.assertTrue("equation of time out of bounds: " + e, Math.abs(e) < 17.0d);
		}
	}

	// =====================================================================
	// Chapter 29 - Correction for Parallax
	// =====================================================================

	@Test
	public void testChapter29ParallaxFromDistance() {
		// Example 29.a: Mars at 0.3757 AU has equatorial horizontal parallax
		// pi = 8".794 / 0.3757 = 23".41 (formula 29.1).
		double piArcsec = ParallaxCorrection.parallaxFromDistanceInDegrees(0.3757d) * 3600.0d;
		Assert.assertEquals(23.41d, piArcsec, 0.01d);
	}

	@Test
	public void testChapter29MarsRigorous() {
		// Example 29.a (rigorous formulae 29.2/29.3), Uccle Observatory:
		// alpha = 21h24m46.85s, delta = -22d24'09".9, pi = 23".41,
		// rho.sin(phi') = +0.771306, rho.cos(phi') = +0.633333, H = +41.562 deg.
		// Book topocentric: alpha' = 21h24m46.14s, delta' = -22d24'30".8.
		double alpha = (21 + 24 / 60.0d + 46.85d / 3600.0d) * 15.0d;
		double delta = -(22 + 24 / 60.0d + 9.9d / 3600.0d);
		double pi = 23.41d / 3600.0d;
		EquatorialCoordinates topo = ParallaxCorrection.topocentric(alpha, delta, pi, 0.771306d,
				0.633333d, 41.562d);

		double bookAlpha = (21 + 24 / 60.0d + 46.14d / 3600.0d) * 15.0d;
		double bookDelta = -(22 + 24 / 60.0d + 30.8d / 3600.0d);
		Assert.assertEquals(bookAlpha, topo.getRightAscension(), 5e-4d);
		Assert.assertEquals(bookDelta, topo.getDeclinaison(), 5e-4d);
		// Regression anchors.
		Assert.assertEquals(321.19225282952004d, topo.getRightAscension(), PIN);
		Assert.assertEquals(-22.40856158749298d, topo.getDeclinaison(), PIN);
	}

	@Test
	public void testChapter29MarsApproximateMatchesRigorous() {
		// For Mars the non-rigorous formulae (29.4/29.5) give the same result.
		double alpha = (21 + 24 / 60.0d + 46.85d / 3600.0d) * 15.0d;
		double delta = -(22 + 24 / 60.0d + 9.9d / 3600.0d);
		double pi = 23.41d / 3600.0d;
		EquatorialCoordinates approx = ParallaxCorrection.topocentricApproximate(alpha, delta, pi,
				0.771306d, 0.633333d, 41.562d);
		Assert.assertEquals(321.19225282952004d, approx.getRightAscension(), 5e-5d);
		Assert.assertEquals(-22.40856158749298d, approx.getDeclinaison(), 5e-5d);
		// Regression anchors.
		Assert.assertEquals(321.1922530014444d, approx.getRightAscension(), PIN);
		Assert.assertEquals(-22.40856151930152d, approx.getDeclinaison(), PIN);
	}

	@Test
	public void testChapter29MoonNeedsRigorousFormulae() {
		// The book's Moon exercise (fictive values, Uccle observer):
		// alpha = 15 deg, delta = +5 deg, H = +60 deg, pi = 0d59'00".
		// For the Moon the rigorous and non-rigorous results differ markedly,
		// which is precisely why the chapter says to use the rigorous formulae.
		double pi = 59.0d / 60.0d;
		EquatorialCoordinates rigorous = ParallaxCorrection.topocentric(15.0d, 5.0d, pi, 0.771306d,
				0.633333d, 60.0d);
		EquatorialCoordinates approximate = ParallaxCorrection.topocentricApproximate(15.0d, 5.0d,
				pi, 0.771306d, 0.633333d, 60.0d);
		double deltaAlphaArcsec = Math
				.abs(rigorous.getRightAscension() - approximate.getRightAscension()) * 3600.0d;
		double deltaDeltaArcsec = Math
				.abs(rigorous.getDeclinaison() - approximate.getDeclinaison()) * 3600.0d;
		Assert.assertTrue("rigorous and approximate should differ for the Moon",
				deltaAlphaArcsec > 5.0d && deltaDeltaArcsec > 5.0d);
		// Regression anchors (rigorous).
		Assert.assertEquals(14.455672132383134d, rigorous.getRightAscension(), PIN);
		Assert.assertEquals(4.266643180630606d, rigorous.getDeclinaison(), PIN);
	}

	@Test
	public void testChapter29MoonTopocentricConvenience() {
		// End-to-end Moon topocentric position from Uccle (lat 50.7986, long
		// 4.36 deg east), with an explicit apparent sidereal time. The
		// topocentric position must differ from the geocentric one, but by no
		// more than the Moon's horizontal parallax (about 0.93 deg here).
		GeographicCoordinates uccle = new GeographicCoordinates(50.7986d, -4.3589d);
		EquatorialCoordinates geocentric = underTest.moonApparentEquatorialCoordinates(2444214.5d);
		EquatorialCoordinates topocentric = underTest.moonTopocentricEquatorialCoordinates(
				2444214.5d, uccle, 105.0d, 6.0d);

		double parallax = underTest.moonEquatorialHorizontalParallaxe(underTest.T(2444214.5d));
		double dAlpha = Math.abs(topocentric.getRightAscension() - geocentric.getRightAscension());
		double dDelta = Math.abs(topocentric.getDeclinaison() - geocentric.getDeclinaison());
		Assert.assertTrue("topocentric should differ from geocentric", dAlpha > 1e-3 || dDelta > 1e-3);
		Assert.assertTrue("shift cannot exceed the horizontal parallax",
				dAlpha < parallax + 0.2d && dDelta < parallax + 0.2d);
		// Regression anchors.
		Assert.assertEquals(115.17408968280652d, topocentric.getRightAscension(), PIN);
		Assert.assertEquals(17.734214368923983d, topocentric.getDeclinaison(), PIN);
	}
}
