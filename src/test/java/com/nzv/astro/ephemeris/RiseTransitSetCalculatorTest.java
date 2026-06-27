package com.nzv.astro.ephemeris;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;
import com.nzv.astro.ephemeris.impl.EphemerisEngineImpl;
import com.nzv.astro.ephemeris.impl.MeeusEphemerisImpl;

public class RiseTransitSetCalculatorTest {

	private static final double DELTA = 1e-6;

	private final RiseTransitSetCalculator underTest = new RiseTransitSetCalculator();

	@Test
	public void testVenusFromBoston() {
		// Chapter 42 worked example geometry: Venus seen from Boston
		// (latitude +42.3333, longitude +71.0833 west) with apparent sidereal
		// time at Greenwich for 0h UT equal to 11.50721 h, for the central-day
		// apparent place of Venus (RA 41.73129, Dec +18.44092). The values below
		// are the exact single-shot result of the first-approximation method.
		EquatorialCoordinates venus = new EquatorialCoordinates(41.73129, 18.44092);
		GeographicCoordinates boston = new GeographicCoordinates(42.3333, 71.0833);
		RiseTransitSet rts = underTest.compute(venus, boston, 11.50721);

		Assert.assertFalse(rts.isCircumpolar());
		Assert.assertFalse(rts.isAlwaysBelowHorizon());
		Assert.assertEquals(20.02464682329871, rts.getTransitTimeUT(), DELTA);
		Assert.assertEquals(12.808778518422807, rts.getRiseTimeUT(), DELTA);
		Assert.assertEquals(3.2405151281746094, rts.getSetTimeUT(), DELTA);
		Assert.assertEquals(66.10762, rts.getTransitAltitude(), DELTA);
		Assert.assertEquals(64.0912183205762, rts.getRiseAzimuth(), DELTA);
		Assert.assertEquals(295.9087816794238, rts.getSetAzimuth(), DELTA);
	}

	@Test
	public void testCircumpolarStar() {
		// A star very close to the north celestial pole never sets when observed
		// from a northern latitude.
		EquatorialCoordinates nearPole = new EquatorialCoordinates(37.95, 89.26);
		GeographicCoordinates site = new GeographicCoordinates(59.9, -10.7);
		RiseTransitSet rts = underTest.compute(nearPole, site, 0.0);
		Assert.assertTrue(rts.isCircumpolar());
		Assert.assertFalse(rts.isAlwaysBelowHorizon());
		Assert.assertTrue(Double.isNaN(rts.getRiseTimeUT()));
		Assert.assertTrue(Double.isNaN(rts.getSetTimeUT()));
		Assert.assertFalse(Double.isNaN(rts.getTransitTimeUT()));
	}

	@Test
	public void testNeverRisesStar() {
		// The same near-pole star is permanently below the horizon for a southern
		// observer.
		EquatorialCoordinates nearPole = new EquatorialCoordinates(37.95, 89.26);
		GeographicCoordinates site = new GeographicCoordinates(-33.9, -18.4);
		RiseTransitSet rts = underTest.compute(nearPole, site, 0.0);
		Assert.assertTrue(rts.isAlwaysBelowHorizon());
		Assert.assertFalse(rts.isCircumpolar());
	}

	@Test
	public void testSunFromUccleIsPhysicallyReasonable() {
		// Integration test exercising chapter 18 (solar coordinates), sidereal
		// time and chapter 42 together: the Sun's rise, transit and set from
		// Uccle on 1978 November 13. Transit must fall near local apparent noon
		// (about 11:25 UT for this longitude and equation of time) and the day
		// must be the short ~9 hours expected in mid-November at this latitude.
		EphemerisEngineImpl engine = new EphemerisEngineImpl();
		MeeusEphemerisImpl meeus = new MeeusEphemerisImpl();
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		double ast0h = meeus.getApparentSiderealTimeAsHoursFromJulianDay(jd, new Sexagesimal(0, 0, 0));
		EquatorialCoordinates sun = engine.sunApparentEquatorialCoordinates(jd);
		GeographicCoordinates uccle = new GeographicCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
				-(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));
		RiseTransitSet rts = underTest.compute(sun, uccle, ast0h,
				RiseTransitSetCalculator.STANDARD_ALTITUDE_SUN);

		Assert.assertEquals(11.42, rts.getTransitTimeUT(), 0.1);
		double dayLength = rts.getSetTimeUT() - rts.getRiseTimeUT();
		Assert.assertEquals(9.1, dayLength, 0.3);
	}
}
