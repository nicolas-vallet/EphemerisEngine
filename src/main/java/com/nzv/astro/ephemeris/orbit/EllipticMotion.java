package com.nzv.astro.ephemeris.orbit;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.log10;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.KeplerEquation;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Geocentric ephemeris of a body in elliptic orbit, following chapter 25 of Jean
 * Meeus' <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * The chapter gives two methods, both implemented here as pure, stateless static
 * helpers:
 * <ul>
 * <li>The <b>first method</b> ({@link #firstMethod}) is suited to the major
 * planets. It works from elements referred to the mean equinox <i>of the
 * date</i>: it forms the body's heliocentric ecliptical longitude, latitude and
 * radius vector ({@link #heliocentricEcliptic}), combines them with the Sun's
 * geometric longitude and radius vector to obtain the geocentric ecliptical
 * coordinates ({@link #geocentricEcliptic}), and converts those to right
 * ascension and declination of the date.</li>
 * <li>The <b>second method</b> ({@link #secondMethod}) is suited to minor
 * planets and periodic comets. It works from elements referred to a standard
 * equinox: it forms the body's heliocentric rectangular <i>equatorial</i>
 * coordinates from the per-orbit Gaussian constants ({@link #gaussianConstants}),
 * adds the Sun's geocentric rectangular equatorial coordinates referred to the
 * same equinox, and reads the right ascension and declination directly.</li>
 * </ul>
 * <p>
 * The positions returned are the body's <i>geometric</i> coordinates, exactly as
 * the chapter's worked examples 25.a and 25.b produce them. The effect of
 * light-time can be applied with {@link #secondMethodLightTimeCorrected} (which
 * places the body at the retarded instant {@code t - tau} while keeping the Sun
 * at {@code t}); nutation and aberration — the remaining corrections to a fully
 * apparent place, see chapter 16 — are left to the caller.
 * <p>
 * All angles are in degrees throughout, distances in astronomical units.
 */
public final class EllipticMotion {

	/**
	 * The light-time constant of formula (25.10): {@code tau = 0.0057756 * Delta}
	 * days.
	 */
	public static final double LIGHT_TIME_CONSTANT = 0.0057756d;

	private EllipticMotion() {
		// Utility class: no instances.
	}

	/* --------------------------------------------------------------------- */
	/* Shared orbital-plane quantities (true anomaly and radius vector).      */
	/* --------------------------------------------------------------------- */

	/**
	 * Solves the orbit in its plane: from the mean anomaly and the elements,
	 * returns the true anomaly {@code v} (degrees, in [0, 360)) and the radius
	 * vector {@code r} (AU). Uses the equation of Kepler (chapter 22), then
	 * formulae (25.1) and (25.2).
	 *
	 * @param meanAnomalyDegrees the mean anomaly {@code M}, in degrees.
	 * @param eccentricity the eccentricity {@code e}.
	 * @param semiMajorAxisAU the semimajor axis {@code a}, in AU.
	 * @return {@code {v, r}} — true anomaly in degrees and radius vector in AU.
	 */
	public static double[] trueAnomalyAndRadius(double meanAnomalyDegrees,
			double eccentricity, double semiMajorAxisAU) {
		double E = KeplerEquation.solveEccentricAnomaly(meanAnomalyDegrees, eccentricity);
		double halfE = toRadians(E / 2d);
		double v = normalize(2d * toDegrees(atan2(
				sqrt(1d + eccentricity) * sin(halfE),
				sqrt(1d - eccentricity) * cos(halfE))));        // (25.1)
		double r = semiMajorAxisAU * (1d - eccentricity * cos(toRadians(E))); // (25.2)
		return new double[] { v, r };
	}

	/* --------------------------------------------------------------------- */
	/* First method (major planets, elements of the mean equinox of date).    */
	/* --------------------------------------------------------------------- */

	/**
	 * Heliocentric ecliptical coordinates of the planet (first method), formulae
	 * (25.1)&ndash;(25.6).
	 *
	 * @param meanLongitudeDegrees the mean longitude {@code L}, in degrees.
	 * @param semiMajorAxisAU the semimajor axis {@code a}, in AU.
	 * @param eccentricity the eccentricity {@code e}.
	 * @param inclinationDegrees the inclination {@code i}, in degrees.
	 * @param nodeLongitudeDegrees the longitude of the ascending node
	 *            {@code Omega}, in degrees.
	 * @param meanAnomalyDegrees the mean anomaly {@code M}, in degrees.
	 * @return {@code {l, b, r}} — heliocentric longitude (deg, [0, 360)),
	 *         latitude (deg) and radius vector (AU).
	 */
	public static double[] heliocentricEcliptic(double meanLongitudeDegrees,
			double semiMajorAxisAU, double eccentricity, double inclinationDegrees,
			double nodeLongitudeDegrees, double meanAnomalyDegrees) {
		double[] vr = trueAnomalyAndRadius(meanAnomalyDegrees, eccentricity, semiMajorAxisAU);
		double v = vr[0];
		double r = vr[1];
		double u = meanLongitudeDegrees + v - meanAnomalyDegrees - nodeLongitudeDegrees; // (25.3)
		double uRad = toRadians(u);
		double lMinusNode = toDegrees(atan2(
				cos(toRadians(inclinationDegrees)) * sin(uRad), cos(uRad)));     // (25.5)
		double l = normalize(nodeLongitudeDegrees + lMinusNode);                 // (25.4)
		double b = toDegrees(asin(sin(uRad) * sin(toRadians(inclinationDegrees)))); // (25.6)
		return new double[] { l, b, r };
	}

	/**
	 * Geocentric ecliptical coordinates from the heliocentric ones and the Sun
	 * (first method), formulae (25.7)&ndash;(25.9).
	 *
	 * @param l heliocentric longitude {@code l}, in degrees.
	 * @param b heliocentric latitude {@code b}, in degrees.
	 * @param r heliocentric radius vector {@code r}, in AU.
	 * @param sunGeometricLongitudeDegrees the Sun's geometric longitude
	 *            {@code Theta} (chapter 18), in degrees.
	 * @param sunRadiusVectorAU the Sun's radius vector {@code R} (chapter 18), in
	 *            AU.
	 * @return {@code {lambda, beta, Delta}} — geocentric longitude (deg,
	 *         [0, 360)), latitude (deg) and Earth distance (AU).
	 */
	public static double[] geocentricEcliptic(double l, double b, double r,
			double sunGeometricLongitudeDegrees, double sunRadiusVectorAU) {
		double cosB = cos(toRadians(b));
		double lMinusTheta = toRadians(l - sunGeometricLongitudeDegrees);
		double N = r * cosB * sin(lMinusTheta);
		double D = r * cosB * cos(lMinusTheta) + sunRadiusVectorAU;               // (25.7)
		double lambda = normalize(sunGeometricLongitudeDegrees + toDegrees(atan2(N, D)));
		double rSinB = r * sin(toRadians(b));
		double delta = sqrt(N * N + D * D + rSinB * rSinB);                        // (25.8)
		double beta = toDegrees(asin(rSinB / delta));                             // (25.9)
		return new double[] { lambda, beta, delta };
	}

	/**
	 * Full geocentric position by the first method, from elements of the mean
	 * equinox of the date and the Sun's geometric longitude, radius vector and
	 * the obliquity of the date. The right ascension and declination are referred
	 * to the mean equinox of the date.
	 */
	public static OrbitPosition firstMethod(double meanLongitudeDegrees, double semiMajorAxisAU,
			double eccentricity, double inclinationDegrees, double nodeLongitudeDegrees,
			double meanAnomalyDegrees, double sunGeometricLongitudeDegrees,
			double sunRadiusVectorAU, double obliquityDegrees) {
		double[] lbr = heliocentricEcliptic(meanLongitudeDegrees, semiMajorAxisAU, eccentricity,
				inclinationDegrees, nodeLongitudeDegrees, meanAnomalyDegrees);
		double[] geo = geocentricEcliptic(lbr[0], lbr[1], lbr[2],
				sunGeometricLongitudeDegrees, sunRadiusVectorAU);
		double lambda = geo[0];
		double beta = geo[1];
		double delta = geo[2];
		double r = lbr[2];
		EquatorialCoordinates eq = eclipticToEquatorial(lambda, beta, obliquityDegrees);
		double elongation = GeocentricReduction.elongation(r, delta, sunRadiusVectorAU);
		double phase = GeocentricReduction.phaseAngle(r, delta, sunRadiusVectorAU);
		return new OrbitPosition(eq, r, delta, elongation, phase);
	}

	/**
	 * Convenience overload of the first method that takes the Sun's geometric
	 * longitude, radius vector and the obliquity of the date from the engine
	 * itself, for the given instant. Produces the geometric position referred to
	 * the mean equinox of the date.
	 *
	 * @param engine the ephemeris engine providing the Sun (chapter 18).
	 * @param julianDay the instant, as a Julian Day (Ephemeris Time).
	 */
	public static OrbitPosition firstMethod(EphemerisEngine engine, double julianDay,
			double meanLongitudeDegrees, double semiMajorAxisAU, double eccentricity,
			double inclinationDegrees, double nodeLongitudeDegrees, double meanAnomalyDegrees) {
		double T = engine.T(julianDay);
		double theta = engine.sunTrueLongitude(T);   // the Sun's geometric longitude
		double R = engine.sunRadiusVector(T);
		double obliquity = engine.meanObliquityOfEcliptic(T);
		return firstMethod(meanLongitudeDegrees, semiMajorAxisAU, eccentricity, inclinationDegrees,
				nodeLongitudeDegrees, meanAnomalyDegrees, theta, R, obliquity);
	}

	/* --------------------------------------------------------------------- */
	/* Second method (minor planets and comets, standard-equinox elements).   */
	/* --------------------------------------------------------------------- */

	/**
	 * The per-orbit Gaussian constants of formula (25.13). They depend only on
	 * the node, the inclination and the obliquity, hence are constant for a whole
	 * ephemeris. The returned magnitudes {@code a, b, c} are taken positive and
	 * the angles {@code A, B, C} (degrees) are placed in the correct quadrant.
	 *
	 * @param nodeLongitudeDegrees the longitude of the ascending node
	 *            {@code Omega}, in degrees.
	 * @param inclinationDegrees the inclination {@code i}, in degrees.
	 * @param obliquityDegrees the obliquity {@code epsilon} of the elements'
	 *            equinox, in degrees.
	 * @return {@code {a, b, c, A, B, C}} — three magnitudes then three angles
	 *         (degrees).
	 */
	public static double[] gaussianConstants(double nodeLongitudeDegrees,
			double inclinationDegrees, double obliquityDegrees) {
		double node = toRadians(nodeLongitudeDegrees);
		double i = toRadians(inclinationDegrees);
		double eps = toRadians(obliquityDegrees);
		double F = cos(node);
		double G = sin(node) * cos(eps);
		double H = sin(node) * sin(eps);
		double P = -sin(node) * cos(i);
		double Q = cos(node) * cos(i) * cos(eps) - sin(i) * sin(eps);
		double R = cos(node) * cos(i) * sin(eps) + sin(i) * cos(eps);
		double A = toDegrees(atan2(F, P));
		double B = toDegrees(atan2(G, Q));
		double C = toDegrees(atan2(H, R));
		return new double[] { hypot(F, P), hypot(G, Q), hypot(H, R), A, B, C };
	}

	/**
	 * Heliocentric rectangular equatorial coordinates of the body (second
	 * method), formula (25.14), referred to the elements' standard equinox.
	 *
	 * @param elements the orbital elements.
	 * @param meanAnomalyDegrees the mean anomaly {@code M} for the instant, in
	 *            degrees.
	 * @param obliquityDegrees the obliquity of the elements' equinox, in degrees.
	 * @return {@code {x, y, z}} in AU, plus {@code r} (AU) as a fourth element.
	 */
	public static double[] heliocentricEquatorialRectangular(OrbitalElements elements,
			double meanAnomalyDegrees, double obliquityDegrees) {
		double[] g = gaussianConstants(elements.ascendingNodeLongitudeDegrees(),
				elements.inclinationDegrees(), obliquityDegrees);
		double[] vr = trueAnomalyAndRadius(meanAnomalyDegrees, elements.eccentricity(),
				elements.semiMajorAxisAU());
		double v = vr[0];
		double r = vr[1];
		double[] xyz = GeocentricReduction.heliocentricRectangular(g,
				elements.argumentOfPerihelionDegrees(), v, r);
		return new double[] { xyz[0], xyz[1], xyz[2], r };
	}

	/**
	 * Full geocentric position by the second method, from the elements, the mean
	 * anomaly for the instant, the Sun's geocentric rectangular equatorial
	 * coordinates {@code X, Y, Z} referred to the same standard equinox, and the
	 * obliquity of that equinox. Formulae (25.14)&ndash;(25.15) plus the
	 * elongation and phase angle of page 120. The right ascension and declination
	 * are referred to the elements' standard equinox.
	 */
	public static OrbitPosition secondMethod(OrbitalElements elements, double meanAnomalyDegrees,
			double sunX, double sunY, double sunZ, double obliquityDegrees) {
		double[] xyzr = heliocentricEquatorialRectangular(elements, meanAnomalyDegrees,
				obliquityDegrees);
		return GeocentricReduction.reduce(xyzr[0], xyzr[1], xyzr[2], xyzr[3], sunX, sunY, sunZ);
	}

	/**
	 * Convenience overload of the second method that takes the Sun's rectangular
	 * coordinates from the engine (chapter 19), reduced to the given standard
	 * equinox, and computes the mean anomaly from the elements for the instant.
	 *
	 * @param engine the ephemeris engine (chapter 19 Sun).
	 * @param julianDay the instant, as a Julian Day (Ephemeris Time).
	 * @param elements the orbital elements (referred to {@code standardEquinox}).
	 * @param standardEquinox the standard equinox of the elements (e.g. 1950.0).
	 * @param obliquityDegrees the obliquity of that equinox (e.g. 23.4457889 for
	 *            1950.0).
	 */
	public static OrbitPosition secondMethod(EphemerisEngine engine, double julianDay,
			OrbitalElements elements, double standardEquinox, double obliquityDegrees) {
		double[] sun = engine.sunRectangularEquatorialCoordinates(julianDay, standardEquinox);
		double M = elements.meanAnomalyAt(julianDay);
		return secondMethod(elements, M, sun[0], sun[1], sun[2], obliquityDegrees);
	}

	/**
	 * The second method with the correction for light-time (25.10) applied: the
	 * body is taken at the retarded instant {@code t - tau} while the Sun is kept
	 * at {@code t}, iterating until {@code tau} is stable. The result is the
	 * astrometric place; nutation and aberration (chapter 16) are not applied.
	 *
	 * @param engine the ephemeris engine (chapter 19 Sun).
	 * @param julianDay the instant of observation {@code t}, as a Julian Day (ET).
	 * @param elements the orbital elements (referred to {@code standardEquinox}).
	 * @param standardEquinox the standard equinox of the elements.
	 * @param obliquityDegrees the obliquity of that equinox.
	 */
	public static OrbitPosition secondMethodLightTimeCorrected(EphemerisEngine engine,
			double julianDay, OrbitalElements elements, double standardEquinox,
			double obliquityDegrees) {
		double[] sun = engine.sunRectangularEquatorialCoordinates(julianDay, standardEquinox);
		double tau = 0d;
		OrbitPosition position = null;
		for (int iteration = 0; iteration < 10; iteration++) {
			double M = elements.meanAnomalyAt(julianDay - tau);
			position = secondMethod(elements, M, sun[0], sun[1], sun[2], obliquityDegrees);
			double newTau = LIGHT_TIME_CONSTANT * position.distanceToEarthAU();
			if (Math.abs(newTau - tau) < 1e-9d) {
				tau = newTau;
				break;
			}
			tau = newTau;
		}
		return position;
	}

	/* --------------------------------------------------------------------- */
	/* Magnitudes (formula 25.16 and the minor-planet relation, page 120).    */
	/* --------------------------------------------------------------------- */

	/**
	 * Total magnitude of a comet, formula (25.16):
	 * {@code m = g + 5 log Delta + kappa log r}.
	 *
	 * @param absoluteMagnitude the comet's absolute magnitude {@code g}.
	 * @param earthDistanceAU the geocentric distance {@code Delta}, in AU.
	 * @param radiusVectorAU the heliocentric distance {@code r}, in AU.
	 * @param kappa the comet's brightness constant (typically 5 to 15).
	 * @return the total magnitude.
	 */
	public static double cometTotalMagnitude(double absoluteMagnitude, double earthDistanceAU,
			double radiusVectorAU, double kappa) {
		return absoluteMagnitude + 5d * log10(earthDistanceAU) + kappa * log10(radiusVectorAU);
	}

	/**
	 * Magnitude of a minor planet, page 120:
	 * {@code m = g + 5 log(r*Delta) + k*beta}, with {@code beta} the phase angle
	 * in degrees and {@code k} the phase coefficient (typically 0.023).
	 *
	 * @param absoluteMagnitude the absolute magnitude {@code g}.
	 * @param radiusVectorAU the heliocentric distance {@code r}, in AU.
	 * @param earthDistanceAU the geocentric distance {@code Delta}, in AU.
	 * @param phaseAngleDegrees the phase angle {@code beta}, in degrees.
	 * @param phaseCoefficient the phase coefficient {@code k}.
	 * @return the magnitude.
	 */
	public static double minorPlanetMagnitude(double absoluteMagnitude, double radiusVectorAU,
			double earthDistanceAU, double phaseAngleDegrees, double phaseCoefficient) {
		return absoluteMagnitude + 5d * log10(radiusVectorAU * earthDistanceAU)
				+ phaseCoefficient * phaseAngleDegrees;
	}

	/* --------------------------------------------------------------------- */
	/* Internal helpers.                                                      */
	/* --------------------------------------------------------------------- */

	/**
	 * Converts ecliptical longitude/latitude to equatorial right
	 * ascension/declination with formulae (8.3)/(8.4), using the supplied
	 * obliquity (of the date for the first method). Both inputs and outputs are
	 * in degrees; the right ascension is normalised to [0, 360).
	 */
	private static EquatorialCoordinates eclipticToEquatorial(double longitudeDegrees,
			double latitudeDegrees, double obliquityDegrees) {
		double lambda = toRadians(longitudeDegrees);
		double beta = toRadians(latitudeDegrees);
		double eps = toRadians(obliquityDegrees);
		double alpha = normalize(toDegrees(atan2(
				sin(lambda) * cos(eps) - tan(beta) * sin(eps), cos(lambda))));     // (8.3)
		double dec = toDegrees(asin(
				sin(beta) * cos(eps) + cos(beta) * sin(eps) * sin(lambda)));       // (8.4)
		return new EquatorialCoordinates(alpha, dec);
	}

	private static double normalize(double degrees) {
		double d = degrees % 360d;
		if (d < 0d) {
			d += 360d;
		}
		return d;
	}
}
