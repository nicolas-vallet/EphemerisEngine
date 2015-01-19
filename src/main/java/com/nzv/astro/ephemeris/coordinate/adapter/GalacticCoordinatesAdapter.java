package com.nzv.astro.ephemeris.coordinate.adapter;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.toRadians;
import static java.lang.Math.toDegrees;

import com.nzv.astro.ephemeris.coordinate.IEquatorialCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.GalacticCoordinates;

public class GalacticCoordinatesAdapter implements IEquatorialCoordinates {

	private GalacticCoordinates gc;

	public GalacticCoordinatesAdapter(GalacticCoordinates gc) {
		this.gc = gc;
	}

	public GalacticCoordinates getGalacticCoordinates() {
		return gc;
	}

	public void setGalacticCoordinates(GalacticCoordinates galacticCoordinates) {
		this.gc = galacticCoordinates;
	}

	@Override
	public double getRightAscension() {
		double term1 = sin(toRadians(gc.getGalacticLongitude() - 123d));
		double term2 = cos(toRadians(gc.getGalacticLongitude() - 123d)) * sin(toRadians(27.4d))
				- tan(toRadians(gc.getGalacticLatitude())) * cos(toRadians(27.4d));
		double y = toDegrees(atan2(term1, term2));
		double alpha = y + 12.25d;
		if (alpha < 0) {
			// Right ascension should always be positive so we change the value
			// to get one in the 0-360 interval.
			alpha += 360;
		}
		return alpha % 360;
	}

	@Override
	public double getDeclinaison() {
		double term1 = sin(toRadians(gc.getGalacticLatitude())) * sin(toRadians(27.4d));
		double term2 = cos(toRadians(gc.getGalacticLatitude())) * cos(toRadians(27.4d))
				* cos(toRadians(gc.getGalacticLongitude() - 123d));
		double dec = toDegrees(asin(term1 + term2));
		return dec;
	}

}
