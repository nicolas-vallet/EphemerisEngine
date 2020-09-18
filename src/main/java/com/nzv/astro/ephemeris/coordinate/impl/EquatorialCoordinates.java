package com.nzv.astro.ephemeris.coordinate.impl;

import com.nzv.astro.ephemeris.coordinate.IEquatorialCoordinates;

public class EquatorialCoordinates implements IEquatorialCoordinates {

    private double rightAscension;
    private double declinaison;

    public EquatorialCoordinates(double rightAscension, double declinaison) {
        this.rightAscension = rightAscension;
        this.declinaison = declinaison;
    }

    public double getRightAscension() {
        return rightAscension;
    }

    public double getDeclinaison() {
        return declinaison;
    }

}
