package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.ApparentPlace;
import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.Precession;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Tests for the apparent-place reduction of chapter 16, anchored on Theta Persei
 * reduced to the project's house date 1978-11-13 (the star and date Meeus uses
 * for this chapter's worked example).
 */
public class ApparentPlaceImplTest {

	private static final double DELTA = 1e-9;
	private static final double ARCSEC = 1.0 / 3600.0;

	private final EphemerisEngine engine = new EphemerisEngineImpl();
	private final Precession precession = new PrecessionImpl();
	private final ApparentPlace underTest = new ApparentPlaceImpl(engine, precession);

	// Theta Persei mean place at J2000.0 and its proper motion.
	private static final double THETA_PER_RA_2000 = (2 + 44 / 60.0 + 11.986 / 3600.0) * 15;
	private static final double THETA_PER_DEC_2000 = 49 + 13 / 60.0 + 42.48 / 3600.0;
	private static final double THETA_PER_MU_RA = 0.03425 * 15; // 0.03425 s/yr -> arcsec/yr
	private static final double THETA_PER_MU_DEC = -0.0895; // arcsec/yr

	@Test
	public void testProperMotionIsAPlainCoordinateRate() {
		// A proper motion of one degree per year (3600 arcsec) over two years adds
		// exactly two degrees of right ascension and nothing else; declination is
		// unaffected when its proper motion is zero.
		EquatorialCoordinates moved = underTest.applyProperMotion(
				new EquatorialCoordinates(100.0, 20.0), 3600.0, 0.0, 2.0);
		Assert.assertEquals(102.0, moved.getRightAscension(), DELTA);
		Assert.assertEquals(20.0, moved.getDeclinaison(), DELTA);
	}

	@Test
	public void testNutationReductionMatchesPublishedNutation() {
		// For 1978-11-13 the library's nutation is dPsi ~ -3.378", dEps ~ -9.321"
		// (the values quoted in the reference card). Feeding the precessed
		// mean-of-date position of Theta Persei through Meeus' chapter-16 nutation
		// reduction yields about +4.06" in right ascension and -7.09" in
		// declination; this independently checks the reduction formula and its
		// sign, not merely the code path.
		EquatorialCoordinates meanOfDate = new EquatorialCoordinates(40.687094402812,
				49.140024777303);
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		double[] correction = underTest.nutationCorrection(meanOfDate, jd);
		Assert.assertEquals(4.06 * ARCSEC, correction[0], 0.1 * ARCSEC);
		Assert.assertEquals(-7.09 * ARCSEC, correction[1], 0.1 * ARCSEC);
	}

	@Test
	public void testAberrationStaysWithinPhysicalBounds() {
		// Annual aberration cannot exceed the constant of aberration (20.5") in
		// declination, nor that constant divided by cos(dec) in the right-ascension
		// coordinate. Both corrections must be non-trivial.
		EquatorialCoordinates meanOfDate = new EquatorialCoordinates(40.687094402812,
				49.140024777303);
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		double[] correction = underTest.aberrationCorrection(meanOfDate, jd);
		double capDec = (ApparentPlaceImpl.ABERRATION_CONSTANT + 1.0) * ARCSEC;
		double capRa = capDec / Math.cos(Math.toRadians(49.14));
		Assert.assertTrue("declination aberration within bound",
				Math.abs(correction[1]) <= capDec);
		Assert.assertTrue("right ascension aberration within bound",
				Math.abs(correction[0]) <= capRa);
		Assert.assertTrue("aberration is non-trivial", Math.abs(correction[0]) > 0.5 * ARCSEC);
	}

	@Test
	public void testApparentPlaceComposesTheSteps() {
		// The high-level method must equal the manual composition: proper motion,
		// then precession to the equinox of date, then nutation plus aberration.
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		double epochOfDate = 1900.0 + (jd - 2415020.0) / 365.25;
		EquatorialCoordinates mean = new EquatorialCoordinates(THETA_PER_RA_2000,
				THETA_PER_DEC_2000);

		EquatorialCoordinates pm = underTest.applyProperMotion(mean, THETA_PER_MU_RA,
				THETA_PER_MU_DEC, epochOfDate - 2000.0);
		EquatorialCoordinates meanOfDate = precession.precess(pm, 2000.0, epochOfDate);
		double[] dn = underTest.nutationCorrection(meanOfDate, jd);
		double[] da = underTest.aberrationCorrection(meanOfDate, jd);
		double expectedRa = meanOfDate.getRightAscension() + dn[0] + da[0];
		double expectedDec = meanOfDate.getDeclinaison() + dn[1] + da[1];

		EquatorialCoordinates apparent = underTest.apparentPlace(mean, 2000.0, THETA_PER_MU_RA,
				THETA_PER_MU_DEC, jd);
		Assert.assertEquals(expectedRa, apparent.getRightAscension(), DELTA);
		Assert.assertEquals(expectedDec, apparent.getDeclinaison(), DELTA);
	}

	@Test
	public void testApparentPlaceOfThetaPersei() {
		// Pinned high-precision regression anchor for the full pipeline on the
		// house date. The apparent place differs from the mean place of date only
		// by the small nutation + aberration terms (< 0.01 degrees), as a physical
		// sanity check.
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		EquatorialCoordinates apparent = underTest.apparentPlace(
				new EquatorialCoordinates(THETA_PER_RA_2000, THETA_PER_DEC_2000), 2000.0,
				THETA_PER_MU_RA, THETA_PER_MU_DEC, jd);

		Assert.assertEquals(40.696385186502, apparent.getRightAscension(), DELTA);
		Assert.assertEquals(49.139810033960, apparent.getDeclinaison(), DELTA);

		double meanOfDateRa = 40.687094402812;
		double meanOfDateDec = 49.140024777303;
		Assert.assertEquals(meanOfDateRa, apparent.getRightAscension(), 0.01);
		Assert.assertEquals(meanOfDateDec, apparent.getDeclinaison(), 0.01);
	}
}
