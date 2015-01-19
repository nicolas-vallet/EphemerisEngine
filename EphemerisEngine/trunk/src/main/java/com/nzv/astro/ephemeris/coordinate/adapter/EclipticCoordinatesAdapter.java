package com.nzv.astro.ephemeris.coordinate.adapter;

import com.nzv.astro.ephemeris.Constants;
import com.nzv.astro.ephemeris.coordinate.IEquatorialCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.toRadians;
import static java.lang.Math.toDegrees;

public class EclipticCoordinatesAdapter implements IEquatorialCoordinates {

	private EclipticCoordinates ec;

	public EclipticCoordinatesAdapter(EclipticCoordinates eclipticCoordinates) {
		this.ec = eclipticCoordinates;
	}

	public EclipticCoordinates getEclipticCoordinates() {
		return ec;
	}

	public void setEclipticCoordinates(EclipticCoordinates eclipticCoordinates) {
		this.ec = eclipticCoordinates;
	}

	@Override
	public double getRightAscension() {
		double term1 = sin(toRadians(ec.getEcliptiqueLongitude()))
				* cos(toRadians(Constants.ECLIPTIC_OBLIQUITY_1950.getValueAsUnits()));
		double term2 = tan(toRadians(ec.getEcliptiqueLatitude()))
				* sin(toRadians(Constants.ECLIPTIC_OBLIQUITY_1950.getValueAsUnits()));
		double term3 = cos(toRadians(ec.getEcliptiqueLongitude()));
		double alpha = toDegrees(atan2((term1 - term2), term3));
		return alpha;
	}

	@Override
	public double getDeclinaison() {
		double term1 = sin(toRadians(ec.getEcliptiqueLatitude()))
				* cos(toRadians(Constants.ECLIPTIC_OBLIQUITY_1950.getValueAsUnits()));
		double term2 = cos(toRadians(ec.getEcliptiqueLatitude()))
				* sin(toRadians(Constants.ECLIPTIC_OBLIQUITY_1950.getValueAsUnits()))
				* sin(toRadians(ec.getEcliptiqueLongitude()));
		double dec = toDegrees(asin(term1 + term2));
		return dec;
	}

}
