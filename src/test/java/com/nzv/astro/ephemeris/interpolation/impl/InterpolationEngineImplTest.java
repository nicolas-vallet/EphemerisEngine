package com.nzv.astro.ephemeris.interpolation.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.interpolation.InterpolationData;
import com.nzv.astro.ephemeris.interpolation.InterpolationEngine;
import com.nzv.astro.ephemeris.interpolation.InterpolationException;

public class InterpolationEngineImplTest {

	private static final double DELTA = 1e-9;

	private final InterpolationEngine engine = new InterpolationEngineImpl();

	@Test
	public void testThreePointInterpolation() throws InterpolationException {
		InterpolationData i1 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1969.0805), 0.664531);
		InterpolationData i2 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1969.0806), 0.669651);
		InterpolationData i3 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1969.0807), 0.674800);
		double search = JulianDay.getJulianDayFromDateAsDouble(1969.0807, new Sexagesimal(4, 21, 0));
		Assert.assertEquals(0.6757363607221748, engine.interpolate(i1, i2, i3, search), DELTA);
	}

	@Test
	public void testFivePointInterpolation() throws InterpolationException {
		InterpolationData m1 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1979.1209),
				Sexagesimal.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 45.5099)).doubleValue());
		InterpolationData m2 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1979.12095),
				Sexagesimal.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 34.4060)).doubleValue());
		InterpolationData m3 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1979.1210),
				Sexagesimal.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 25.6303)).doubleValue());
		InterpolationData m4 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1979.12105),
				Sexagesimal.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 19.3253)).doubleValue());
		InterpolationData m5 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1979.1211),
				Sexagesimal.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 15.5940)).doubleValue());
		double search = JulianDay.getJulianDayFromDateAsDouble(1979.1210, new Sexagesimal(3, 20, 0));
		Assert.assertEquals(0.9065627642680891,
				engine.interpolate(m1, m2, m3, m4, m5, search), DELTA);
	}

	@Test
	public void testFindExtremum() throws InterpolationException {
		InterpolationData p1 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1966.0111), 1.381701);
		InterpolationData p2 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1966.0115), 1.381502);
		InterpolationData p3 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1966.0119), 1.381535);
		InterpolationData extremum = engine.findExtremum(p1, p2, p3);
		Assert.assertEquals(2439141.9310344825, extremum.getX(), 1e-6);
		Assert.assertEquals(1.3814871530172415, extremum.getY(), DELTA);
	}

	@Test
	public void testFindZero() throws InterpolationException {
		InterpolationData z1 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1973.0226),
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 28, 13.4)) * -3600);
		InterpolationData z2 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1973.0227),
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 6, 46.3)) * 3600);
		InterpolationData z3 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1973.0228),
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 38, 23.2)) * 3600);
		Assert.assertEquals(2441740.298732705, engine.findZero(z1, z2, z3), 1e-6);
	}

	@Test
	public void testListBasedInterpolationMatchesThreePoint() throws InterpolationException {
		InterpolationData i1 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1969.0805), 0.664531);
		InterpolationData i2 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1969.0806), 0.669651);
		InterpolationData i3 = new InterpolationData(
				JulianDay.getJulianDayFromDateAsDouble(1969.0807), 0.674800);
		double search = JulianDay.getJulianDayFromDateAsDouble(1969.0807, new Sexagesimal(4, 21, 0));
		List<InterpolationData> samples = Arrays.asList(i1, i2, i3);
		Assert.assertEquals(engine.interpolate(i1, i2, i3, search),
				engine.interpolate(samples, search), 1e-6);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testFindExtremumOnListIsGuarded() throws InterpolationException {
		engine.findExtremum(Arrays.asList(
				new InterpolationData(1, 1), new InterpolationData(2, 2)));
	}
}
