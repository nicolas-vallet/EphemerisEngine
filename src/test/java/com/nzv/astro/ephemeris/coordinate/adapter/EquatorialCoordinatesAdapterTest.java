package com.nzv.astro.ephemeris.coordinate.adapter;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;
import com.nzv.astro.ephemeris.impl.MeeusEphemerisImpl;

public class EquatorialCoordinatesAdapterTest {

	private static final double DELTA = 1e-6;

	@Test
	public void testConvertEquatorialCoordinatesToEclipticCoordinates() {
		// Ecliptic coordinates of Pollux (RA=7h 42m 15.525s / DEC=+28 08' 55.11").
		EquatorialCoordinatesAdapter eca = new EquatorialCoordinatesAdapter(
				new EquatorialCoordinates(115.564688d, 28.148642d));
		Assert.assertEquals(112.52566509627357, eca.getEcliptiqueLongitude(), DELTA);
		Assert.assertEquals(6.686583627239734, eca.getEcliptiqueLatitude(), DELTA);
	}

	@Test
	public void testConvertEquatorialCoordinatesToGalacticCoordinates() {
		// Galactic coordinates of Nova Serpentis 1978 (RA=17h 48m 59.74s / DEC=-14 43' 08.2").
		// Reference (Meeus): l approx 12.9593, b approx 6.0463.
		Sexagesimal ra = new Sexagesimal(17, 48, 59.74);
		Sexagesimal dec = new Sexagesimal(-14, 43, 8.2);
		EquatorialCoordinatesAdapter eca = new EquatorialCoordinatesAdapter(
				new EquatorialCoordinates(ra.getValueAsUnits() * 15, dec.getValueAsUnits()));
		Assert.assertEquals(12.959250041566406, eca.getGalacticLongitude(), DELTA);
		Assert.assertEquals(6.046298477995704, eca.getGalacticLatitude(), DELTA);
	}

	@Test
	public void testConvertEquatorialCoordinatesToHorizontalCoordinates() {
		MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();
		// Alt/Az of Saturn on 13 November 1978 at 4h 34m 00s UT
		// (RA=10h 57m 35.681s / DEC=+8 25' 58.10") at the Uccle observatory
		// (longitude -0h 17m 25.94s / latitude +50 47' 55").
		// With the corrected apparent sidereal time these match Meeus's published
		// result: A approx 128 18' 03", h approx 36 32' 25.7".
		EquatorialCoordinatesAdapter eca = new EquatorialCoordinatesAdapter(
				new EquatorialCoordinates(
						Sexagesimal.sexagesimalToDecimal(new Sexagesimal(10, 57, 35.681)),
						Sexagesimal.sexagesimalToDecimal(new Sexagesimal(8, 25, 58.10))));
		GeographicCoordinates observerSite = new GeographicCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
				-(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));
		double siderealTime = meeusEngine.getApparentSiderealTimeAsHoursFromJulianDay(
				JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));
		Assert.assertEquals(128.3008369017421, eca.getAzimuth(observerSite, siderealTime), DELTA);
		Assert.assertEquals(36.54047977837304, eca.getElevation(observerSite, siderealTime), DELTA);
	}
}
