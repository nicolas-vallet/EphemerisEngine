package com.nzv.astro.ephemeris.orbit;

import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;

/**
 * The computed geocentric position of a planet, minor planet or comet, as
 * produced by {@link EllipticMotion}.
 * <p>
 * The {@link #equatorial() equatorial coordinates} carry the right ascension
 * (in degrees) and declination (in degrees), referred to the same equinox as
 * the orbital elements that produced them (the mean equinox of the date for the
 * first method; the chosen standard equinox for the second method). The
 * remaining fields are the heliocentric distance {@code r} and geocentric
 * distance {@code Delta} (both in astronomical units), the elongation from the
 * Sun {@code psi} and the phase angle (Sun&ndash;body&ndash;Earth), both in
 * degrees and both in the range [0, 180].
 *
 * @param equatorial the geocentric right ascension (degrees) and declination
 *            (degrees).
 * @param radiusVectorAU the heliocentric distance {@code r}, in AU.
 * @param distanceToEarthAU the geocentric distance {@code Delta}, in AU.
 * @param elongationDegrees the elongation from the Sun {@code psi}, in degrees.
 * @param phaseAngleDegrees the phase angle (Sun&ndash;body&ndash;Earth), in
 *            degrees.
 */
public record OrbitPosition(EquatorialCoordinates equatorial, double radiusVectorAU,
		double distanceToEarthAU, double elongationDegrees, double phaseAngleDegrees) {

	/** Convenience accessor for the right ascension, in degrees. */
	public double rightAscensionDegrees() {
		return equatorial.getRightAscension();
	}

	/** Convenience accessor for the declination, in degrees. */
	public double declinationDegrees() {
		return equatorial.getDeclinaison();
	}
}
