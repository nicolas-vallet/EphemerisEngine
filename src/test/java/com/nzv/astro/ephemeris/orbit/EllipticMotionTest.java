package com.nzv.astro.ephemeris.orbit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.impl.EphemerisEngineImpl;

/**
 * Validates the chapter-25 elliptic-motion engine against the chapter's two
 * worked examples and the chapter's published-ephemeris exercise.
 * <p>
 * Following the project's two-tier discipline, the pure geometry is first
 * checked against the book's own printed inputs and intermediates (tight
 * tolerances), then the {@code julianDay} convenience overloads are checked
 * end-to-end through the library's own Sun (looser tolerances, with the computed
 * value pinned as a regression anchor).
 */
public class EllipticMotionTest {

	private static final EphemerisEngine ENGINE = new EphemerisEngineImpl();

	/** Standard equinox 1950.0 and its obliquity, used by example 25.b (page 118). */
	private static final double EQUINOX_1950 = 1950.0d;
	private static final double OBLIQUITY_1950 = 23.4457889d;

	/* ===================== Example 25.a — first method ===================== */

	@Test
	public void firstMethodReproducesHeliocentricExample25a() {
		// Mercury, 1978 November 12.0 ET. Book inputs.
		double[] lbr = EllipticMotion.heliocentricEcliptic(337.053200d, 0.3870986d, 0.20563033d,
				7.004337d, 48.080736d, 259.92660d);
		assertEquals("l", 315.35697d, lbr[0], 5e-4d);
		assertEquals("b", -6.99650d, lbr[1], 5e-4d);
		assertEquals("r", 0.41571d, lbr[2], 5e-5d);
	}

	@Test
	public void firstMethodReproducesGeocentricExample25a() {
		// Heliocentric l, b, r and Sun (Theta, R) taken from the book.
		double[] geo = EllipticMotion.geocentricEcliptic(315.35697d, -6.99650d, 0.41571d,
				229.25049d, 0.98984d);
		assertEquals("lambda", 251.27086d, geo[0], 2e-3d);
		assertEquals("beta", -2.64058d, geo[1], 2e-3d);
		assertEquals("Delta", 1.09912d, geo[2], 5e-5d);
	}

	@Test
	public void firstMethodReproducesEquatorialExample25a() {
		// Full first method on the book's inputs (Theta, R, epsilon of date).
		OrbitPosition p = EllipticMotion.firstMethod(337.053200d, 0.3870986d, 0.20563033d,
				7.004337d, 48.080736d, 259.92660d, 229.25049d, 0.98984d, 23.442032d);
		assertEquals("alpha", 249.31740d, p.rightAscensionDegrees(), 2e-3d);
		assertEquals("delta", -24.74770d, p.declinationDegrees(), 2e-3d);
		assertEquals("Delta", 1.09912d, p.distanceToEarthAU(), 5e-5d);
		assertEquals("r", 0.41571d, p.radiusVectorAU(), 5e-5d);
		assertEquals("psi", 22.17d, p.elongationDegrees(), 1e-2d);
	}

	@Test
	public void firstMethodFromEngineReproducesExample25a() {
		// julianDay overload: Sun and obliquity come from the library itself.
		double jd = 2443824.5d; // 1978 November 12.0 ET
		OrbitPosition p = EllipticMotion.firstMethod(ENGINE, jd, 337.053200d, 0.3870986d,
				0.20563033d, 7.004337d, 48.080736d, 259.92660d);
		// The library reproduces Theta, R and epsilon to the book's precision, so
		// the end-to-end position matches the book closely.
		assertEquals("alpha", 249.31740d, p.rightAscensionDegrees(), 5e-3d);
		assertEquals("delta", -24.74770d, p.declinationDegrees(), 5e-3d);
		// Regression anchor at the engine's JD-derived T.
		assertEquals("alpha pinned", 249.317559d, p.rightAscensionDegrees(), 5e-4d);
		assertEquals("delta pinned", -24.747739d, p.declinationDegrees(), 5e-4d);
	}

	/* ===================== Example 25.b — second method ==================== */

	@Test
	public void gaussianConstantsReproduceExample25b() {
		double[] g = EllipticMotion.gaussianConstants(303.83085d, 10.82772d, OBLIQUITY_1950);
		// a, b, c (magnitudes) then A, B, C (angles).
		assertEquals("a", 0.98774923d, g[0], 1e-7d);
		assertEquals("b", 0.87354119d, g[1], 1e-7d);
		assertEquals("c", 0.51115287d, g[2], 1e-7d);
		assertEquals("A", 34.30847d, g[3], 1e-4d);
		assertEquals("B", -60.74191d, g[4], 1e-4d);
		assertEquals("C", -40.28610d, g[5], 1e-4d);
	}

	@Test
	public void secondMethodReproducesRectangularExample25b() {
		// 433 Eros, 1975 February 11.0 ET. M from the book (17.29550 days * n).
		OrbitalElements eros = OrbitalElements.fromPerihelionPassage(1.4579641d, 0.2227021d,
				10.82772d, 178.44991d, 303.83085d, /*perihelion*/ 0d, 0.55986565d);
		double M = 17.29550d * 0.55986565d; // = 9.683156 degrees
		double[] xyzr = EllipticMotion.heliocentricEquatorialRectangular(eros, M, OBLIQUITY_1950);
		assertEquals("x", -0.8415580d, xyzr[0], 5e-7d);
		assertEquals("y", 0.7257529d, xyzr[1], 5e-7d);
		assertEquals("z", 0.2582179d, xyzr[2], 5e-7d);
		assertEquals("r", 1.1408828d, xyzr[3], 5e-7d);
	}

	@Test
	public void secondMethodReproducesEquatorialExample25b() {
		// Geometry against the book's printed Sun X, Y, Z (1950.0).
		OrbitalElements eros = OrbitalElements.fromPerihelionPassage(1.4579641d, 0.2227021d,
				10.82772d, 178.44991d, 303.83085d, 0d, 0.55986565d);
		double M = 17.29550d * 0.55986565d;
		OrbitPosition p = EllipticMotion.secondMethod(eros, M, 0.7700006d, -0.5664014d,
				-0.2456064d, OBLIQUITY_1950);
		assertEquals("alpha", 114.182647d, p.rightAscensionDegrees(), 1e-3d);
		assertEquals("delta", 4.130d, p.declinationDegrees(), 2e-3d);
		assertEquals("Delta", 0.1751354d, p.distanceToEarthAU(), 5e-7d);
		assertEquals("r", 1.1408828d, p.radiusVectorAU(), 5e-7d);
		assertEquals("psi", 149.19d, p.elongationDegrees(), 1e-2d);
		assertEquals("phase", 26.30d, p.phaseAngleDegrees(), 1e-2d);
	}

	@Test
	public void secondMethodMinorPlanetMagnitudeReproducesExample25b() {
		double m = EllipticMotion.minorPlanetMagnitude(12.4d, 1.1408828d, 0.1751354d, 26.30d,
				0.023d);
		assertEquals("magnitude", 9.5d, m, 5e-2d);
	}

	@Test
	public void secondMethodFromEngineApproximatesExample25b() {
		// julianDay + light-time overload: Sun X, Y, Z come from chapter 19, which
		// differs from the A.E. by the known ~1e-4 AU precession residual, so the
		// agreement with the book is looser. The computed value is pinned.
		// julianDay + light-time overload: Sun X, Y, Z come from chapter 19, which
		// differs from the A.E. by the known ~1e-4 AU precession residual. Because
		// Eros is near the Earth here (Delta = 0.175 AU), that small absolute error
		// is angularly amplified to ~0.04 degree, so the agreement with the book is
		// necessarily loose; the computed value is pinned as the regression anchor.
		double jd = 2442454.5d; // 1975 February 11.0 ET
		OrbitalElements eros = OrbitalElements.fromPerihelionPassage(1.4579641d, 0.2227021d,
				10.82772d, 178.44991d, 303.83085d,
				/*perihelion JD 1975 Jan 24.70450 ET*/ 2442437.20450d, 0.55986565d);
		OrbitPosition p = EllipticMotion.secondMethodLightTimeCorrected(ENGINE, jd, eros,
				EQUINOX_1950, OBLIQUITY_1950);
		assertEquals("alpha vs book", 114.182647d, p.rightAscensionDegrees(), 5e-2d);
		assertEquals("delta vs book", 4.130d, p.declinationDegrees(), 5e-2d);
		// Regression anchors (engine Sun + light-time).
		assertEquals("alpha pinned", 114.146667d, p.rightAscensionDegrees(), 5e-4d);
		assertEquals("delta pinned", 4.142939d, p.declinationDegrees(), 5e-4d);
	}

	/* ============ Published-ephemeris exercise — minor planet 234 Barbara === */

	@Test
	public void secondMethodMatchesPublishedEphemerisForBarbara() {
		// Elements at epoch 1979 November 23.0 ET (JD 2444200.5), equinox 1950.0.
		OrbitalElements barbara = OrbitalElements.fromMeanAnomalyAtEpoch(2.3848264d, 0.2456180d,
				15.38354d, 191.11341d, 144.17952d, 2444200.5d, 34.88670d, 0.26762022d);

		// Ephemerides of Minor Planets for 1979 (Leningrad, 1978), 0h ET.
		// 1979 Sept 24: alpha 1h21.0m, delta -15deg04'.
		assertPosition(barbara, 2444140.5d, hms(1, 21.0d), dms(-15, 4d), 4e-2d);
		// 1979 Oct 14: alpha 1h08.4m, delta -19deg15'.
		assertPosition(barbara, 2444160.5d, hms(1, 8.4d), dms(-19, 15d), 4e-2d);
		// 1979 Nov 3: alpha 0h57.9m, delta -20deg17'.
		assertPosition(barbara, 2444180.5d, hms(0, 57.9d), dms(-20, 17d), 4e-2d);
	}

	private void assertPosition(OrbitalElements el, double jd, double expectedAlpha,
			double expectedDelta, double tolerance) {
		OrbitPosition p = EllipticMotion.secondMethodLightTimeCorrected(ENGINE, jd, el,
				EQUINOX_1950, OBLIQUITY_1950);
		assertEquals("alpha @ " + jd, expectedAlpha, p.rightAscensionDegrees(), tolerance);
		assertEquals("delta @ " + jd, expectedDelta, p.declinationDegrees(), tolerance);
	}

	/* ===================== Light-time and element helpers ================== */

	@Test
	public void lightTimeShiftsThePositionSlightly() {
		double jd = 2442454.5d;
		OrbitalElements eros = OrbitalElements.fromPerihelionPassage(1.4579641d, 0.2227021d,
				10.82772d, 178.44991d, 303.83085d, 2442437.20450d, 0.55986565d);
		double[] sun = ENGINE.sunRectangularEquatorialCoordinates(jd, EQUINOX_1950);
		OrbitPosition geometric = EllipticMotion.secondMethod(eros, eros.meanAnomalyAt(jd),
				sun[0], sun[1], sun[2], OBLIQUITY_1950);
		OrbitPosition corrected = EllipticMotion.secondMethodLightTimeCorrected(ENGINE, jd, eros,
				EQUINOX_1950, OBLIQUITY_1950);
		double shift = Math.abs(corrected.rightAscensionDegrees()
				- geometric.rightAscensionDegrees());
		// tau = 0.0057756 * Delta ~ 0.001 day at Delta = 0.175 AU; the position moves
		// by a small but non-zero amount.
		assertTrue("light-time produces a non-zero shift", shift > 0d);
		assertTrue("light-time shift is small", shift < 0.01d);
	}

	@Test
	public void meanAnomalyAdvancesWithMeanMotion() {
		OrbitalElements el = OrbitalElements.fromMeanAnomalyAtEpoch(2d, 0.1d, 5d, 10d, 20d,
				2444200.5d, 30d, 0.5d);
		assertEquals(30d, el.meanAnomalyAt(2444200.5d), 1e-9d);
		assertEquals(35d, el.meanAnomalyAt(2444210.5d), 1e-9d); // +10 days * 0.5/day
	}

	@Test
	public void meanMotionFromSemiMajorAxisMatchesFormula2512() {
		// Eros: a = 1.4579641 -> n ~ 0.55987 deg/day.
		double n = OrbitalElements.meanMotionFromSemiMajorAxis(1.4579641d);
		assertEquals(0.559866d, n, 5e-6d);
	}

	/* ============================ small helpers ============================ */

	/** Right ascension in degrees from hours and decimal minutes. */
	private static double hms(int hours, double minutes) {
		return (hours + minutes / 60d) * 15d;
	}

	/** Declination in degrees from (signed) degrees and arcminutes. */
	private static double dms(int degrees, double arcminutes) {
		double sign = degrees < 0 ? -1d : 1d;
		return sign * (Math.abs(degrees) + arcminutes / 60d);
	}
}
