package com.nzv.astro.ephemeris.impl;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MoonPhase;
import com.nzv.astro.ephemeris.Season;
import com.nzv.astro.ephemeris.Sexagesimal;

/**
 * Tests for Phase 3, step 1 of the implementation plan: the derived Sun and Moon
 * phenomena of chapters 31 (illuminated fraction), 13 (bright-limb position
 * angle), 32 (phases of the Moon), 20 (equinoxes and solstices) and 19
 * (rectangular coordinates of the Sun).
 * <p>
 * Each chapter is validated in two tiers: a loose check against the relevant
 * Meeus worked example (the trustworthy anchor) and a pinned high-precision
 * value produced by this implementation (the regression anchor). Where the book
 * example feeds coordinates taken from the Astronomical Ephemeris rather than
 * self-computed ones, the underlying geometry is exercised directly with those
 * same A.E. coordinates so a transcription slip is isolated from a model
 * difference.
 */
public class PhaseThreeStepOneTest {

	private static final double PIN = 1e-9;
	private final EphemerisEngineImpl underTest = new EphemerisEngineImpl();

	// =====================================================================
	// Chapter 31 - Illuminated fraction of the Moon's disk
	// =====================================================================

	@Test
	public void testChapter31GeometryFromBookCoordinates() {
		// Example 31.a feeds A.E. coordinates for 1979 December 25.0:
		// Sun longitude 272 deg 35' 23", Moon longitude 346 deg 39' 01",
		// Moon latitude -1 deg 22' 54", with M = 350.32, M' = 357.20.
		double sunLongitude = 272 + 35 / 60.0d + 23 / 3600.0d;
		double moonLongitude = 346 + 39 / 60.0d + 1 / 3600.0d;
		double moonLatitude = -(1 + 22 / 60.0d + 54 / 3600.0d);
		double i = EphemerisEngineImpl.phaseAngleFromCoordinates(sunLongitude, moonLongitude,
				moonLatitude, 350.32d, 357.20d);
		// Book: phase angle i = 105.794 deg, illuminated fraction k = 0.36.
		Assert.assertEquals(105.794d, i, 0.01d);
		double k = (1 + Math.cos(Math.toRadians(i))) / 2.0d;
		Assert.assertEquals(0.3639d, k, 0.001d);
	}

	@Test
	public void testChapter31EndToEnd() {
		// Fully self-computed pipeline for 1979 December 25.0 (JD 2444232.5).
		double jd = 2444232.5d;
		Assert.assertEquals(105.79326167892809d, underTest.moonPhaseAngle(jd), PIN);
		Assert.assertEquals(0.36391645867222494d, underTest.moonIlluminatedFraction(jd), PIN);
		// Rounds to the book value 0.36.
		Assert.assertEquals(0.36d, underTest.moonIlluminatedFraction(jd), 0.005d);
	}

	@Test
	public void testChapter31LowAccuracy() {
		// Example 31.b, neglecting the Moon's latitude (formula 31.4): i = 105.843.
		double jd = 2444232.5d;
		double i = underTest.moonPhaseAngleApproximate(jd);
		Assert.assertEquals(105.8423225138199d, i, PIN);
		Assert.assertEquals(105.843d, i, 0.01d);
	}

	// =====================================================================
	// Chapter 13 - Position angle of the Moon's bright limb
	// =====================================================================

	@Test
	public void testChapter13GeometryFromBookCoordinates() {
		// Example 13.a, A.E. coordinates for 1979 February 2 at 21h ET:
		// Sun (alpha = 315.8930, delta = -16.7915),
		// Moon (alpha' = 28.5757, delta' = +8.0299). Book chi = 250.38.
		double chi = EphemerisEngineImpl.brightLimbPositionAngle(315.8930d, -16.7915d, 28.5757d,
				8.0299d);
		Assert.assertEquals(250.38d, chi, 0.01d);
	}

	@Test
	public void testChapter13EndToEnd() {
		// Self-computed Sun and Moon for 1979 February 2 at 21h ET.
		double jd = JulianDay.getJulianDayFromDateAsDouble(1979.0202, new Sexagesimal(21, 0, 0));
		double chi = underTest.moonBrightLimbPositionAngle(jd);
		Assert.assertEquals(250.3769891240866d, chi, PIN);
		// Within a few hundredths of a degree of the book's 250.38.
		Assert.assertEquals(250.38d, chi, 0.05d);
	}

	// =====================================================================
	// Chapter 19 - Rectangular coordinates of the Sun
	// =====================================================================

	@Test
	public void testChapter19MeanEquinoxOfDate() {
		// Example 19.a, 1978 November 12 at 0h ET = JD 2443824.5.
		// Book values referred to the mean equinox of date.
		double[] xyz = underTest.sunRectangularEquatorialCoordinates(2443824.5d);
		Assert.assertArrayEquals(new double[] { -0.646121d, -0.687981d, -0.298316d }, xyz, 5e-4d);
		// Regression anchors.
		Assert.assertEquals(-0.6461195053847167d, xyz[0], PIN);
		Assert.assertEquals(-0.6879794670414162d, xyz[1], PIN);
		Assert.assertEquals(-0.2983146911524013d, xyz[2], PIN);
	}

	@Test
	public void testChapter19ReducedTo1950() {
		// Example 19.a reduced to the standard equinox of 1950.0.
		// A.E. values: -0.6513639, -0.6838057, -0.2965014.
		double[] xyz = underTest.sunRectangularEquatorialCoordinates(2443824.5d, 1950.0d);
		Assert.assertArrayEquals(new double[] { -0.6513639d, -0.6838057d, -0.2965014d }, xyz, 5e-4d);
		// Regression anchors.
		Assert.assertEquals(-0.6513783136860328d, xyz[0], PIN);
		Assert.assertEquals(-0.6837942769231526d, xyz[1], PIN);
		Assert.assertEquals(-0.2964953567481586d, xyz[2], PIN);
	}

	// =====================================================================
	// Chapter 20 - Equinoxes and solstices
	// =====================================================================

	@Test
	public void testChapter20SeptemberEquinox1979() {
		// Example 20.a: the September equinox of 1979 falls at JD 2444140.137.
		double jd = underTest.equinoxSolsticeJulianDay(1979, Season.SEPTEMBER_EQUINOX);
		Assert.assertEquals(2444140.137d, jd, 0.01d);
		Assert.assertEquals(2444140.136945118d, jd, PIN);
	}

	@Test
	public void testChapter20AllSeasonsAreOrdered() {
		// The four events of a year occur in calendar order and span the year.
		double march = underTest.equinoxSolsticeJulianDay(1979, Season.MARCH_EQUINOX);
		double june = underTest.equinoxSolsticeJulianDay(1979, Season.JUNE_SOLSTICE);
		double september = underTest.equinoxSolsticeJulianDay(1979, Season.SEPTEMBER_EQUINOX);
		double december = underTest.equinoxSolsticeJulianDay(1979, Season.DECEMBER_SOLSTICE);
		Assert.assertTrue(march < june);
		Assert.assertTrue(june < september);
		Assert.assertTrue(september < december);
		// Consecutive events are roughly a quarter of a year apart.
		Assert.assertEquals(91.0d, june - march, 5.0d);
		Assert.assertEquals(94.0d, september - june, 5.0d);
		Assert.assertEquals(89.0d, december - september, 5.0d);
	}

	// =====================================================================
	// Chapter 32 - Phases of the Moon
	// =====================================================================

	@Test
	public void testChapter32NewMoonFebruary1977() {
		// Example 32.a: New Moon of February 1977 at JD 2443192.6523.
		double jd = underTest.moonPhaseJulianDay(1977.13, MoonPhase.NEW_MOON);
		Assert.assertEquals(2443192.6523d, jd, 0.001d);
		Assert.assertEquals(2443192.6524524256d, jd, PIN);
	}

	@Test
	public void testChapter32LastQuarterNovember1952() {
		// Example 32.b: Last Quarter of November 1952 at JD 2434326.1553.
		double jd = underTest.moonPhaseJulianDay(1952.88, MoonPhase.LAST_QUARTER);
		Assert.assertEquals(2434326.1553d, jd, 0.001d);
		Assert.assertEquals(2434326.1553273844d, jd, PIN);
	}

	@Test
	public void testChapter32PhasesAreOrdered() {
		// Within one lunation the four phases follow New < First < Full < Last.
		double newMoon = underTest.moonPhaseJulianDay(1977.05, MoonPhase.NEW_MOON);
		double first = underTest.moonPhaseJulianDay(1977.13, MoonPhase.FIRST_QUARTER);
		double full = underTest.moonPhaseJulianDay(1977.16, MoonPhase.FULL_MOON);
		double last = underTest.moonPhaseJulianDay(1977.20, MoonPhase.LAST_QUARTER);
		Assert.assertTrue(newMoon < first);
		Assert.assertTrue(first < full);
		Assert.assertTrue(full < last);
	}
}
