package com.nzv.astro.ephemeris.coordinate;

import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.Constants;
import com.nzv.astro.ephemeris.Sexagesimal;

/**
 * See Chapter 5 of Jean Meeus book
 */
public class GeocentricCoordinates {

	/**
	 * Equivalent to the value of p sin theta'
	 */
	double abscissa = 0;

	/**
	 * Equivalent to the value of p cos theta'
	 */
	double ordinate = 0;

	public GeocentricCoordinates(int d, int m, double s, double height) {
		Sexagesimal phi = new Sexagesimal(d, m, s);
		double u = computeU(phi.getValueAsUnits());
		abscissa = computeAbscissa(u, phi.getValueAsUnits(), height);
		ordinate = computeOrdinate(u, phi.getValueAsUnits(), height);
	}

	public GeocentricCoordinates(double geographicLatitude, float height) {
		Sexagesimal phi = Sexagesimal.decimalToSexagesimal(geographicLatitude);
		double u = computeU(phi.getValueAsUnits());
		abscissa = computeAbscissa(u, phi.getValueAsUnits(), height);
		ordinate = computeOrdinate(u, phi.getValueAsUnits(), height);
	}

	private double computeU(double angleInDegrees) {
		return atan((1 - Constants.EARTH_FLATTENING) * tan(toRadians(angleInDegrees)));
	}

	private double computeAbscissa(double u, double phi, double height) {
		return (1 - Constants.EARTH_FLATTENING) * sin(u)
				+ (height / (Constants.EARTH_EQUATORIAL_RADIUS_IN_KM * 1000)) * sin(toRadians(phi));
	}

	private double computeOrdinate(double u, double phi, double height) {
		return cos(u) + (height / (Constants.EARTH_EQUATORIAL_RADIUS_IN_KM * 1000))
				* cos(toRadians(phi));
	}

	public double getAbscissa() {
		return abscissa;
	}

	public double getOrdinate() {
		return ordinate;
	}

	/**
	 * Returns the observer's quantity rho * sin(phi'), where phi' is the geocentric
	 * latitude and rho the geocentric radius in units of the Earth's equatorial
	 * radius. This is the value labelled the abscissa elsewhere in this class and
	 * is the quantity used by the parallax reduction of chapter 29.
	 *
	 * @return rho * sin(phi').
	 */
	public double getRhoSinPhiPrime() {
		return abscissa;
	}

	/**
	 * Returns the observer's quantity rho * cos(phi'), where phi' is the geocentric
	 * latitude and rho the geocentric radius in units of the Earth's equatorial
	 * radius. This is the value labelled the ordinate elsewhere in this class and
	 * is the quantity used by the parallax reduction of chapter 29.
	 *
	 * @return rho * cos(phi').
	 */
	public double getRhoCosPhiPrime() {
		return ordinate;
	}

}
