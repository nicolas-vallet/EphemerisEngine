package com.nzv.astro.ephemeris;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Computes the times of rising, upper transit and setting of a celestial body
 * using the standard rising/transit/setting method. This is a supplementary
 * utility: it is not one of the chapters of the reference edition of Meeus'
 * <i>Astronomical Formulae for Calculators</i> used by this project.
 * <p>
 * This is the first-approximation form of the method: the body's apparent right
 * ascension and declination are treated as constant over the day. That is exact
 * for stars and an excellent approximation for the Sun and planets; for the
 * fast-moving Moon it should be regarded as a first estimate only.
 * <p>
 * Conventions follow the rest of the library: the body's right ascension is
 * supplied in <b>degrees</b> (divide hours by 15, or use directly the output of
 * {@link EphemerisEngine#sunApparentEquatorialCoordinates(double)}), geographic
 * longitude is measured positive <b>west</b> of Greenwich, and the Greenwich
 * sidereal time is supplied in hours.
 */
public class RiseTransitSetCalculator {

	/**
	 * Standard altitude of the apparent horizon for stars and planets (-34',
	 * accounting for atmospheric refraction), in degrees.
	 */
	public static final double STANDARD_ALTITUDE_STAR_PLANET = -0.5667d;

	/**
	 * Standard altitude of the apparent horizon for the Sun (-50', the upper limb
	 * touching the horizon plus refraction), in degrees.
	 */
	public static final double STANDARD_ALTITUDE_SUN = -0.8333d;

	/**
	 * Ratio between the rates of sidereal and solar time.
	 */
	private static final double SIDEREAL_RATE = 1.00273790935d;

	/**
	 * Computes the rising, transit and setting circumstances of a body using the
	 * standard altitude for stars and planets.
	 *
	 * @param body the body's apparent equatorial coordinates (right ascension and
	 *            declination in degrees).
	 * @param observer the observer's geographic coordinates (latitude in degrees,
	 *            longitude positive west in degrees).
	 * @param apparentSiderealTimeAtGreenwich0hInHours the apparent sidereal time at
	 *            Greenwich for 0h Universal Time of the day considered, in hours.
	 * @return the rising / transit / setting circumstances.
	 */
	public RiseTransitSet compute(EquatorialCoordinates body, GeographicCoordinates observer,
			double apparentSiderealTimeAtGreenwich0hInHours) {
		return compute(body, observer, apparentSiderealTimeAtGreenwich0hInHours,
				STANDARD_ALTITUDE_STAR_PLANET);
	}

	/**
	 * Computes the rising, transit and setting circumstances of a body for a chosen
	 * standard horizon altitude.
	 *
	 * @param body the body's apparent equatorial coordinates (right ascension and
	 *            declination in degrees).
	 * @param observer the observer's geographic coordinates (latitude in degrees,
	 *            longitude positive west in degrees).
	 * @param apparentSiderealTimeAtGreenwich0hInHours the apparent sidereal time at
	 *            Greenwich for 0h Universal Time of the day considered, in hours.
	 * @param standardAltitudeInDegrees the altitude of the body's centre at the
	 *            apparent rising/setting (for example
	 *            {@link #STANDARD_ALTITUDE_SUN}), in degrees.
	 * @return the rising / transit / setting circumstances.
	 */
	public RiseTransitSet compute(EquatorialCoordinates body, GeographicCoordinates observer,
			double apparentSiderealTimeAtGreenwich0hInHours, double standardAltitudeInDegrees) {
		double alpha = body.getRightAscension();
		double delta = body.getDeclinaison();
		double phi = observer.getLatitude();
		double longitudeWest = observer.getLongitude();
		double theta0Hours = apparentSiderealTimeAtGreenwich0hInHours;

		// Altitude of the body at upper transit.
		double transitAltitude = 90.0d - abs(phi - delta);

		// Universal Time of the upper transit (local hour angle zero).
		double transitTimeUT = normalizeHours(
				((alpha + longitudeWest) / 15.0d - theta0Hours) / SIDEREAL_RATE);

		double cosH0 = (sin(toRadians(standardAltitudeInDegrees)) - sin(toRadians(phi))
				* sin(toRadians(delta))) / (cos(toRadians(phi)) * cos(toRadians(delta)));

		if (cosH0 < -1.0d) {
			// The body never sets: it is circumpolar above the chosen horizon.
			return new RiseTransitSet(Double.NaN, transitTimeUT, Double.NaN, Double.NaN,
					Double.NaN, transitAltitude, true, false);
		}
		if (cosH0 > 1.0d) {
			// The body never rises above the chosen horizon.
			return new RiseTransitSet(Double.NaN, transitTimeUT, Double.NaN, Double.NaN,
					Double.NaN, transitAltitude, false, true);
		}

		double H0 = toDegrees(acos(cosH0));

		double riseTimeUT = normalizeHours(
				((alpha - H0 + longitudeWest) / 15.0d - theta0Hours) / SIDEREAL_RATE);
		double setTimeUT = normalizeHours(
				((alpha + H0 + longitudeWest) / 15.0d - theta0Hours) / SIDEREAL_RATE);

		// Azimuths at the horizon, measured eastwards from the north.
		double cosAz = (sin(toRadians(delta)) - sin(toRadians(standardAltitudeInDegrees))
				* sin(toRadians(phi))) / (cos(toRadians(standardAltitudeInDegrees))
				* cos(toRadians(phi)));
		cosAz = Math.max(-1.0d, Math.min(1.0d, cosAz));
		double azimuth = toDegrees(acos(cosAz));
		double riseAzimuth = azimuth;
		double setAzimuth = 360.0d - azimuth;

		return new RiseTransitSet(riseTimeUT, transitTimeUT, setTimeUT, riseAzimuth, setAzimuth,
				transitAltitude, false, false);
	}

	/**
	 * Reduces a time expressed in hours to the range [0, 24).
	 */
	private static double normalizeHours(double hours) {
		double result = hours % 24.0d;
		if (result < 0) {
			result += 24.0d;
		}
		return result;
	}
}
