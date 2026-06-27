package com.nzv.astro.ephemeris;

import org.junit.Assert;
import org.junit.Test;

public class StellarMagnitudesTest {

	private static final double DELTA = 1e-9;

	@Test
	public void testCombinedMagnitudeOfDoubleStar() {
		// Chapter 38: two components of magnitudes 1.96 and 2.89 appear combined
		// as a single star of magnitude about 1.58.
		Assert.assertEquals(1.5757527397308575,
				StellarMagnitudes.combinedMagnitude(1.96, 2.89), DELTA);
		Assert.assertEquals(1.58, StellarMagnitudes.combinedMagnitude(1.96, 2.89), 0.01);
	}

	@Test
	public void testBrightnessRatioForOneMagnitude() {
		// A difference of one magnitude corresponds to Pogson's ratio, the fifth
		// root of 100 (about 2.512).
		Assert.assertEquals(2.51188643150958, StellarMagnitudes.brightnessRatio(0.0, 1.0), DELTA);
		Assert.assertEquals(StellarMagnitudes.POGSON_RATIO,
				StellarMagnitudes.brightnessRatio(0.0, 1.0), DELTA);
	}

	@Test
	public void testMagnitudeDifferenceForRatioHundred() {
		// A brightness ratio of 100 is exactly five magnitudes.
		Assert.assertEquals(5.0, StellarMagnitudes.magnitudeDifference(100.0), DELTA);
	}

	@Test
	public void testCombinedMagnitudeOfThreeEqualStars() {
		// Three stars of magnitude 5 combine to 5 - 2.5*log10(3) ~ 3.807.
		Assert.assertEquals(3.807196863200844,
				StellarMagnitudes.combinedMagnitude(5.0, 5.0, 5.0), DELTA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeRatioRejected() {
		StellarMagnitudes.magnitudeDifference(-1.0);
	}
}
