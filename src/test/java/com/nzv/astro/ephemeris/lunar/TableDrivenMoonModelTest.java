package com.nzv.astro.ephemeris.lunar;

import org.junit.Assert;
import org.junit.Test;

/**
 * Validates the AFFC-1900 table-driven Moon model against Meeus' worked Example 30.a
 * (1979 Dec. 7.0 ET, T = +0.7993018480).
 */
public class TableDrivenMoonModelTest {

	private static final double ARCSEC = 1.0 / 3600.0;
	// T from JD 2444214.5 (1979 Dec. 7.0 ET), matching EphemerisEngine.T(jd) to full precision;
	// the book prints the rounded value +0.7993018480.
	private static final double T_EXAMPLE = (2444214.5 - 2415020.0) / 36525.0;

	private final MoonPositionModel model = MoonModels.AFFC_1900.getModel();

	@Test
	public void testMetadata() {
		Assert.assertEquals("affc-1900", model.id());
		Assert.assertEquals(1900.0, model.epochYear(), 1e-9);
	}

	@Test
	public void testLongitudeMatchesBookExample() {
		// Book Example 30.a: lambda = 113.6604 (the abridged theory is good to ~10").
		Assert.assertEquals(113.6604, model.geocentricLongitude(T_EXAMPLE), 0.001);
	}

	@Test
	public void testLatitudeMatchesAstronomicalEphemeris() {
		// The correctly summed table reproduces the Astronomical Ephemeris value
		// beta = -3 09'49".22; the book's printed example B carries a ~2" slip.
		double aeBeta = -(3 + 9 / 60.0 + 49.22 / 3600.0);
		Assert.assertEquals(aeBeta, model.geocentricLatitude(T_EXAMPLE), 3 * ARCSEC);
	}

	@Test
	public void testParallaxMatchesBookExample() {
		Assert.assertEquals(0.930249, model.horizontalParallax(T_EXAMPLE), 1 * ARCSEC);
	}

	@Test
	public void testPinnedRegressionValues() {
		Assert.assertEquals(113.660331336, model.geocentricLongitude(T_EXAMPLE), 1e-6);
		Assert.assertEquals(-3.163668282, model.geocentricLatitude(T_EXAMPLE), 1e-6);
		Assert.assertEquals(0.930249314, model.horizontalParallax(T_EXAMPLE), 1e-6);
	}

	@Test
	public void testPhysicalRanges() {
		double T = 0.5; // arbitrary instant
		double lambda = model.geocentricLongitude(T);
		Assert.assertTrue("lambda normalised", lambda >= 0.0 && lambda < 360.0);
		Assert.assertTrue("latitude bounded", Math.abs(model.geocentricLatitude(T)) < 6.0);
		double pi = model.horizontalParallax(T);
		Assert.assertTrue("parallax in lunar range", pi > 0.88 && pi < 1.03);
	}
}
