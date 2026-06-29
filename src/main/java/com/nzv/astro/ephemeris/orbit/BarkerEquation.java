package com.nzv.astro.ephemeris.orbit;

import static java.lang.Math.atan;
import static java.lang.Math.cbrt;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * Solver for <i>Barker's equation</i>, {@code s^3 + 3s - W = 0} (26.3), which
 * gives the motion of a body in a parabolic orbit in chapter 26 of Jean Meeus'
 * <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * For a parabolic orbit the eccentricity is exactly 1, the semimajor axis and
 * period are infinite and the mean motion is zero, so the equation of Kepler
 * does not apply. Instead, the quantity {@code s = tan(v/2)} (with {@code v} the
 * true anomaly) is the single real root of the cubic (26.3), whose coefficient
 * {@code W} (26.1) is built from the perihelion distance and the time since
 * perihelion. The root has the same sign as {@code t - T}: negative before
 * perihelion, positive after.
 * <p>
 * The chapter gives two ways to obtain {@code s}: an iteration (26.4), which the
 * author prefers because it works without any difficulty, and a closed form
 * (26.5) after Bauschinger. Both are provided here; the iteration is the
 * recommended default.
 *
 * @see ParabolicMotion
 */
public final class BarkerEquation {

	/** Coefficient of formula (26.1): {@code W = 0.0364911624 (t - T) / (q*sqrt(q))}. */
	public static final double W_CONSTANT = 0.0364911624d;

	private BarkerEquation() {
		// Utility class: no instances.
	}

	/**
	 * The coefficient {@code W} of Barker's equation, formula (26.1).
	 *
	 * @param perihelionDistanceAU the perihelion distance {@code q}, in AU.
	 * @param daysSincePerihelion the time since perihelion {@code t - T}, in days
	 *            (negative before perihelion).
	 * @return the coefficient {@code W}.
	 */
	public static double wCoefficient(double perihelionDistanceAU, double daysSincePerihelion) {
		return W_CONSTANT * daysSincePerihelion
				/ (perihelionDistanceAU * sqrt(perihelionDistanceAU));
	}

	/**
	 * Solves Barker's equation for {@code s = tan(v/2)} — the recommended method,
	 * the iteration (26.4).
	 *
	 * @param w the coefficient {@code W} (see {@link #wCoefficient}).
	 * @return the real root {@code s = tan(v/2)}.
	 */
	public static double solveTrueAnomalyParameter(double w) {
		return solveTrueAnomalyParameterByIteration(w);
	}

	/**
	 * Solves Barker's equation by the iteration (26.4),
	 * {@code s' = (2 s^3 + W) / (3 (s^2 + 1))}, starting from {@code s = 0} and
	 * repeating until the value is stable. This is the author's preferred method.
	 *
	 * @param w the coefficient {@code W}.
	 * @return the real root {@code s = tan(v/2)}.
	 */
	public static double solveTrueAnomalyParameterByIteration(double w) {
		double s = 0d;
		for (int iteration = 0; iteration < 100; iteration++) {
			double next = (2d * s * s * s + w) / (3d * (s * s + 1d));
			if (Math.abs(next - s) < 1e-12d) {
				return next;
			}
			s = next;
		}
		return s;
	}

	/**
	 * Solves Barker's equation in closed form (26.5), after Bauschinger:
	 * {@code tan beta = 2/W}, {@code tan gamma = cbrt(tan(beta/2))},
	 * {@code s = 2 / tan(2 gamma)}. The cube root is taken of a possibly negative
	 * quantity, which {@link Math#cbrt} handles directly.
	 *
	 * @param w the coefficient {@code W}.
	 * @return the real root {@code s = tan(v/2)}.
	 */
	public static double solveTrueAnomalyParameterClosedForm(double w) {
		double beta = atan(2d / w);                                // tan beta = 2/W
		double gamma = atan(cbrt(tan(beta / 2d)));                 // tan gamma = cbrt(tan(beta/2))
		return 2d / tan(2d * gamma);                               // s = 2 / tan(2 gamma)
	}

	/**
	 * The true anomaly {@code v} (degrees) and radius vector {@code r} (AU) from
	 * the root {@code s} and the perihelion distance, formula (26.2):
	 * {@code v = 2 atan(s)}, {@code r = q (1 + s^2)}.
	 *
	 * @param s the root {@code s = tan(v/2)}.
	 * @param perihelionDistanceAU the perihelion distance {@code q}, in AU.
	 * @return {@code {v, r}} — true anomaly in degrees and radius vector in AU.
	 */
	public static double[] trueAnomalyAndRadius(double s, double perihelionDistanceAU) {
		double v = Math.toDegrees(2d * atan(s));
		double r = perihelionDistanceAU * (1d + s * s);
		return new double[] { v, r };
	}
}
