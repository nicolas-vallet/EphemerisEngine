package com.nzv.astro.ephemeris.impl;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.Constants;
import com.nzv.astro.ephemeris.EphemerisEngine;

public class EphemerisEngineImpl implements EphemerisEngine {

	private final com.nzv.astro.ephemeris.lunar.MoonPositionModel moonModel;

	/** Creates an engine using the default Moon model (AFFC chapter 30, epoch 1900.0). */
	public EphemerisEngineImpl() {
		this(com.nzv.astro.ephemeris.lunar.MoonModels.AFFC_1900.getModel());
	}

	/**
	 * Creates an engine using the supplied Moon model, allowing a higher-precision model to be
	 * substituted for the chapter-30 default.
	 */
	public EphemerisEngineImpl(com.nzv.astro.ephemeris.lunar.MoonPositionModel moonModel) {
		this.moonModel = moonModel;
	}

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
		double e = earthOrbitEccentricity(T);
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


	@Override
	public double moonGeocentricLongitude(double T) {
		return moonModel.geocentricLongitude(T);
	}

	@Override
	public double moonGeocentricLatitude(double T) {
		return moonModel.geocentricLatitude(T);
	}

	@Override
	public double moonEquatorialHorizontalParallaxe(double T) {
		return moonModel.horizontalParallax(T);
	}

	@Override
	public com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates moonApparentEquatorialCoordinates(
			double julianDay) {
		double T = T(julianDay);
		// Apparent longitude of date: true longitude plus the nutation in longitude.
		double lambda = moonModel.geocentricLongitude(T) + getNutationInLongitude(julianDay) / 3600.0d;
		double beta = moonModel.geocentricLatitude(T);
		// True obliquity of date.
		double epsilon = meanObliquityOfEcliptic(T) + getNutationInObliquity(julianDay) / 3600.0d;
		double l = toRadians(lambda);
		double b = toRadians(beta);
		double e = toRadians(epsilon);
		double alpha = toDegrees(atan2(sin(l) * cos(e) - tan(b) * sin(e), cos(l)));
		double delta = toDegrees(asin(sin(b) * cos(e) + cos(b) * sin(e) * sin(l)));
		return new com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates(
				normalizeDegrees(alpha), delta);
	}

	@Override
	public com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates moonGeocentricEclipticCoordinates(
			double julianDay) {
		double T = T(julianDay);
		return new com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates(
				moonModel.geocentricLongitude(T), moonModel.geocentricLatitude(T));
	}

	@Override
	public double moonDistanceToEarthInKilometers(double julianDay) {
		return earthDistanceToMoonInKilometers(moonModel.horizontalParallax(T(julianDay)));
	}


	// =====================================================================
	// Chapter 31 - Illuminated Fraction of the Moon's Disk
	// =====================================================================

	@Override
	public double moonPhaseAngle(double julianDay) {
		double T = T(julianDay);
		// The Sun's TRUE longitude is used (not the apparent one): the apparent
		// longitude already folds in nutation and aberration, which would be
		// double-counted in the Sun-Moon elongation.
		double sunLongitude = sunTrueLongitude(T);
		double moonLongitude = moonGeocentricLongitude(T);
		double moonLatitude = moonGeocentricLatitude(T);
		double sunMeanAnomaly = normalizeDegrees(sunMeanAnomaly(T));
		double moonMeanAnomaly = normalizeDegrees(moonMeanAnomaly(T));
		return phaseAngleFromCoordinates(sunLongitude, moonLongitude, moonLatitude,
				sunMeanAnomaly, moonMeanAnomaly);
	}

	@Override
	public double moonIlluminatedFraction(double julianDay) {
		double i = moonPhaseAngle(julianDay);
		return (1 + cos(toRadians(i))) / 2.0d;
	}

	@Override
	public double moonPhaseAngleApproximate(double julianDay) {
		// Lower-accuracy phase angle from the mean elements alone, neglecting the
		// Moon's latitude (chapter 31, formula 31.4).
		double T = T(julianDay);
		double D = normalizeDegrees(moonMeanElongation(T));
		double M = normalizeDegrees(sunMeanAnomaly(T));
		double Mprime = normalizeDegrees(moonMeanAnomaly(T));
		double i = 180.0d - D
				- 6.289d * sin(toRadians(Mprime))
				+ 2.100d * sin(toRadians(M))
				- 1.274d * sin(toRadians(2 * D - Mprime))
				- 0.658d * sin(toRadians(2 * D))
				- 0.214d * sin(toRadians(2 * Mprime))
				- 0.112d * sin(toRadians(D));
		return i;
	}

	/**
	 * Computes the Moon's phase angle (degrees) from the Sun's longitude, the
	 * Moon's longitude and latitude, and the mean anomalies of Sun and Moon, by
	 * means of chapter-31 formulae (31.2) and (31.3). Exposed so the geometry can
	 * be validated against externally supplied coordinates (for example the book
	 * worked example, which feeds A.E. positions).
	 *
	 * @param sunLongitude the Sun's true longitude, degrees.
	 * @param moonLongitude the Moon's geocentric longitude, degrees.
	 * @param moonLatitude the Moon's geocentric latitude, degrees.
	 * @param sunMeanAnomaly the Sun's mean anomaly, degrees.
	 * @param moonMeanAnomaly the Moon's mean anomaly, degrees.
	 * @return the phase angle, degrees.
	 */
	public static double phaseAngleFromCoordinates(double sunLongitude, double moonLongitude,
			double moonLatitude, double sunMeanAnomaly, double moonMeanAnomaly) {
		// Geocentric elongation d of the Moon from the Sun (formula 31.2).
		double d = toDegrees(acos(
				cos(toRadians(moonLongitude - sunLongitude)) * cos(toRadians(moonLatitude))));
		// Phase angle i (formula 31.3).
		double factor = (1 - 0.0549d * sin(toRadians(moonMeanAnomaly)))
				/ (1 - 0.0167d * sin(toRadians(sunMeanAnomaly)));
		return 180.0d - d - 0.1468d * factor * sin(toRadians(d));
	}

	// =====================================================================
	// Chapter 13 - Position Angle of the Moon's Bright Limb
	// =====================================================================

	@Override
	public double moonBrightLimbPositionAngle(double julianDay) {
		com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates sun =
				sunApparentEquatorialCoordinates(julianDay);
		com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates moon =
				moonApparentEquatorialCoordinates(julianDay);
		return brightLimbPositionAngle(sun.getRightAscension(), sun.getDeclinaison(),
				moon.getRightAscension(), moon.getDeclinaison());
	}

	/**
	 * Computes the position angle of the Moon's bright limb (degrees, in the
	 * range [0, 360)) from the equatorial coordinates of the Sun and the Moon
	 * (chapter 13). All four arguments are in degrees. The quadrant is resolved
	 * with atan2, as the chapter recommends.
	 *
	 * @param sunRightAscension the Sun's right ascension, degrees.
	 * @param sunDeclination the Sun's declination, degrees.
	 * @param moonRightAscension the Moon's right ascension, degrees.
	 * @param moonDeclination the Moon's declination, degrees.
	 * @return the position angle of the bright limb, degrees in [0, 360).
	 */
	public static double brightLimbPositionAngle(double sunRightAscension, double sunDeclination,
			double moonRightAscension, double moonDeclination) {
		double deltaAlpha = toRadians(sunRightAscension - moonRightAscension);
		double numerator = cos(toRadians(sunDeclination)) * sin(deltaAlpha);
		double denominator = cos(toRadians(moonDeclination)) * sin(toRadians(sunDeclination))
				- sin(toRadians(moonDeclination)) * cos(toRadians(sunDeclination)) * cos(deltaAlpha);
		double chi = toDegrees(atan2(numerator, denominator));
		return normalizeDegrees(chi);
	}

	// =====================================================================
	// Chapter 19 - Rectangular Coordinates of the Sun
	// =====================================================================

	@Override
	public double[] sunRectangularEquatorialCoordinates(double julianDay) {
		double T = T(julianDay);
		double theta = sunTrueLongitude(T);
		double epsilon = meanObliquityOfEcliptic(T);
		double R = sunRadiusVector(T);
		double X = R * cos(toRadians(theta));
		double Y = R * sin(toRadians(theta)) * cos(toRadians(epsilon));
		double Z = R * sin(toRadians(theta)) * sin(toRadians(epsilon));
		return new double[] { X, Y, Z };
	}

	@Override
	public double[] sunRectangularEquatorialCoordinates(double julianDay, double targetEquinox) {
		double[] ofDate = sunRectangularEquatorialCoordinates(julianDay);
		double X0 = ofDate[0];
		double Y0 = ofDate[1];
		double Z0 = ofDate[2];

		// Equinox of date, expressed as a decimal year, used as the starting
		// equinox of the precessional reduction (the same convention used for the
		// apparent place of a star in chapter 16).
		double epochOfDate = 1900.0d + (julianDay - 2415020.0d) / 365.25d;
		double[] angles = new PrecessionImpl().precessionalAngles(epochOfDate, targetEquinox);
		double zeta = toRadians(angles[0]);
		double z = toRadians(angles[1]);
		double theta = toRadians(angles[2]);

		// Rotation matrix elements (chapter 19, formula 19.2).
		double Xx = cos(zeta) * cos(z) * cos(theta) - sin(zeta) * sin(z);
		double Xy = sin(zeta) * cos(z) + cos(zeta) * sin(z) * cos(theta);
		double Xz = cos(zeta) * sin(theta);
		double Yx = -cos(zeta) * sin(z) - sin(zeta) * cos(z) * cos(theta);
		double Yy = cos(zeta) * cos(z) - sin(zeta) * sin(z) * cos(theta);
		double Yz = -sin(zeta) * sin(theta);
		double Zx = -cos(z) * sin(theta);
		double Zy = -sin(z) * sin(theta);
		double Zz = cos(theta);

		double X = Xx * X0 + Yx * Y0 + Zx * Z0;
		double Y = Xy * X0 + Yy * Y0 + Zy * Z0;
		double Z = Xz * X0 + Yz * Y0 + Zz * Z0;
		return new double[] { X, Y, Z };
	}

	// =====================================================================
	// Chapter 20 - Equinoxes and Solstices
	// =====================================================================

	@Override
	public double equinoxSolsticeJulianDay(int year, com.nzv.astro.ephemeris.Season season) {
		int k = season.getK();
		// First approximation (formula 20.1).
		double julianDay = (year + k / 4.0d) * 365.2422d + 1721141.3d;
		double correction;
		int iterations = 0;
		do {
			double T = T(julianDay);
			double apparentLongitude = sunApparentLongitude(T);
			// Correction towards the instant where the apparent longitude equals
			// k * 90 degrees (formula 20.2). The sine naturally absorbs the 360
			// degree wrap, so no explicit normalisation of the difference is needed.
			correction = 58.0d * sin(toRadians(k * 90.0d - apparentLongitude));
			julianDay += correction;
			iterations++;
		} while (Math.abs(correction) > 1e-7d && iterations < 50);
		return julianDay;
	}

	// =====================================================================
	// Chapter 32 - Phases of the Moon
	// =====================================================================

	@Override
	public double moonPhaseJulianDay(double year, com.nzv.astro.ephemeris.MoonPhase phase) {
		double fraction = phase.getFraction();
		// Approximate lunation number (formula 32.2), then snap to the nearest
		// value carrying the required phase fraction.
		double kApprox = (year - 1900.0d) * 12.3685d;
		double k = Math.round(kApprox - fraction) + fraction;
		double T = k / 1236.85d; // formula 32.3

		// Mean phase (formula 32.1).
		double julianDay = 2415020.75933d + 29.53058868d * k + 0.0001178d * T * T
				- 0.000000155d * T * T * T
				+ 0.00033d * sin(toRadians(166.56d + 132.87d * T - 0.009173d * T * T));

		// Mean anomalies and argument of latitude (chapter 32).
		double M = normalizeDegrees(
				359.2242d + 29.10535608d * k - 0.0000333d * T * T - 0.00000347d * T * T * T);
		double Mprime = normalizeDegrees(
				306.0253d + 385.81691806d * k + 0.0107306d * T * T + 0.00001236d * T * T * T);
		double F = normalizeDegrees(
				21.2964d + 390.67050646d * k - 0.0016528d * T * T - 0.00000239d * T * T * T);

		double correction;
		boolean quarter = (fraction == 0.25d || fraction == 0.75d);
		if (quarter) {
			correction = quarterPhaseCorrection(T, M, Mprime, F);
			if (fraction == 0.25d) {
				correction += 0.0028d - 0.0004d * cos(toRadians(M)) + 0.0003d * cos(toRadians(Mprime));
			} else {
				correction += -0.0028d + 0.0004d * cos(toRadians(M)) - 0.0003d * cos(toRadians(Mprime));
			}
		} else {
			correction = newFullPhaseCorrection(T, M, Mprime, F);
		}
		return julianDay + correction;
	}

	/** Periodic correction (days) for New and Full Moon (formula 32.4). */
	private static double newFullPhaseCorrection(double T, double M, double Mprime, double F) {
		double m = toRadians(M);
		double mp = toRadians(Mprime);
		double f = toRadians(F);
		return (0.1734d - 0.000393d * T) * sin(m)
				+ 0.0021d * sin(2 * m)
				- 0.4068d * sin(mp)
				+ 0.0161d * sin(2 * mp)
				- 0.0004d * sin(3 * mp)
				+ 0.0104d * sin(2 * f)
				- 0.0051d * sin(m + mp)
				- 0.0074d * sin(m - mp)
				+ 0.0004d * sin(2 * f + m)
				- 0.0004d * sin(2 * f - m)
				- 0.0006d * sin(2 * f + mp)
				+ 0.0010d * sin(2 * f - mp)
				+ 0.0005d * sin(m + 2 * mp);
	}

	/** Periodic correction (days) for First and Last Quarter (formula 32.5). */
	private static double quarterPhaseCorrection(double T, double M, double Mprime, double F) {
		double m = toRadians(M);
		double mp = toRadians(Mprime);
		double f = toRadians(F);
		return (0.1721d - 0.0004d * T) * sin(m)
				+ 0.0021d * sin(2 * m)
				- 0.6280d * sin(mp)
				+ 0.0089d * sin(2 * mp)
				- 0.0004d * sin(3 * mp)
				+ 0.0079d * sin(2 * f)
				- 0.0119d * sin(m + mp)
				- 0.0047d * sin(m - mp)
				+ 0.0003d * sin(2 * f + m)
				- 0.0004d * sin(2 * f - m)
				- 0.0006d * sin(2 * f + mp)
				+ 0.0021d * sin(2 * f - mp)
				+ 0.0003d * sin(m + 2 * mp)
				+ 0.0004d * sin(m - 2 * mp)
				- 0.0003d * sin(2 * m + mp);
	}


	// =====================================================================
	// Chapter 21 - Equation of Time
	// =====================================================================

	@Override
	public double equationOfTime(double julianDay) {
		double T = T(julianDay);
		double epsilon = meanObliquityOfEcliptic(T);
		double L = sunMeanLongitude(T);
		double M = sunMeanAnomaly(T);
		double e = earthOrbitEccentricity(T);
		double y = Math.pow(tan(toRadians(epsilon / 2.0d)), 2);
		double Lr = toRadians(L);
		double Mr = toRadians(M);

		// W. M. Smart's series (formula 21.1); E is obtained in radians.
		double E = y * sin(2 * Lr)
				- 2 * e * sin(Mr)
				+ 4 * e * y * sin(Mr) * cos(2 * Lr)
				- 0.5d * y * y * sin(4 * Lr)
				- 1.25d * e * e * sin(2 * Mr);

		// Radians -> degrees -> minutes of time (1 degree = 4 minutes of time).
		return toDegrees(E) * 4.0d;
	}

	/**
	 * Computes the equation of time (in minutes of time) from values that are
	 * tabulated in the Astronomical Ephemeris, using the relation given at the
	 * head of chapter 21. Exposed so the chapter's worked example, which feeds
	 * A.E. values, can be reproduced exactly.
	 *
	 * @param apparentSiderealTimeAtGreenwich0hUTInHours apparent sidereal time at
	 *            Greenwich for 0h UT, in hours.
	 * @param sunApparentRightAscension0hETInHours the Sun's apparent right
	 *            ascension at 0h ET, in hours.
	 * @param deltaTInSeconds the difference ET - UT, in seconds.
	 * @return the equation of time, in minutes of time.
	 */
	public static double equationOfTimeFromApparentValues(
			double apparentSiderealTimeAtGreenwich0hUTInHours,
			double sunApparentRightAscension0hETInHours, double deltaTInSeconds) {
		double eHours = 12.0d + apparentSiderealTimeAtGreenwich0hUTInHours
				- sunApparentRightAscension0hETInHours
				- (0.002738d * deltaTInSeconds) / 3600.0d;
		// The equation of time is small; fold the result into (-12, 12] hours so
		// inputs straddling the 0h/24h boundary still give the right value.
		eHours = eHours % 24.0d;
		if (eHours > 12.0d) {
			eHours -= 24.0d;
		} else if (eHours < -12.0d) {
			eHours += 24.0d;
		}
		return eHours * 60.0d;
	}

	// =====================================================================
	// Chapter 29 - Correction for Parallax
	// =====================================================================

	@Override
	public com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates moonTopocentricEquatorialCoordinates(
			double julianDay, com.nzv.astro.ephemeris.coordinate.GeographicCoordinates observer,
			double observerHeightInMeters, double apparentSiderealTimeAtGreenwichInHours) {
		double T = T(julianDay);
		com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates geocentric =
				moonApparentEquatorialCoordinates(julianDay);
		double alpha = geocentric.getRightAscension();
		double delta = geocentric.getDeclinaison();
		double parallax = moonEquatorialHorizontalParallaxe(T);

		com.nzv.astro.ephemeris.coordinate.GeocentricCoordinates observerGeocentric =
				new com.nzv.astro.ephemeris.coordinate.GeocentricCoordinates(
						observer.getLatitude(), (float) observerHeightInMeters);

		// Geocentric hour angle H = theta0 - longitude(west) - alpha, in degrees.
		double H = apparentSiderealTimeAtGreenwichInHours * 15.0d - observer.getLongitude() - alpha;

		return com.nzv.astro.ephemeris.ParallaxCorrection.topocentric(alpha, delta, parallax,
				observerGeocentric.getRhoSinPhiPrime(), observerGeocentric.getRhoCosPhiPrime(), H);
	}

	/**
	 * Returns the eccentricity of the Earth's orbit for a given instant
	 * (chapter 18). Shared by the radius vector and the equation of time.
	 */
	private static double earthOrbitEccentricity(double T) {
		return 0.01675104d - 0.0000418d * T - 0.000000126d * T * T;
	}

}
