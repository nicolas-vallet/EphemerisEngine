package com.nzv.astro.ephemeris.coordinate.adapter;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.IEquatorialCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.HorizontalCoordinates;

public class HorizontalCoordinatesAdapter implements IEquatorialCoordinates {

	private HorizontalCoordinates hc;

	public HorizontalCoordinatesAdapter(HorizontalCoordinates horizontalCoordinates) {
		this.hc = horizontalCoordinates;
	}

	public HorizontalCoordinates getHorizontalCoordinatesAdapter() {
		return hc;
	}

	public void setHc(HorizontalCoordinates horizontalCoordinates) {
		this.hc = horizontalCoordinates;
	}

	@Override
	public double getRightAscension() {
		throw new UnsupportedOperationException(
				"Determining right ascension depends of the observer position and the time. "
						+ "Use the getRightAscension(GeographicCoordinates, double) method instead!");
	}

	// TODO : Gérer les azimuts mesurés depuis le Nord comme référence.
	public double getRightAscension(GeographicCoordinates observerSite, double siderealTime) {
		double term1 = -sin(toRadians(hc.getAzimuth()));
		double term2 = -cos(toRadians(hc.getAzimuth())) * sin(toRadians(observerSite.getLatitude()))
				+ tan(toRadians(hc.getElevation())) * cos(toRadians(observerSite.getLatitude()));
		double alpha = (siderealTime * 15) - observerSite.getLongitude()
				- toDegrees(atan2(term1, term2));
		return alpha % 360;
	}

	@Override
	public double getDeclinaison() {
		throw new UnsupportedOperationException(
				"Determining declinaison depends of the observer position and the time. "
						+ "Use the getDeclinaison(GeographicCoordinates, double) method instead!");
	}

	public double getDeclinaison(GeographicCoordinates observerSite, double siderealTime) {
		double term1 = sin(toRadians(observerSite.getLatitude()))
				* sin(toRadians(hc.getElevation()));
		double term2 = cos(toRadians(observerSite.getLatitude()))
				* cos(toRadians(hc.getElevation())) * cos(toRadians(hc.getAzimuth()+180));
		double delta = toDegrees(asin(term1 - term2));
		return delta;
	}

}
