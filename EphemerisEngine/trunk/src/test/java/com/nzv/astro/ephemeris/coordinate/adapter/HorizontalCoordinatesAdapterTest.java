package com.nzv.astro.ephemeris.coordinate.adapter;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.HorizontalCoordinates;
import com.nzv.astro.ephemeris.impl.MeeusEphemerisImpl;

public class HorizontalCoordinatesAdapterTest {

	private MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();

	@Test
	public void testConvertHorizontalCoordinatesToEquatorialCoordinates() {
		// Coordonnées équatoriales du point situé à Azimut=128° 18' 3.013\" /
		// Altitude=36° 32' 25.7272\",
		// le 13/11/1978 à 4h 34m 00s TU à l'Observatoire d'Uccle (longitude -0h
		// 17m 25.94s / latitude +50° 47' 55\")
		HorizontalCoordinates hc = new HorizontalCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(128, 18, 3.0128d)),
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(36, 32, 25.7272d)));
		HorizontalCoordinatesAdapter horizontalCoordinatesAdapter = new HorizontalCoordinatesAdapter(
				hc);
		GeographicCoordinates observerSite = new GeographicCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
				-(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));
		double siderealTime = meeusEngine.getApparentSiderealTimeAsHoursFromJulianDay(
				JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));
		Assert.assertTrue("10H 57m 34.8843210620004s".equals(Sexagesimal.decimalToSexagesimal(
				horizontalCoordinatesAdapter.getRightAscension(observerSite, siderealTime) / 15)
				.toString(SexagesimalType.HOURS)));
		Assert.assertTrue("8° 25' 58.10001678732\"".equals(Sexagesimal.decimalToSexagesimal(
				horizontalCoordinatesAdapter.getDeclinaison(observerSite, siderealTime)).toString(
				SexagesimalType.DEGREES)));
	}
}
