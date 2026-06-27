package com.nzv.astro.ephemeris;

import static java.lang.Math.abs;

import java.math.BigDecimal;

/**
 * A value expressed in a sexagesimal system (degrees/hours, minutes, seconds).
 * <p>
 * The sign of the value is normally carried by the {@code unit} field. For values
 * whose magnitude is strictly less than one unit (for example {@code -0° 35' 35"}),
 * the integer {@code unit} is zero and cannot carry the sign; in that case the sign
 * is tracked separately so that such values round-trip correctly.
 */
public class Sexagesimal {

	public static enum SexagesimalType {
		DEGREES, HOURS;
	}

	private int unit;
	private int minute;
	private double second;

	/**
	 * Carries the sign only when {@code unit == 0}; otherwise the sign is given by
	 * {@code unit}.
	 */
	private boolean negativeWhenUnitZero;

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
		this.negativeWhenUnitZero = s.negativeWhenUnitZero;
	}

	/**
	 * @return {@code true} when this value is negative, taking into account the special
	 *         case where the magnitude is below one unit.
	 */
	public boolean isNegative() {
		return unit < 0 || (unit == 0 && negativeWhenUnitZero);
	}

	@Override
	public String toString() {
		return signPrefix() + abs(unit) + "u " + abs(minute) + "' " + abs(second) + "\"";
	}

	public String toString(SexagesimalType type) {
		switch (type) {
		case DEGREES:
			return signPrefix() + abs(unit) + "\u00b0 " + abs(minute) + "' " + abs(second) + "\"";
		default:
			return signPrefix() + abs(unit) + "H " + abs(minute) + "m " + abs(second) + "s";
		}
	}

	private String signPrefix() {
		return isNegative() ? "-" : "";
	}

	public double getValueAsUnits() {
		return sexagesimalToDecimal(this);
	}

	public static double sexagesimalToDecimal(Sexagesimal s) {
		return sexagesimalToBigDecimal(s).doubleValue();
	}

	public static BigDecimal sexagesimalToBigDecimal(Sexagesimal s) {
		BigDecimal result = BigDecimal.valueOf(s.getUnit());
		BigDecimal minutes = BigDecimal.valueOf(abs(s.getMinute())).divide(BigDecimal.valueOf(60),
				Constants.BIG_DECIMAL_PRECISION);
		BigDecimal seconds = BigDecimal.valueOf(abs(s.getSecond())).divide(
				BigDecimal.valueOf(3600), Constants.BIG_DECIMAL_PRECISION);
		if (!s.isNegative()) {
			result = result.add(minutes).add(seconds);
		} else {
			result = result.subtract(minutes).subtract(seconds);
		}
		return result;
	}

	public static Sexagesimal decimalToSexagesimal(double d) {
		boolean negative = d < 0;
		BigDecimal input = BigDecimal.valueOf(abs(d));
		int unit = input.intValue();
		int minute = input.multiply(BigDecimal.valueOf(60))
				.subtract(BigDecimal.valueOf(unit).multiply(BigDecimal.valueOf(60)))
				.intValue();
		double second = input.multiply(BigDecimal.valueOf(3600))
				.subtract(BigDecimal.valueOf(unit).multiply(BigDecimal.valueOf(3600)))
				.subtract(BigDecimal.valueOf(minute).multiply(BigDecimal.valueOf(60)))
				.doubleValue();
		Sexagesimal result = new Sexagesimal(negative ? -unit : unit, minute, second);
		// When the magnitude is below one unit the sign cannot live in unit (== 0),
		// so record it explicitly.
		result.negativeWhenUnitZero = negative && unit == 0;
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
