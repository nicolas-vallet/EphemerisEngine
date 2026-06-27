package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

public class PrecessionImplTest {

	private static final double DELTA = 1e-9;

	private final PrecessionImpl underTest = new PrecessionImpl();

	@Test
	public void testAnnualPrecessionRatesAtEquator() {
		// At right ascension 0 and declination 0 the annual change of right
		// ascension is the precessional constant m (about 46.1") and the change
		// of declination is n (about 20.04"). Reducing over a single year and
		// converting to seconds of arc recovers those rates.
		EquatorialCoordinates origin = new EquatorialCoordinates(0, 0);
		EquatorialCoordinates oneYearLater = underTest.precess(origin, 1950.0, 1951.0);
		Assert.assertEquals(46.099099646014245, oneYearLater.getRightAscension() * 3600, 1e-6);
		Assert.assertEquals(20.042512232860243, oneYearLater.getDeclinaison() * 3600, 1e-6);
	}

	@Test
	public void testReduceRegulusFromB1950ToJ2000() {
		// Regulus at B1950.0: 10h05m42.9s, +12 12' 45". The values below are the
		// pinned high-precision output of the rigorous reduction to J2000.0.
		// (Note: this is precession only; the small residual against the
		// catalogue J2000.0 position is the star's own proper motion, which
		// belongs to chapter 16, not chapter 14.)
		double ra1950 = (10 + 5 / 60.0 + 42.9 / 3600.0) * 15;
		double de1950 = 12 + 12 / 60.0 + 45.0 / 3600.0;
		EquatorialCoordinates j2000 = underTest.precess(
				new EquatorialCoordinates(ra1950, de1950), 1950.0, 2000.0);
		Assert.assertEquals(152.09731852199525, j2000.getRightAscension(), DELTA);
		Assert.assertEquals(11.967284813347545, j2000.getDeclinaison(), DELTA);
	}

	@Test
	public void testReductionIsReversible() {
		// Precessing a position forward and then back recovers the original to
		// full numerical precision: a strong internal-consistency check on the
		// rotation.
		EquatorialCoordinates original = new EquatorialCoordinates(150.0, 12.0);
		EquatorialCoordinates forward = underTest.precess(original, 1950.0, 2000.0);
		EquatorialCoordinates back = underTest.precess(forward, 2000.0, 1950.0);
		Assert.assertEquals(original.getRightAscension(), back.getRightAscension(), 1e-5);
		Assert.assertEquals(original.getDeclinaison(), back.getDeclinaison(), 1e-5);
	}

	@Test
	public void testPrecessionalAngles() {
		double[] angles = underTest.precessionalAngles(1950.0, 2000.0);
		Assert.assertEquals(0.3201532638888889, angles[0], DELTA);
		Assert.assertEquals(0.3202082361111111, angles[1], DELTA);
		Assert.assertEquals(0.2783377777777778, angles[2], DELTA);
	}
}
