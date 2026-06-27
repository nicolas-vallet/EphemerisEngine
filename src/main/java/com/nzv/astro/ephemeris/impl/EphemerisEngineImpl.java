package com.nzv.astro.ephemeris.impl;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.Constants;
import com.nzv.astro.ephemeris.EphemerisEngine;

public class EphemerisEngineImpl implements EphemerisEngine {

	@Override
	public double T(double julianDay) {
		double result = (julianDay - 2415020.0) / 36525;
		return result;
	}

	@Override
	public double H(double greenwichSiderealTime, double longitude, double rightAscension) {
		return ((greenwichSiderealTime * 15) - longitude - (rightAscension * 15));
	}

	@Override
	public double sunMeanLongitude(double T) {
		double L = 279.6967d + 36000.7689d * T + 0.000303 * T * T;
		return L;
	}

	@Override
	public double moonMeanLongitude(double T) {
		double Lprime = 270.4342d + 481267.8831d * T - 0.001133d * T * T + 0.0000019d * T * T * T;
		return Lprime;
	}

	@Override
	public double sunMeanAnomaly(double T) {
		double M = 358.4758d + 35999.0498d * T - 0.000150d * T * T - 0.0000033d * T * T * T;
		return M;
	}

	@Override
	public double moonMeanAnomaly(double T) {
		double Mprime = 296.1046d + 477198.8491d * T + 0.009192d * T * T + 0.0000144d * T * T * T;
		return Mprime;
	}

	@Override
	public double moonAscendantNodeLongitude(double T) {
		double omega = 259.1833d - 1934.1420d * T + 0.002078 * T * T + 0.0000022d * T * T * T;
		return omega;
	}

	@Override
	public double moonMeanElongation(double T) {
		double D = 350.737486d + 445267.1142d * T - 0.001436d * T * T + 0.0000019d * T * T * T;
		return D;
	}

	@Override
	public double moonMeanDistanceToAscendantNode(double T) {
		double F = 11.250889d + 483202.0251d * T - 0.003211d * T * T - 0.0000003 * T * T * T;
		return F;
	}

	// @Override
	// public abstract double moonGeocentricLongitude(double T);
	//
	// @Override
	// public abstract double moonGeocentricLatitude(double T);
	//
	// @Override
	// public abstract double moonEquatorialHorizontalParallaxe(double T);

	@Override
	public double earthDistanceToMoonInKilometers(double moonEquatorialHorizontalParallaxe) {
		double D = Constants.EARTH_EQUATORIAL_RADIUS_IN_KM
				/ sin(toRadians(moonEquatorialHorizontalParallaxe));
		return D;
	}

	@Override
	public double getNutationInLongitude(double julianDay) {
		double T = T(julianDay);

		// Mean longitude of the Sun
		double L = sunMeanLongitude(T);

		// Mean longitude of the Moon
		double Lprime = moonMeanLongitude(T);

		// Mean anomaly of the Sun
		double M = sunMeanAnomaly(T);

		// Mean anomaly of the Moon
		double Mprime = moonMeanAnomaly(T);

		// Moon ascendant node longitude
		double omega = moonAscendantNodeLongitude(T);

		double nutationInLongitude = -(17.2327d + 0.01737d * T) * sin(toRadians(omega))
				- (1.2729d + 0.00013d * T) * sin(toRadians(2 * L)) + 0.2088d
				* sin(toRadians(2 * omega)) - 0.2037d * sin(toRadians(2 * Lprime))
				+ (0.1261d - 0.00031d * T) * sin(toRadians(M)) + 0.0675d * sin(toRadians(Mprime))
				- (0.0497d - 0.00012d * T) * sin(toRadians(2 * L + M)) - 0.0342d
				* sin(toRadians(2 * Lprime - omega)) - 0.0261d
				* sin(toRadians(2 * Lprime + Mprime)) + 0.0214d * sin(toRadians(2 * L - M))
				- 0.0149d * sin(toRadians(2 * L - 2 * Lprime + Mprime)) + 0.0124d
				* sin(toRadians(2 * L - omega)) + 0.0114d * sin(toRadians(2 * Lprime - Mprime));
		return nutationInLongitude;
	}

	@Override
	public double getNutationInObliquity(double julianDay) {
		double T = T(julianDay);

		// Mean longitude of the Sun
		double L = sunMeanLongitude(T);

		// Mean longitude of the Moon
		double Lprime = moonMeanLongitude(T);

		// Mean anomaly of the Sun
		double M = sunMeanAnomaly(T);

		// Mean anomaly of the Moon
		double Mprime = moonMeanAnomaly(T);

		// Moon ascendant node longitude
		double omega = moonAscendantNodeLongitude(T);

		double nutationInObliquity = +(9.2100d + 0.00091d * T) * cos(toRadians(omega))
				+ (0.5522d - 0.00029d * T) * cos(toRadians(2 * L)) - 0.0904d
				* cos(toRadians(2 * omega)) + 0.0884d * cos(toRadians(2 * Lprime)) + 0.0216d
				* cos(toRadians(2 * L + M)) + 0.0183d * cos(toRadians(2 * Lprime - omega))
				+ 0.0113d * cos(toRadians(2 * Lprime + Mprime)) - 0.0093d
				* cos(toRadians(2 * L - M)) - 0.0066d * cos(toRadians(2 * L - omega));
		return nutationInObliquity;
	}

	// =====================================================================
	// Chapter 18 - Solar Coordinates
	// =====================================================================

	@Override
	public double meanObliquityOfEcliptic(double T) {
		// Meeus, Astronomical Formulae for Calculators, chapter 18.
		return 23.452294d - 0.0130125d * T - 0.00000164d * T * T + 0.000000503d * T * T * T;
	}

	@Override
	public double sunEquationOfCenter(double T) {
		double M = sunMeanAnomaly(T);
		double C = (1.919460d - 0.004789d * T - 0.000014d * T * T) * sin(toRadians(M))
				+ (0.020094d - 0.000100d * T) * sin(toRadians(2 * M))
				+ 0.000293d * sin(toRadians(3 * M));
		return C;
	}

	@Override
	public double sunTrueLongitude(double T) {
		return normalizeDegrees(sunMeanLongitude(T) + sunEquationOfCenter(T));
	}

	@Override
	public double sunTrueAnomaly(double T) {
		return normalizeDegrees(sunMeanAnomaly(T) + sunEquationOfCenter(T));
	}

	@Override
	public double sunApparentLongitude(double T) {
		// The Sun's apparent longitude is the true longitude corrected for
		// nutation and aberration. Meeus (chapter 18) gives the combined
		// correction as -0.00569 deg - 0.00479 deg * sin(omega).
		double omega = moonAscendantNodeLongitude(T);
		double apparent = sunTrueLongitude(T) - 0.00569d - 0.00479d * sin(toRadians(omega));
		return normalizeDegrees(apparent);
	}

	@Override
	public double sunRadiusVector(double T) {
		double e = 0.01675104d - 0.0000418d * T - 0.000000126d * T * T;
		double v = sunTrueAnomaly(T);
		return (1.0000002d * (1 - e * e)) / (1 + e * cos(toRadians(v)));
	}

	@Override
	public com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates sunApparentEquatorialCoordinates(
			double julianDay) {
		double T = T(julianDay);
		double lambda = sunApparentLongitude(T);
		// True obliquity of date: mean obliquity plus the nutation correction the
		// apparent longitude already accounts for (Meeus, chapter 18).
		double omega = moonAscendantNodeLongitude(T);
		double epsilon = meanObliquityOfEcliptic(T) + 0.00256d * cos(toRadians(omega));

		// The Sun's geocentric ecliptic latitude is taken as zero, so the standard
		// ecliptic-to-equatorial rotation reduces to the expressions below.
		double alpha = toDegrees(atan2(
				cos(toRadians(epsilon)) * sin(toRadians(lambda)),
				cos(toRadians(lambda))));
		double delta = toDegrees(asin(sin(toRadians(epsilon)) * sin(toRadians(lambda))));
		return new com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates(
				normalizeDegrees(alpha), delta);
	}

	/**
	 * Reduces an angle expressed in degrees to the range [0, 360).
	 */
	private static double normalizeDegrees(double degrees) {
		double result = degrees % 360d;
		if (result < 0) {
			result += 360d;
		}
		return result;
	}

}
