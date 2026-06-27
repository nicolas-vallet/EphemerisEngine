package com.nzv.astro.ephemeris.coordinate.adapter;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.HorizontalCoordinates;
import com.nzv.astro.ephemeris.impl.MeeusEphemerisImpl;

public class HorizontalCoordinatesAdapterTest {

	private static final double DELTA = 1e-6;

	private final MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();

	@Test
	public void testConvertHorizontalCoordinatesToEquatorialCoordinates() {
		// Equatorial coordinates of the point at Azimuth=128 18' 3.0128" /
		// Altitude=36 32' 25.7272", on 13/11/1978 at 4h 34m 00s UT at Uccle
		// (longitude -0h 17m 25.94s / latitude +50 47' 55").
		HorizontalCoordinates hc = new HorizontalCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(128, 18, 3.0128d)),
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(36, 32, 25.7272d)));
		HorizontalCoordinatesAdapter adapter = new HorizontalCoordinatesAdapter(hc);
		GeographicCoordinates observerSite = new GeographicCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
				-(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));
		double siderealTime = meeusEngine.getApparentSiderealTimeAsHoursFromJulianDay(
				JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));
		Assert.assertEquals(10.95991138951116,
				adapter.getRightAscension(observerSite, siderealTime) / 15, DELTA);
		Assert.assertEquals(8.432805560218702,
				adapter.getDeclinaison(observerSite, siderealTime), DELTA);
	}
}
