package com.nzv.astro.ephemeris.coordinate.adapter;

import com.nzv.astro.ephemeris.Constants;
import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.IEcliptiqueCoordinates;
import com.nzv.astro.ephemeris.coordinate.IGalacticCoordinates;
import com.nzv.astro.ephemeris.coordinate.IHorizontalCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;
import com.nzv.astro.ephemeris.impl.EphemerisEngineImpl;

import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class EquatorialCoordinatesAdapter implements IEcliptiqueCoordinates, IGalacticCoordinates,
        IHorizontalCoordinates {

    private EquatorialCoordinates ec;
    private EphemerisEngine ephemerisEngine = new EphemerisEngineImpl();

    public EquatorialCoordinatesAdapter(EquatorialCoordinates equatorialCoordinates) {
        this.ec = equatorialCoordinates;
    }

    public EquatorialCoordinates getEquatorialCoordinates() {
        return ec;
    }

    public void setEquatorialCoordinates(EquatorialCoordinates equatorialCoordinates) {
        this.ec = equatorialCoordinates;
    }

    @Override
    public double getEcliptiqueLongitude() {
        double term1 = (sin(toRadians(ec.getRightAscension()))
                * cos(toRadians(Constants.ECLIPTIC_OBLIQUITY_2000.getValueAsUnits())) + tan(toRadians(ec
                .getDeclinaison()))
                * sin(toRadians(Constants.ECLIPTIC_OBLIQUITY_2000.getValueAsUnits())));
        double term2 = cos(toRadians(ec.getRightAscension()));
        double lambda = toDegrees(atan2(term1, term2));
        return lambda % 360;
    }

    @Override
    public double getEcliptiqueLatitude() {
        double term1 = sin(toRadians(ec.getDeclinaison()))
                * cos(toRadians(Constants.ECLIPTIC_OBLIQUITY_2000.getValueAsUnits()));
        double term2 = cos(toRadians(ec.getDeclinaison()))
                * sin(toRadians(Constants.ECLIPTIC_OBLIQUITY_2000.getValueAsUnits()))
                * sin(toRadians(ec.getRightAscension()));
        double beta = toDegrees(asin(term1 - term2));
        return beta;
    }

    @Override
    public double getGalacticLongitude() {
        double term1 = sin(toRadians(192.25d - ec.getRightAscension()));
        double term2 = cos(toRadians(192.25d - ec.getRightAscension())) * sin(toRadians(27.4d));
        double term3 = tan(toRadians(ec.getDeclinaison())) * cos(toRadians(27.4d));
//		double x = toDegrees(atan2(term1, (term2 - term3)));
        double x = toDegrees(atan(term1 / (term2 - term3)));
        double l = 303 - x;
        return l % 360;
    }

    @Override
    public double getGalacticLatitude() {
        double term1 = sin(toRadians(ec.getDeclinaison())) * sin(toRadians(27.4d));
        double term2 = cos(toRadians(ec.getDeclinaison())) * cos(toRadians(27.4d))
                * cos(toRadians(192.25d - ec.getRightAscension()));
        double b = toDegrees(asin(term1 + term2));
        return b;
    }

    @Override
    public double getAzimuth() {
        throw new UnsupportedOperationException(
                "Azimuth depends of the observer position and the time. "
                        + "Use the getAzimuth(GeographicCoordinates, double) method instead!");
    }

    public double getAzimuth(GeographicCoordinates observerSite, double greenwichSiderealTime) {
        double H = ephemerisEngine.H(greenwichSiderealTime, observerSite.getLongitude(),
                ec.getRightAscension());
        double term1 = -sin(toRadians(H));
        double term2 = tan(toRadians(ec.getDeclinaison()))
                * cos(toRadians(observerSite.getLatitude())) - cos(toRadians(H))
                * sin(toRadians(observerSite.getLatitude()));
        double A = toDegrees(atan2(term1, term2));
        return A % 360;
    }

    @Override
    public double getElevation() {
        throw new UnsupportedOperationException(
                "Elevation depends of the observer position and the time. "
                        + "Use the getElevation(GeographicCoordinates, double) method instead!");
    }

    public double getElevation(GeographicCoordinates observerSite, double greenwichSiderealTime) {
        double H = ephemerisEngine.H(greenwichSiderealTime, observerSite.getLongitude(),
                ec.getRightAscension());
        double term1 = sin(toRadians(observerSite.getLatitude()))
                * sin(toRadians(ec.getDeclinaison()));
        double term2 = cos(toRadians(observerSite.getLatitude()))
                * cos(toRadians(ec.getDeclinaison())) * cos(toRadians(H));
        double h = toDegrees(asin(term1 + term2));
        return h;
    }

}
