package com.nzv.astro.ephemeris.coordinate;

public class GeographicCoordinates {

    private double latitude;
    private double longitude;

    public GeographicCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
