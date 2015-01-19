package com.nzv.astro.ephemeris.coordinate.impl;

import com.nzv.astro.ephemeris.coordinate.IHorizontalCoordinates;

public class HorizontalCoordinates implements IHorizontalCoordinates {

	private double azimuth;
	private double elevation;

	public HorizontalCoordinates(double azimuth, double elevation) {
		super();
		this.azimuth = azimuth;
		this.elevation = elevation;
	}

	@Override
	public double getAzimuth() {
		return azimuth;
	}

	@Override
	public double getElevation() {
		return elevation;
	}

}
