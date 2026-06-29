package com.nzv.astro.ephemeris.orbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.impl.EphemerisEngineImpl;

/**
 * Validates the chapter-26 parabolic-motion engine against the chapter's worked
 * example 26.a (comet Kohler 1977m).
 * <p>
 * Two-tier discipline as for chapter 25: the pure geometry is checked against the
 * book's own printed inputs and intermediates (tight), then the {@code julianDay}
 * convenience path is checked end-to-end through the library's own Sun (looser,
 * with the computed value pinned).
 */
public class ParabolicMotionTest {

	private static final EphemerisEngine ENGINE = new EphemerisEngineImpl();

	private static final double EQUINOX_1950 = 1950.0d;
	private static final double OBLIQUITY_1950 = 23.4457889d;

	// Example 26.a — comet Kohler (1977m), 1977 September 29.0 ET (IAUC 3137).
	// Verified Julian Days: observation 1977 Sep 29.0 = JD 2443415.5; time of
	// perihelion passage 1977 Nov 10.5659 = JD 2443458.0659; hence t - T = -42.5659.
	private static final double OBSERVATION_JD = 2443415.5d;
	private static final double PERIHELION_JD = 2443458.0659d;
	private static final double DAYS_SINCE_PERIHELION = -42.5659d;

	private static ParabolicElements kohler() {
		return new ParabolicElements(0.990662d, 48.7196d, 163.4799d, 181.8175d, PERIHELION_JD);
	}

	/* ===================== Barker's equation (26.1–26.5) ==================== */

	@Test
	public void wCoefficientReproducesExample26a() {
		double w = BarkerEquation.wCoefficient(0.990662d, DAYS_SINCE_PERIHELION);
		assertEquals(-1.5752927d, w, 1e-6d);
	}

	@Test
	public void barkerIterationReproducesExample26a() {
		double w = BarkerEquation.wCoefficient(0.990662d, DAYS_SINCE_PERIHELION);
		double s = BarkerEquation.solveTrueAnomalyParameterByIteration(w);
		assertEquals(-0.4866743d, s, 5e-7d);
	}

	@Test
	public void barkerClosedFormMatchesIterationAndBook() {
		double w = BarkerEquation.wCoefficient(0.990662d, DAYS_SINCE_PERIHELION);
		double sIteration = BarkerEquation.solveTrueAnomalyParameterByIteration(w);
		double sClosedForm = BarkerEquation.solveTrueAnomalyParameterClosedForm(w);
		assertEquals("closed form vs book", -0.4866743d, sClosedForm, 5e-7d);
		assertEquals("closed form vs iteration", sIteration, sClosedForm, 1e-9d);
	}

	@Test
	public void trueAnomalyAndRadiusReproduceExample26a() {
		double w = BarkerEquation.wCoefficient(0.990662d, DAYS_SINCE_PERIHELION);
		double s = BarkerEquation.solveTrueAnomalyParameter(w);
		double[] vr = BarkerEquation.trueAnomalyAndRadius(s, 0.990662d);
		assertEquals("v", -51.90199d, vr[0], 5e-5d);
		assertEquals("r", 1.2253022d, vr[1], 5e-7d);
	}

	@Test
	public void barkerRootHasSameSignAsTimeSincePerihelion() {
		double before = BarkerEquation.solveTrueAnomalyParameter(
				BarkerEquation.wCoefficient(0.990662d, -10d));
		double after = BarkerEquation.solveTrueAnomalyParameter(
				BarkerEquation.wCoefficient(0.990662d, +10d));
		assertTrue("negative before perihelion", before < 0d);
		assertTrue("positive after perihelion", after > 0d);
	}

	/* ===================== Geometry against the book's Sun ================== */

	@Test
	public void geocentricPositionReproducesExample26a() {
		// Sun's geocentric rectangular equatorial coordinates from the book (1950.0).
		OrbitPosition p = ParabolicMotion.geocentricPosition(kohler(), DAYS_SINCE_PERIHELION,
				-0.9973057d, -0.0857667d, -0.0371837d, OBLIQUITY_1950);
		assertEquals("alpha", 244.622064d, p.rightAscensionDegrees(), 1e-3d);
		assertEquals("delta", 20.4517d, p.declinationDegrees(), 2e-3d);
		assertEquals("Delta", 1.3025435d, p.distanceToEarthAU(), 5e-7d);
		assertEquals("r", 1.2253022d, p.radiusVectorAU(), 5e-7d);
		assertEquals("psi", 62.66d, p.elongationDegrees(), 1e-2d);
	}

	@Test
	public void cometMagnitudeReproducesExample26a() {
		// magnitude = 6.0 + 5 log Delta + 10 log r  (g = 6.0, kappa = 10).
		double m = EllipticMotion.cometTotalMagnitude(6.0d, 1.3025435d, 1.2253022d, 10.0d);
		assertEquals("magnitude", 7.5d, m, 5e-2d);
	}

	/* ===================== julianDay + light-time path ===================== */

	@Test
	public void geocentricPositionFromEngineApproximatesExample26a() {
		// Sun from chapter 19 + light-time. The comet is at Delta = 1.30 AU, so the
		// known ~3e-5 AU equinox-reduction residual is only weakly amplified and the
		// agreement with the book is good; the value is pinned as the regression anchor.
		OrbitPosition p = ParabolicMotion.lightTimeCorrected(ENGINE, OBSERVATION_JD, kohler(),
				EQUINOX_1950, OBLIQUITY_1950);
		assertEquals("alpha vs book", 244.622064d, p.rightAscensionDegrees(), 8e-3d);
		assertEquals("delta vs book", 20.4517d, p.declinationDegrees(), 8e-3d);
		// Regression anchors (engine Sun + light-time).
		assertEquals("alpha pinned", 244.617887d, p.rightAscensionDegrees(), 5e-4d);
		assertEquals("delta pinned", 20.451748d, p.declinationDegrees(), 5e-4d);
	}

	@Test
	public void lightTimeShiftsThePositionSlightly() {
		double[] sun = ENGINE.sunRectangularEquatorialCoordinates(OBSERVATION_JD, EQUINOX_1950);
		OrbitPosition geometric = ParabolicMotion.geocentricPosition(kohler(),
				kohler().daysSincePerihelion(OBSERVATION_JD), sun[0], sun[1], sun[2],
				OBLIQUITY_1950);
		OrbitPosition corrected = ParabolicMotion.lightTimeCorrected(ENGINE, OBSERVATION_JD,
				kohler(), EQUINOX_1950, OBLIQUITY_1950);
		double shift = Math.abs(corrected.rightAscensionDegrees()
				- geometric.rightAscensionDegrees());
		assertTrue("light-time produces a non-zero shift", shift > 0d);
		assertTrue("light-time shift is small", shift < 0.05d);
	}

	@Test
	public void daysSincePerihelionIsTimeMinusPerihelion() {
		assertEquals(DAYS_SINCE_PERIHELION, kohler().daysSincePerihelion(OBSERVATION_JD), 1e-9d);
		assertEquals(0d, kohler().daysSincePerihelion(PERIHELION_JD), 1e-9d);
	}
}
