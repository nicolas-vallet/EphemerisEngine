package com.nzv.astro.ephemeris.lunar;

import java.util.function.DoubleUnaryOperator;

/**
 * A single periodic term of a lunar series, as immutable data: a coefficient (degrees), the
 * integer multipliers of the four fundamental arguments in the fixed order (D, M, M', F), and an
 * eccentricity power (0, 1 or 2) by which the coefficient is scaled through {@code e^ePower}.
 * <p>
 * The term contributes {@code coefficient · e^ePower · trig( nD·D + nM·M + nMprime·M' + nF·F )},
 * with the argument evaluated in degrees. The trigonometric function (sine for longitude and
 * latitude, cosine for parallax) is a property of the series, not of the term, so it is supplied
 * at evaluation time.
 */
public record PeriodicTerm(int ePower, double coefficient, int nD, int nM, int nMprime, int nF) {

	/**
	 * Evaluates this term.
	 *
	 * @param D the Moon's mean elongation, in degrees.
	 * @param M the Sun's mean anomaly, in degrees.
	 * @param Mprime the Moon's mean anomaly, in degrees.
	 * @param F the Moon's mean distance from its ascending node, in degrees.
	 * @param e the eccentricity factor of the Earth's orbit (dimensionless).
	 * @param trig the trigonometric function to apply (radians in, value out), e.g.
	 *            {@code Math::sin} or {@code Math::cos}.
	 * @return the term's contribution, in degrees.
	 */
	public double evaluate(double D, double M, double Mprime, double F, double e,
			DoubleUnaryOperator trig) {
		double argument = nD * D + nM * M + nMprime * Mprime + nF * F;
		return coefficient * Math.pow(e, ePower) * trig.applyAsDouble(Math.toRadians(argument));
	}
}
