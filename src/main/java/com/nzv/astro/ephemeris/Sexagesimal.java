package com.nzv.astro.ephemeris;

import static java.lang.Math.abs;

import java.math.BigDecimal;

// TODO : FIX l'instanciation avec une valeur negative mais inférieure  à 1, type -0° 35' 35"
public class Sexagesimal {

	public static enum SexagesimalType {
		DEGREES, HOURS;
	}

	private int unit;
	private int minute;
	private double second;

	public Sexagesimal(int unit, int minute, double second) {
		this.unit = unit;
		this.minute = minute;
		this.second = second;
	}

	public Sexagesimal(double decimalValue) {
		Sexagesimal s = Sexagesimal.decimalToSexagesimal(decimalValue);
		this.unit = s.getUnit();
		this.minute = abs(s.getMinute());
		this.second = abs(s.getSecond());
	}

	@Override
	public String toString() {
		return unit + "u " + abs(minute) + "' " + abs(second) + "\"";
	}

	public String toString(SexagesimalType type) {
		switch (type) {
		case DEGREES:
			return unit + "° " + abs(minute) + "' " + abs(second) + "\"";
		default:
			return unit + "H " + abs(minute) + "m " + abs(second) + "s";
		}
	}

	public double getValueAsUnits() {
		return sexagesimalToDecimal(this);
	}

	public static double sexagesimalToDecimal(Sexagesimal s) {
		return sexagesimalToBigDecimal(s).doubleValue();
	}
	
	public static BigDecimal sexagesimalToBigDecimal(Sexagesimal s) {
		BigDecimal result = BigDecimal.valueOf(s.getUnit());
		if (s.getUnit() >= 0) {
			result = result.add(BigDecimal.valueOf(s.getMinute()).divide(BigDecimal.valueOf(60),
					Constants.BIG_DECIMAL_PRECISION));
			result = result.add(BigDecimal.valueOf(s.getSecond()).divide(BigDecimal.valueOf(3600),
					Constants.BIG_DECIMAL_PRECISION));
		} else {
			result = result.subtract(BigDecimal.valueOf(s.getMinute()).divide(
					BigDecimal.valueOf(60), Constants.BIG_DECIMAL_PRECISION));
			result = result.subtract(BigDecimal.valueOf(s.getSecond()).divide(
					BigDecimal.valueOf(3600), Constants.BIG_DECIMAL_PRECISION));

		}
		return result;
	}

	public static Sexagesimal decimalToSexagesimal(double d) {
		BigDecimal input = BigDecimal.valueOf(d);
		Sexagesimal result = new Sexagesimal(input.intValue(), 0, 0);
		result.setMinute(input.multiply(BigDecimal.valueOf(60))
				.subtract(BigDecimal.valueOf(result.getUnit()).multiply(BigDecimal.valueOf(60)))
				.intValue());
		result.setSecond(input.multiply(BigDecimal.valueOf(3600))
				.subtract(BigDecimal.valueOf(result.getUnit()).multiply(BigDecimal.valueOf(3600)))
				.subtract(BigDecimal.valueOf(result.getMinute()).multiply(BigDecimal.valueOf(60)))
				.doubleValue());
		return result;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public double getSecond() {
		return second;
	}

	public void setSecond(double second) {
		this.second = second;
	}

}
