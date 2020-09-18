package com.nzv.astro.ephemeris.coordinate.impl;

import com.nzv.astro.ephemeris.coordinate.IEcliptiqueCoordinates;

public class EclipticCoordinates implements IEcliptiqueCoordinates {

    private double longitude;
    private double latitude;

    public EclipticCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getEcliptiqueLongitude() {
        return longitude;
    }

    public double getEcliptiqueLatitude() {
        return latitude;
    }

}
