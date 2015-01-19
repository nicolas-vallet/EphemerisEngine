package com.nzv.astro.ephemeris;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class JulianDay {

	public static Date getDateFromJulianDay(double julianDay) {
		BigDecimal jd = BigDecimal.valueOf(julianDay);
		jd = jd.add(BigDecimal.valueOf(0.5d));
		int Z = jd.intValue();
		BigDecimal F = jd.subtract(BigDecimal.valueOf(Z));
		int A = 0;
		if (Z < 2299161) {
			A = Z;
		} else {
			int alpha = (int) ((Z - 1867216.25d) / 36524.25);
			A = Z + 1 + alpha - (int) (alpha / 4);
		}
		int B = A + 1524;
		int C = (int) ((B - 122.1) / 365.25);
		int D = (int) (365.25 * C);
		int E = (int) ((B - D) / 30.6001d);

		BigDecimal d = BigDecimal
				.valueOf(B)
				.subtract(BigDecimal.valueOf(D))
				.subtract(
						BigDecimal.valueOf(BigDecimal.valueOf(30.6001)
								.multiply(BigDecimal.valueOf(E)).intValue()))
				.add(F);
		int m = 0;
		if (E < 13.5) {
			m = E - 1;
		} else {
			m = E - 13;
		}
		int y = 0;
		if (m > 2.5) {
			y = C - 4716;
		} else {
			y = C - 4715;
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d.intValue());

		// We have to convert the decimal part of "d" to the right hour, minute,
		// second...
		BigDecimal dayRemainder = d.subtract(BigDecimal.valueOf(d.intValue()));
		BigDecimal hour = BigDecimal.valueOf(dayRemainder.multiply(
				BigDecimal.valueOf(24)).intValue());
		BigDecimal minute = BigDecimal.valueOf(dayRemainder
				.multiply(BigDecimal.valueOf(24)).subtract(hour)
				.multiply(BigDecimal.valueOf(60)).intValue());
		BigDecimal second = BigDecimal.valueOf(dayRemainder
				.multiply(BigDecimal.valueOf(24)).subtract(hour)
				.multiply(BigDecimal.valueOf(60)).subtract(minute)
				.multiply(BigDecimal.valueOf(60)).intValue());
		cal.set(Calendar.HOUR_OF_DAY, hour.intValue());
		cal.set(Calendar.MINUTE, minute.intValue());
		cal.set(Calendar.SECOND, second.intValue());

		return cal.getTime();
	}

	public static BigDecimal getJulianDateFromDate(Date d) {
		// TODO
		throw new UnsupportedOperationException("Missing implementation!");
	}
	
	public static double getJulianDayFromDateAsDouble(double dt, Sexagesimal time) {
		BigDecimal date = BigDecimal.valueOf(dt);
		try {
			date.multiply(BigDecimal.valueOf(10000)).remainder(date.movePointRight(4)).intValueExact();
		} catch(ArithmeticException ex) {
			throw new IllegalArgumentException("The provided date should correspond to midnight !");
		}
		BigDecimal result = BigDecimal.valueOf(getJulianDayFromDateAsDouble(date.doubleValue()));
		BigDecimal dayFraction = 
				BigDecimal.valueOf(time.getUnit()).divide(BigDecimal.valueOf(24), Constants.BIG_DECIMAL_PRECISION)
				.add(BigDecimal.valueOf(time.getMinute()).divide(BigDecimal.valueOf(1440), Constants.BIG_DECIMAL_PRECISION))
				.add(BigDecimal.valueOf(time.getSecond()).divide(BigDecimal.valueOf(86400), Constants.BIG_DECIMAL_PRECISION));
		return result.add(dayFraction).doubleValue();
	}

	public static double getJulianDayFromDateAsDouble(double dt) {
		BigDecimal date = BigDecimal.valueOf(dt);
		int y = 0;
		int m = 0;
		BigDecimal d = BigDecimal.ZERO;
		int a = 0;
		int b = 0;

		int yyyy = date.intValue();
		BigDecimal tmp = date.subtract(BigDecimal.valueOf(yyyy)).abs();
		int mm = tmp.multiply(BigDecimal.valueOf(100)).intValue();
		d = tmp.multiply(BigDecimal.valueOf(100))
				.subtract(BigDecimal.valueOf(mm))
				.multiply(BigDecimal.valueOf(100));

		if (mm > 2) {
			y = yyyy;
			m = mm;
		} else if (mm == 1 || mm == 2) {
			y = yyyy - 1;
			m = mm + 12;
		} else {
			throw new IllegalArgumentException(
					"La date fournie en paramètre n'est pas valide!");
		}

		if (isInGregorianCalendar(date.doubleValue())) {
			a = (int) (y / 100);
			b = 2 - a + (int) (a / 4);
		}

		return BigDecimal.valueOf((int) (365.25d * y)
				+ (int) (30.6001 * (m + 1)) + d.doubleValue() + 1720994.5d + b).doubleValue();
	}

	public static DayOfWeek getDayOfWeekFromDayAsDouble(double dt) {
		BigDecimal date = BigDecimal.valueOf(dt);
		BigDecimal jd = BigDecimal.valueOf(JulianDay.getJulianDayFromDateAsDouble(date.doubleValue()));
		if (!jd.subtract(BigDecimal.valueOf(jd.intValue())).equals(
				BigDecimal.valueOf(0.5d))) {
			throw new IllegalArgumentException(
					"The passed parameter should correspond to a date at midnight!");
		}
		switch (jd.add(BigDecimal.valueOf(1.5d))
				.remainder(BigDecimal.valueOf(7)).intValue()) {
		case 0:
			return DayOfWeek.SUNDAY;
		case 1:
			return DayOfWeek.MONDAY;
		case 2:
			return DayOfWeek.TUESDAY;
		case 3:
			return DayOfWeek.WEDNESDAY;
		case 4:
			return DayOfWeek.THURSDAY;
		case 5:
			return DayOfWeek.FRIDAY;
		case 6:
			return DayOfWeek.SATURDAY;
		}
		throw new RuntimeException();
	}

	public static int getDayOfYearFromDateAsDouble(double d) {
		BigDecimal date = BigDecimal.valueOf(d);
		Date dt = getDateFromJulianDay(getJulianDayFromDateAsDouble(date.doubleValue()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		if (!isLeapYear(cal.get(Calendar.YEAR))) {
			return ((int) (275 * (cal.get(Calendar.MONTH) + 1) / 9)) - 2
					* ((int) (((cal.get(Calendar.MONTH) + 1) + 9) / 12))
					+ cal.get(Calendar.DAY_OF_MONTH) - 30;
		} else {
			return ((int) (275 * (cal.get(Calendar.MONTH) + 1) / 9))
					- ((int) (((cal.get(Calendar.MONTH) + 1) + 9) / 12))
					+ cal.get(Calendar.DAY_OF_MONTH) - 30;
		}
	}

	private static boolean isInGregorianCalendar(double date) {
		return date >= Constants.GREGORIAN_CALENDAR_START_DATE;
	}

	public static boolean isLeapYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
	}

}
