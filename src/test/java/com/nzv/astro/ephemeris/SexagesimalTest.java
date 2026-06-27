package com.nzv.astro.ephemeris;

import org.junit.Assert;
import org.junit.Test;

import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;

public class SexagesimalTest {

	private static final double DELTA = 1e-9;

	@Test
	public void testSexagesimalToDecimalPositive() {
		// 50 47' 55" = 50.798611... degrees.
		double value = Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55));
		Assert.assertEquals(50 + 47 / 60.0 + 55 / 3600.0, value, DELTA);
	}

	@Test
	public void testSexagesimalToDecimalNegative() {
		// -14 43' 08.2" must be a single negative angle.
		double value = Sexagesimal.sexagesimalToDecimal(new Sexagesimal(-14, 43, 8.2));
		Assert.assertEquals(-(14 + 43 / 60.0 + 8.2 / 3600.0), value, DELTA);
	}

	@Test
	public void testDecimalToSexagesimalRoundTripPositive() {
		double original = 28.148642;
		Sexagesimal s = Sexagesimal.decimalToSexagesimal(original);
		Assert.assertEquals(original, s.getValueAsUnits(), 1e-6);
		Assert.assertFalse(s.isNegative());
	}

	@Test
	public void testDecimalToSexagesimalRoundTripNegativeAboveOneUnit() {
		double original = -14.718900831521825;
		Sexagesimal s = Sexagesimal.decimalToSexagesimal(original);
		Assert.assertEquals(original, s.getValueAsUnits(), 1e-6);
		Assert.assertTrue(s.isNegative());
	}

	@Test
	public void testDecimalToSexagesimalNegativeBelowOneUnit() {
		// Regression test for the historical sign-loss bug: a value such as -0 35' 35"
		// (magnitude below one degree) used to come back positive because the integer
		// "unit" part is zero and could not carry the sign.
		double original = -(35 / 60.0 + 35 / 3600.0); // approx -0.593055...
		Sexagesimal s = Sexagesimal.decimalToSexagesimal(original);
		Assert.assertTrue("value below one unit must stay negative", s.isNegative());
		Assert.assertEquals(original, s.getValueAsUnits(), 1e-6);
		Assert.assertTrue(s.toString(SexagesimalType.DEGREES).startsWith("-0"));
	}

	@Test
	public void testToStringHoursAndDegrees() {
		Assert.assertEquals("7H 42m 15.525s",
				new Sexagesimal(7, 42, 15.525).toString(SexagesimalType.HOURS));
		Assert.assertEquals("-14\u00b0 43' 8.2\"",
				new Sexagesimal(-14, 43, 8.2).toString(SexagesimalType.DEGREES));
	}
}
