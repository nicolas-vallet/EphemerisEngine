package com.nzv.astro.ephemeris.interpolation;

/**
 * A simple container class containing a pair of values X-Y
 * and corresponding to a sample input for an interpolation procedure.
 */
public class InterpolationData {

    private double x;
    private double y;

    public InterpolationData(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
