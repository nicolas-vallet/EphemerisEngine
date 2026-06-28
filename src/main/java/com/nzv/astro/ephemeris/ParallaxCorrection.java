package com.nzv.astro.ephemeris;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Reduction of geocentric equatorial coordinates to topocentric coordinates
 * (the correction for parallax), following chapter 29 of Jean Meeus'
 * <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * <i>Geocentric</i> coordinates are as seen from the centre of the Earth;
 * <i>topocentric</i> coordinates are as seen from the observer's place. The
 * observer is described by the quantities &rho;&middot;sin&phi;&prime; and
 * &rho;&middot;cos&phi;&prime; (chapter 6, available from
 * {@link com.nzv.astro.ephemeris.coordinate.GeocentricCoordinates}), and the
 * body by its equatorial horizontal parallax &pi; and geocentric hour angle.
 * <p>
 * Two reductions are offered: the rigorous one of formulae (29.2)/(29.3), which
 * must be used for the Moon, and the simpler non-rigorous one of formulae
 * (29.4)/(29.5), which is adequate for the Sun, planets and comets. All angles
 * are in degrees.
 */
public final class ParallaxCorrection {

	/**
	 * The Sun's equatorial horizontal parallax at unit distance, 8&Prime;.794,
	 * expressed in degrees (used in formula 29.1).
	 */
	private static final double SOLAR_PARALLAX_AT_UNIT_DISTANCE_DEG = 8.794d / 3600.0d;

	private ParallaxCorrection() {
		// Utility class: no instances.
	}

	/**
	 * Returns the equatorial horizontal parallax of a body from its distance to
	 * the Earth (formula 29.1), suitable for the Sun, planets and comets.
	 *
	 * @param distanceInAstronomicalUnits the body's distance to the Earth, in
	 *            astronomical units.
	 * @return the equatorial horizontal parallax, in degrees.
	 */
	public static double parallaxFromDistanceInDegrees(double distanceInAstronomicalUnits) {
		return SOLAR_PARALLAX_AT_UNIT_DISTANCE_DEG / distanceInAstronomicalUnits;
	}

	/**
	 * Reduces geocentric equatorial coordinates to topocentric ones using the
	 * rigorous formulae (29.2) and (29.3). This is the form required for the
	 * Moon.
	 *
	 * @param rightAscension the geocentric right ascension &alpha;, in degrees.
	 * @param declination the geocentric declination &delta;, in degrees.
	 * @param parallaxInDegrees the body's equatorial horizontal parallax &pi;, in
	 *            degrees.
	 * @param rhoSinPhiPrime the observer's &rho;&middot;sin&phi;&prime;
	 *            (chapter 6).
	 * @param rhoCosPhiPrime the observer's &rho;&middot;cos&phi;&prime;
	 *            (chapter 6).
	 * @param geocentricHourAngleInDegrees the body's geocentric hour angle H, in
	 *            degrees.
	 * @return the topocentric coordinates (right ascension normalised to
	 *         [0, 360) degrees, declination in degrees).
	 */
	public static EquatorialCoordinates topocentric(double rightAscension, double declination,
			double parallaxInDegrees, double rhoSinPhiPrime, double rhoCosPhiPrime,
			double geocentricHourAngleInDegrees) {
		double H = toRadians(geocentricHourAngleInDegrees);
		double sinPi = sin(toRadians(parallaxInDegrees));
		double delta = toRadians(declination);

		// Parallax in right ascension (formula 29.2).
		double numeratorAlpha = -rhoCosPhiPrime * sinPi * sin(H);
		double denominator = cos(delta) - rhoCosPhiPrime * sinPi * cos(H);
		double deltaAlpha = atan2(numeratorAlpha, denominator);

		// Topocentric declination computed directly (formula 29.3).
		double numeratorDelta = (sin(delta) - rhoSinPhiPrime * sinPi) * cos(deltaAlpha);
		double deltaPrime = toDegrees(atan2(numeratorDelta, denominator));

		double alphaPrime = normalizeDegrees(rightAscension + toDegrees(deltaAlpha));
		return new EquatorialCoordinates(alphaPrime, deltaPrime);
	}

	/**
	 * Reduces geocentric equatorial coordinates to topocentric ones using the
	 * non-rigorous formulae (29.4) and (29.5). Adequate for the Sun, planets and
	 * comets, but not for the Moon.
	 *
	 * @param rightAscension the geocentric right ascension &alpha;, in degrees.
	 * @param declination the geocentric declination &delta;, in degrees.
	 * @param parallaxInDegrees the body's equatorial horizontal parallax &pi;, in
	 *            degrees.
	 * @param rhoSinPhiPrime the observer's &rho;&middot;sin&phi;&prime;
	 *            (chapter 6).
	 * @param rhoCosPhiPrime the observer's &rho;&middot;cos&phi;&prime;
	 *            (chapter 6).
	 * @param geocentricHourAngleInDegrees the body's geocentric hour angle H, in
	 *            degrees.
	 * @return the topocentric coordinates (right ascension normalised to
	 *         [0, 360) degrees, declination in degrees).
	 */
	public static EquatorialCoordinates topocentricApproximate(double rightAscension,
			double declination, double parallaxInDegrees, double rhoSinPhiPrime,
			double rhoCosPhiPrime, double geocentricHourAngleInDegrees) {
		double H = toRadians(geocentricHourAngleInDegrees);
		double delta = toRadians(declination);

		// Formula (29.4): parallax in right ascension.
		double deltaAlpha = -parallaxInDegrees * rhoCosPhiPrime * sin(H) / cos(delta);
		// Formula (29.5): parallax in declination.
		double deltaDelta = -parallaxInDegrees
				* (rhoSinPhiPrime * cos(delta) - rhoCosPhiPrime * cos(H) * sin(delta));

		double alphaPrime = normalizeDegrees(rightAscension + deltaAlpha);
		return new EquatorialCoordinates(alphaPrime, declination + deltaDelta);
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
