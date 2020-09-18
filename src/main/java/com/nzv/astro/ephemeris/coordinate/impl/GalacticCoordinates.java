package com.nzv.astro.ephemeris.coordinate.impl;

import com.nzv.astro.ephemeris.coordinate.IGalacticCoordinates;

public class GalacticCoordinates implements IGalacticCoordinates {

    private double longitude;
    private double latitude;

    public GalacticCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getGalacticLongitude() {
        return longitude;
    }

    public double getGalacticLatitude() {
        return latitude;
    }

}
