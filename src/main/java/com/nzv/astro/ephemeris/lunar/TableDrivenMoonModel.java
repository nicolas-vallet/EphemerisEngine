package com.nzv.astro.ephemeris.lunar;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * A {@link MoonPositionModel} driven entirely by parsed coefficient tables (see
 * {@link MoonTableLoader}). The evaluation logic is generic; all numbers come from the model's
 * resource files, so a different theory is a different set of files, not different code.
 * <p>
 * For a given time {@code T} (Julian centuries from the model's epoch) the model:
 * <ol>
 * <li>evaluates the six fundamental arguments from their degree polynomials,</li>
 * <li>adds the periodic "additive" corrections to L', M, M', D and F,</li>
 * <li>computes the eccentricity factor {@code e},</li>
 * <li>sums each periodic series, and</li>
 * <li>applies any model-specific latitude post-correction (the AFFC &omega;&#8321;, &omega;&#8322;).</li>
 * </ol>
 */
public final class TableDrivenMoonModel implements MoonPositionModel {

	/** Kinds of additive correction to the mean arguments. */
	enum AdditiveKind {
		LINEAR, SIN_OMEGA, SIN_OMEGA_PLUS
	}

	/** One additive correction, applied to one or more of L', M, M', D, F. */
	record AdditiveTerm(String[] targets, double coefficient, double argConst, double argT,
			double argT2, AdditiveKind kind) {
	}

	/** One periodic series: a base value, the trig function, and its terms. */
	record Series(double baseConst, boolean addLprime, DoubleUnaryOperator trig,
			List<PeriodicTerm> terms) {
	}

	/** The corrected fundamental arguments at a given instant, plus the eccentricity factor. */
	private record Args(double Lprime, double M, double Mprime, double D, double F, double Omega,
			double e) {
	}

	private final String id;
	private final double epochYear;
	private final double[] eCoef; // e = eCoef[0] + eCoef[1] T + eCoef[2] T^2
	private final Map<String, double[]> fundamental; // symbol -> {c0,c1,c2,c3}
	private final List<AdditiveTerm> additive;
	private final Series longitude;
	private final Series latitude;
	private final Series parallax;
	private final double omega1; // latitude post-correction amplitudes (0 => none)
	private final double omega2;
	private final double omega2ArgConst;
	private final double omega2ArgT;

	TableDrivenMoonModel(String id, double epochYear, double[] eCoef,
			Map<String, double[]> fundamental, List<AdditiveTerm> additive, Series longitude,
			Series latitude, Series parallax, double omega1, double omega2, double omega2ArgConst,
			double omega2ArgT) {
		this.id = id;
		this.epochYear = epochYear;
		this.eCoef = eCoef;
		this.fundamental = fundamental;
		this.additive = additive;
		this.longitude = longitude;
		this.latitude = latitude;
		this.parallax = parallax;
		this.omega1 = omega1;
		this.omega2 = omega2;
		this.omega2ArgConst = omega2ArgConst;
		this.omega2ArgT = omega2ArgT;
	}

	@Override
	public double geocentricLongitude(double T) {
		Args a = computeArgs(T);
		double lambda = sumSeries(longitude, a);
		return normalizeDegrees(lambda);
	}

	@Override
	public double geocentricLatitude(double T) {
		Args a = computeArgs(T);
		double B = sumSeries(latitude, a);
		double w1 = omega1 * Math.cos(Math.toRadians(a.Omega()));
		double w2 = omega2 * Math.cos(Math.toRadians(a.Omega() + omega2ArgConst + omega2ArgT * T));
		return B * (1.0 - w1 - w2);
	}

	@Override
	public double horizontalParallax(double T) {
		return sumSeries(parallax, computeArgs(T));
	}

	@Override
	public double epochYear() {
		return epochYear;
	}

	@Override
	public String id() {
		return id;
	}

	private double sumSeries(Series s, Args a) {
		double value = s.baseConst() + (s.addLprime() ? a.Lprime() : 0.0);
		for (PeriodicTerm term : s.terms()) {
			value += term.evaluate(a.D(), a.M(), a.Mprime(), a.F(), a.e(), s.trig());
		}
		return value;
	}

	private Args computeArgs(double T) {
		double Lp = poly("Lprime", T);
		double M = poly("M", T);
		double Mp = poly("Mprime", T);
		double D = poly("D", T);
		double F = poly("F", T);
		double Omega = poly("Omega", T);

		// Additive corrections depend only on T and the mean Omega, so they can be accumulated
		// against the mean values without ordering concerns.
		double dLp = 0, dM = 0, dMp = 0, dD = 0, dF = 0;
		for (AdditiveTerm t : additive) {
			double corr = switch (t.kind()) {
				case LINEAR -> t.coefficient()
						* Math.sin(Math.toRadians(t.argConst() + t.argT() * T + t.argT2() * T * T));
				case SIN_OMEGA -> t.coefficient() * Math.sin(Math.toRadians(Omega));
				case SIN_OMEGA_PLUS -> t.coefficient()
						* Math.sin(Math.toRadians(Omega + t.argConst() + t.argT() * T));
			};
			for (String target : t.targets()) {
				switch (target) {
					case "Lprime" -> dLp += corr;
					case "M" -> dM += corr;
					case "Mprime" -> dMp += corr;
					case "D" -> dD += corr;
					case "F" -> dF += corr;
					default -> throw new IllegalStateException("Unknown additive target: " + target);
				}
			}
		}
		double e = eCoef[0] + eCoef[1] * T + eCoef[2] * T * T;
		return new Args(Lp + dLp, M + dM, Mp + dMp, D + dD, F + dF, Omega, e);
	}

	private double poly(String symbol, double T) {
		double[] c = fundamental.get(symbol);
		return c[0] + c[1] * T + c[2] * T * T + c[3] * T * T * T;
	}

	private static double normalizeDegrees(double degrees) {
		double d = degrees % 360.0;
		return d < 0 ? d + 360.0 : d;
	}
}
