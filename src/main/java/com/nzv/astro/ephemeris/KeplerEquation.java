package com.nzv.astro.ephemeris;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Solving the equation of Kepler, following chapter 22 of Jean Meeus'
 * <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * The equation of Kepler is {@code E = M + e sin E} (formula 22.1), where
 * {@code e} is the orbital eccentricity, {@code M} the mean anomaly and
 * {@code E} the eccentric anomaly. It is transcendental and cannot be solved in
 * closed form, so {@code E} is found iteratively. To allow the work to be done
 * in degrees rather than radians, the chapter rewrites it as
 * {@code E = M + e0 sin E} (formula 22.2) with the "modified" eccentricity
 * {@code e0 = e * 180/pi}; all the methods below work in degrees.
 * <p>
 * Three methods are offered, matching the chapter:
 * <ul>
 * <li>{@link #solveEccentricAnomaly(double, double)} — Newton's correction
 * (formula 22.3, the chapter's "second method"): converges quickly for every
 * eccentricity and is the recommended general-purpose entry point;</li>
 * <li>{@link #solveEccentricAnomalyByIteration(double, double, double)} — the
 * simple fixed-point iteration (the chapter's "first method"): very simple, but
 * slow to converge for large eccentricities;</li>
 * <li>{@link #approximateEccentricAnomaly(double, double)} — the closed-form
 * approximation (formula 22.4, the "third method"): valid only for small
 * eccentricities.</li>
 * </ul>
 */
public final class KeplerEquation {

	private static final double DEFAULT_TOLERANCE_DEGREES = 1e-9d;
	private static final int MAX_ITERATIONS = 200;

	private KeplerEquation() {
		// Utility class: no instances.
	}

	/**
	 * Solves the equation of Kepler for the eccentric anomaly using Newton's
	 * correction (formula 22.3), to a default accuracy of 1e-9 degree. This is
	 * the recommended method: it converges quickly for every eccentricity in
	 * [0, 1).
	 *
	 * @param meanAnomalyDegrees the mean anomaly M, in degrees.
	 * @param eccentricity the orbital eccentricity e (0 ≤ e &lt; 1).
	 * @return the eccentric anomaly E, in degrees.
	 */
	public static double solveEccentricAnomaly(double meanAnomalyDegrees, double eccentricity) {
		return solveEccentricAnomaly(meanAnomalyDegrees, eccentricity, DEFAULT_TOLERANCE_DEGREES);
	}

	/**
	 * Solves the equation of Kepler using Newton's correction (formula 22.3) to a
	 * caller-specified accuracy. The fraction added at each step is a correction
	 * to the previous value; iteration stops once its absolute value drops below
	 * {@code toleranceDegrees}.
	 *
	 * @param meanAnomalyDegrees the mean anomaly M, in degrees.
	 * @param eccentricity the orbital eccentricity e (0 ≤ e &lt; 1).
	 * @param toleranceDegrees the convergence threshold on the correction, in
	 *            degrees.
	 * @return the eccentric anomaly E, in degrees.
	 */
	public static double solveEccentricAnomaly(double meanAnomalyDegrees, double eccentricity,
			double toleranceDegrees) {
		double modifiedEccentricity = toDegrees(eccentricity); // e0 = e * 180/pi
		double E = meanAnomalyDegrees; // first approximation E0 = M
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			double correction = (meanAnomalyDegrees + modifiedEccentricity * sin(toRadians(E)) - E)
					/ (1 - eccentricity * cos(toRadians(E)));
			E += correction;
			if (abs(correction) < toleranceDegrees) {
				break;
			}
		}
		return E;
	}

	/**
	 * Solves the equation of Kepler with the simple fixed-point iteration of the
	 * chapter's first method: {@code E(n+1) = M + e0 sin E(n)}, starting from
	 * {@code E0 = M}. Iteration stops once two successive values differ by less
	 * than {@code toleranceDegrees}. This method is very simple but converges
	 * slowly for large eccentricities, where {@link #solveEccentricAnomaly} is
	 * preferable.
	 *
	 * @param meanAnomalyDegrees the mean anomaly M, in degrees.
	 * @param eccentricity the orbital eccentricity e (0 ≤ e &lt; 1).
	 * @param toleranceDegrees the convergence threshold between successive
	 *            iterates, in degrees.
	 * @return the eccentric anomaly E, in degrees.
	 */
	public static double solveEccentricAnomalyByIteration(double meanAnomalyDegrees,
			double eccentricity, double toleranceDegrees) {
		double modifiedEccentricity = toDegrees(eccentricity);
		double E = meanAnomalyDegrees;
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			double next = meanAnomalyDegrees + modifiedEccentricity * sin(toRadians(E));
			double delta = next - E;
			E = next;
			if (abs(delta) < toleranceDegrees) {
				break;
			}
		}
		return E;
	}

	/**
	 * Returns the closed-form approximation of the eccentric anomaly (formula
	 * 22.4): {@code tan E = sin M / (cos M - e)}. This is valid only for small
	 * eccentricities; the error grows quickly with e (about 0.16 degree at
	 * e = 0.25, and far larger beyond that).
	 *
	 * @param meanAnomalyDegrees the mean anomaly M, in degrees.
	 * @param eccentricity the orbital eccentricity e.
	 * @return the approximate eccentric anomaly E, in degrees, in the range
	 *         [0, 360).
	 */
	public static double approximateEccentricAnomaly(double meanAnomalyDegrees,
			double eccentricity) {
		double M = toRadians(meanAnomalyDegrees);
		double E = toDegrees(atan2(sin(M), cos(M) - eccentricity));
		double result = E % 360d;
		if (result < 0) {
			result += 360d;
		}
		return result;
	}
}
