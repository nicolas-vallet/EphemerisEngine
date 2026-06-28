package com.nzv.astro.ephemeris.impl;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.ApparentPlace;
import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.Precession;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Implementation of the apparent-place reduction of chapter 16 of Jean Meeus'
 * <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * This class is a composition of pieces that already exist in the library: it
 * delegates precession to a {@link Precession} and reads nutation, the mean
 * obliquity of the ecliptic and the Sun's true longitude from an
 * {@link EphemerisEngine}. Only the proper-motion step and the nutation /
 * aberration reductions onto right ascension and declination are new here.
 */
public class ApparentPlaceImpl implements ApparentPlace {

	/**
	 * The constant of aberration, in arcseconds.
	 */
	public static final double ABERRATION_CONSTANT = 20.49552d;

	/**
	 * The library's fundamental epoch 1900.0 expressed as a Julian day.
	 */
	private static final double EPOCH_1900_JD = 2415020.0d;

	/**
	 * Length of the Julian year in days, used to convert a Julian day to a decimal
	 * year (the equinox of date) for precession and proper motion.
	 */
	private static final double JULIAN_YEAR = 365.25d;

	private final EphemerisEngine engine;
	private final Precession precession;

	public ApparentPlaceImpl(EphemerisEngine engine, Precession precession) {
		this.engine = engine;
		this.precession = precession;
	}

	@Override
	public EquatorialCoordinates apparentPlace(EquatorialCoordinates meanCatalogue,
			double catalogueEpoch, double muAlpha, double muDelta, double julianDay) {
		double epochOfDate = decimalYearOf(julianDay);
		double years = epochOfDate - catalogueEpoch;

		// 1. carry the mean place forward by proper motion (still referred to the
		// catalogue equinox).
		EquatorialCoordinates moved = applyProperMotion(meanCatalogue, muAlpha, muDelta, years);

		// 2. precess to the mean equinox of date (chapter 14).
		EquatorialCoordinates meanOfDate = precession.precess(moved, catalogueEpoch, epochOfDate);

		// 3. add the nutation and aberration corrections to obtain the apparent
		// place.
		double[] dNutation = nutationCorrection(meanOfDate, julianDay);
		double[] dAberration = aberrationCorrection(meanOfDate, julianDay);

		double alpha = normalizeDegrees(
				meanOfDate.getRightAscension() + dNutation[0] + dAberration[0]);
		double delta = meanOfDate.getDeclinaison() + dNutation[1] + dAberration[1];
		return new EquatorialCoordinates(alpha, delta);
	}

	@Override
	public EquatorialCoordinates applyProperMotion(EquatorialCoordinates mean, double muAlpha,
			double muDelta, double years) {
		double alpha = mean.getRightAscension() + (muAlpha * years) / 3600.0d;
		double delta = mean.getDeclinaison() + (muDelta * years) / 3600.0d;
		return new EquatorialCoordinates(normalizeDegrees(alpha), delta);
	}

	@Override
	public double[] nutationCorrection(EquatorialCoordinates mean, double julianDay) {
		double T = engine.T(julianDay);
		double deltaPsi = engine.getNutationInLongitude(julianDay); // arcseconds
		double deltaEps = engine.getNutationInObliquity(julianDay); // arcseconds
		double epsilon = trueObliquity(T, deltaEps); // degrees

		double a = toRadians(mean.getRightAscension());
		double d = toRadians(mean.getDeclinaison());
		double eps = toRadians(epsilon);

		// Meeus, chapter 16: nutation in right ascension and declination, in
		// arcseconds (deltaPsi and deltaEps are themselves in arcseconds).
		double dAlpha = (cos(eps) + sin(eps) * sin(a) * tan(d)) * deltaPsi
				- (cos(a) * tan(d)) * deltaEps;
		double dDelta = (sin(eps) * cos(a)) * deltaPsi + sin(a) * deltaEps;

		return new double[] { dAlpha / 3600.0d, dDelta / 3600.0d };
	}

	@Override
	public double[] aberrationCorrection(EquatorialCoordinates mean, double julianDay) {
		double T = engine.T(julianDay);
		double deltaEps = engine.getNutationInObliquity(julianDay);
		double epsilon = trueObliquity(T, deltaEps);

		double sunLongitude = engine.sunTrueLongitude(T); // true geometric longitude
		double e = earthOrbitEccentricity(T);
		double perihelion = earthPerihelionLongitude(T);

		double a = toRadians(mean.getRightAscension());
		double d = toRadians(mean.getDeclinaison());
		double eps = toRadians(epsilon);
		double sun = toRadians(sunLongitude);
		double pi = toRadians(perihelion);
		double k = ABERRATION_CONSTANT;

		// Meeus, chapter 16: the leading terms use the Sun's true longitude; the
		// terms multiplied by the eccentricity e use the longitude of the
		// perihelion of the Earth's orbit.
		double dAlpha = -k * (cos(a) * cos(sun) * cos(eps) + sin(a) * sin(sun)) / cos(d)
				+ e * k * (cos(a) * cos(pi) * cos(eps) + sin(a) * sin(pi)) / cos(d);

		double dDelta = -k
				* (cos(sun) * cos(eps) * (tan(eps) * cos(d) - sin(a) * sin(d))
						+ cos(a) * sin(d) * sin(sun))
				+ e * k * (cos(pi) * cos(eps) * (tan(eps) * cos(d) - sin(a) * sin(d))
						+ cos(a) * sin(d) * sin(pi));

		return new double[] { dAlpha / 3600.0d, dDelta / 3600.0d };
	}

	/**
	 * The true obliquity of the ecliptic, in degrees: the mean obliquity (chapter
	 * 18) plus the nutation in obliquity.
	 */
	private double trueObliquity(double T, double deltaEpsArcsec) {
		return engine.meanObliquityOfEcliptic(T) + deltaEpsArcsec / 3600.0d;
	}

	/**
	 * Eccentricity of the Earth's orbit (Meeus, chapter 18), dimensionless.
	 */
	private double earthOrbitEccentricity(double T) {
		return 0.01675104d - 0.0000418d * T - 0.000000126d * T * T;
	}

	/**
	 * Longitude of the perihelion of the Earth's orbit, in degrees. It equals the
	 * Sun's mean longitude minus the Sun's mean anomaly (the longitude of the
	 * Sun's perigee), about 281.22 degrees at the fundamental epoch.
	 */
	private double earthPerihelionLongitude(double T) {
		return normalizeDegrees(engine.sunMeanLongitude(T) - engine.sunMeanAnomaly(T));
	}

	/**
	 * Converts a Julian day to a decimal year measured from the fundamental epoch
	 * 1900.0, used as the equinox of date.
	 */
	private double decimalYearOf(double julianDay) {
		return 1900.0d + (julianDay - EPOCH_1900_JD) / JULIAN_YEAR;
	}

	private double normalizeDegrees(double degrees) {
		double d = degrees % 360.0d;
		if (d < 0) {
			d += 360.0d;
		}
		return d;
	}
}
