package com.nzv.astro.ephemeris.coordinate;

import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * Great-circle angular separation and relative position angle between two
 * celestial bodies, following chapter 9 of Jean Meeus' <i>Astronomical Formulae
 * for Calculators</i>.
 * <p>
 * Both bodies are given by their equatorial coordinates (right ascension and
 * declination expressed in degrees). The computation is purely geometric and
 * works equally well with any spherical coordinate pair (for example ecliptic
 * longitude/latitude) as long as both bodies use the same system.
 */
public final class AngularSeparation {

	private AngularSeparation() {
		// Utility class: no instances.
	}

	/**
	 * Returns the angular separation between two bodies given their equatorial
	 * coordinates.
	 *
	 * @param body1 the first body (right ascension and declination in degrees).
	 * @param body2 the second body (right ascension and declination in degrees).
	 * @return the angular separation, expressed in degrees in the range [0, 180].
	 */
	public static double between(EquatorialCoordinates body1, EquatorialCoordinates body2) {
		return between(body1.getRightAscension(), body1.getDeclinaison(),
				body2.getRightAscension(), body2.getDeclinaison());
	}

	/**
	 * Returns the angular separation between two bodies given their spherical
	 * coordinates.
	 *
	 * @param longitude1 first body abscissa (right ascension or longitude), degrees.
	 * @param latitude1 first body ordinate (declination or latitude), degrees.
	 * @param longitude2 second body abscissa (right ascension or longitude), degrees.
	 * @param latitude2 second body ordinate (declination or latitude), degrees.
	 * @return the angular separation, expressed in degrees in the range [0, 180].
	 */
	public static double between(double longitude1, double latitude1, double longitude2,
			double latitude2) {
		double d1 = toRadians(latitude1);
		double d2 = toRadians(latitude2);
		double deltaLon = toRadians(longitude1 - longitude2);
		double cosD = sin(d1) * sin(d2) + cos(d1) * cos(d2) * cos(deltaLon);
		// Guard against round-off pushing the cosine just outside [-1, 1].
		cosD = Math.max(-1.0d, Math.min(1.0d, cosD));
		return toDegrees(acos(cosD));
	}

	/**
	 * Returns the position angle of {@code body2} with respect to {@code body1},
	 * measured eastwards (counter-clockwise) from the north.
	 *
	 * @param body1 the reference body (right ascension and declination in degrees).
	 * @param body2 the second body (right ascension and declination in degrees).
	 * @return the position angle, expressed in degrees in the range [0, 360).
	 */
	public static double positionAngle(EquatorialCoordinates body1, EquatorialCoordinates body2) {
		double a1 = toRadians(body1.getRightAscension());
		double d1 = toRadians(body1.getDeclinaison());
		double a2 = toRadians(body2.getRightAscension());
		double d2 = toRadians(body2.getDeclinaison());

		double deltaAlpha = a2 - a1;
		double numerator = sin(deltaAlpha);
		double denominator = cos(d1) * Math.tan(d2) - sin(d1) * cos(deltaAlpha);
		double p = toDegrees(atan2(numerator, denominator));
		if (p < 0) {
			p += 360d;
		}
		return p;
	}
}
