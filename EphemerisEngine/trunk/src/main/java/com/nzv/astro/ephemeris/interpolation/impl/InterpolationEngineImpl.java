package com.nzv.astro.ephemeris.interpolation.impl;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.analysis.solvers.PolynomialSolver;

import com.nzv.astro.ephemeris.interpolation.InterpolationData;
import com.nzv.astro.ephemeris.interpolation.InterpolationEngine;
import com.nzv.astro.ephemeris.interpolation.InterpolationException;

public class InterpolationEngineImpl implements InterpolationEngine {

	@Override
	public double interpolate(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, double searchValue) throws InterpolationException {
		double a = input2.getY() - input1.getY();
		double b = input3.getY() - input2.getY();
		double c = b - a;
		double n = searchValue - input2.getX();

		double result1 = input2.getY() + (n / 2) * (a + b + (n * c));
		return result1;
	}

	@Override
	public double interpolate(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, InterpolationData input4, InterpolationData input5,
			double searchValue) throws InterpolationException {
		double A = input2.getY() - input1.getY();
		double B = input3.getY() - input2.getY();
		double C = input4.getY() - input3.getY();
		double D = input5.getY() - input4.getY();
		double E = B - A;
		double F = C - B;
		double G = D - C;
		double H = F - E;
		double J = G - F;
		double K = J - H;

		double interval = abs(input2.getX() - input1.getX());
		double n = (searchValue - input3.getX()) / interval;

		double result = input3.getY() + n * (((B + C) / 2) - ((H + J) / 12)) + pow(n, 2)
				* ((F / 2) - (K / 24)) + pow(n, 3) * ((H + J) / 12) + pow(n, 4) * (K / 24);
		return result;
	}

	@Override
	public InterpolationData findExtremum(InterpolationData input1, InterpolationData input2,
			InterpolationData input3) throws InterpolationException {
		double a = input2.getY() - input1.getY();
		double b = input3.getY() - input2.getY();
		double c = b - a;

		double ym = input2.getY() - pow((a + b), 2) / (8 * c);
		double nm = -(a + b) / (2 * c);
		double interval = abs(input2.getX() - input1.getX());
		double xm = input2.getX() + (interval * nm);

		InterpolationData result = new InterpolationData(xm, ym);
		return result;
	}

	@Override
	public InterpolationData findExtremum(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, InterpolationData input4, InterpolationData input5)
			throws InterpolationException {
		double A = input2.getY() - input1.getY();
		double B = input3.getY() - input2.getY();
		double C = input4.getY() - input3.getY();
		double D = input5.getY() - input4.getY();
		double E = B - A;
		double F = C - B;
		double G = D - C;
		double H = F - E;
		double J = G - F;
		double K = J - H;

		double n = Double.MAX_VALUE;
		double n0 = 0;
		while (n - n0 != 0) {
			n = n0;
			n0 = (6 * B + 6 * C - H - J + 3 * pow(n0, 2) * (H + J) + 2 * pow(n0, 3) * K)
					/ (K - 12 * F);
		}

		double interval = abs(input2.getX() - input1.getX());
		double x = input3.getX() + (n0 * interval);
		double y = input3.getY() + n0 * (((B + C) / 2) - ((H + J) / 12)) + pow(n0, 2)
				* ((F / 2) - (K / 24)) + pow(n0, 3) * ((H + J) / 12) + pow(n0, 4) * (K / 24);

		return new InterpolationData(x, y);
	}

	@Override
	public double findZero(InterpolationData input1, InterpolationData input2,
			InterpolationData input3) throws InterpolationException {
		double a = input2.getY() - input1.getY();
		double b = input3.getY() - input2.getY();
		double c = b - a;

		double n = Double.MAX_VALUE;
		double n0 = 0;
		while (n - n0 != 0) {
			n = n0;
			n0 = -(2 * input2.getY()) / (a + b + (c * n));
		}

		double interval = abs(input2.getX() - input1.getX());
		double result = input2.getX() + (interval * n0);
		return result;
	}

	@Override
	public double findZero(InterpolationData input1, InterpolationData input2,
			InterpolationData input3, InterpolationData input4, InterpolationData input5)
			throws InterpolationException {

		double A = input2.getY() - input1.getY();
		double B = input3.getY() - input2.getY();
		double C = input4.getY() - input3.getY();
		double D = input5.getY() - input4.getY();
		double E = B - A;
		double F = C - B;
		double G = D - C;
		double H = F - E;
		double J = G - F;
		double K = J - H;

		double n = Double.MAX_VALUE;
		double n0 = 0;
		while (n - n0 != 0) {
			n = n0;
			n0 = (-24 * input3.getY() + pow(n, 2) * (K - 12 * F) - 2 * pow(n, 3) * (H + J) - pow(n,
					4) * K)
					/ (2 * (6 * B + 6 * C - H - J));
		}

		double interval = abs(input2.getX() - input1.getX());
		double result = input3.getX() + (interval * n0);
		return result;
	}

	@Override
	public double interpolate(List<InterpolationData> samples, double searchValueFor)
			throws InterpolationException {
		PolynomialFunctionLagrangeForm poly = findPolynomialFunctionLagrange(samples);
		double result = poly.value(searchValueFor);
		
		PolynomialFunction pf = new PolynomialFunction(poly.getCoefficients());
		System.out.println("DEBUG => "+pf.toString());
		
		PolynomialFunction derivate = pf.polynomialDerivative();
		System.out.println("DERIVEE => " + derivate.toString());
		return result;
	}

	@Override
	public InterpolationData findExtremum(List<InterpolationData> samples)
			throws InterpolationException {
		// TODO
		return null;
	}


	@Override
	public double findZero(List<InterpolationData> samples) throws InterpolationException {
		PolynomialFunctionLagrangeForm poly = findPolynomialFunctionLagrange(samples);
		PolynomialFunction pf = new PolynomialFunction(poly.getCoefficients());
		PolynomialSolver solver = new LaguerreSolver();
		double result = solver.solve(100, pf, 0);
		return result;
	}

	private PolynomialFunctionLagrangeForm findPolynomialFunctionLagrange(List<InterpolationData> samples) {
		double[] x = new double[samples.size()];
		double[] y = new double[samples.size()];
		for (int i = 0; i < samples.size(); i++) {
			x[i] = samples.get(i).getX();
			y[i] = samples.get(i).getY();
		}
		PolynomialFunctionLagrangeForm poly = new PolynomialFunctionLagrangeForm(x, y);
		return poly;
	}
}
