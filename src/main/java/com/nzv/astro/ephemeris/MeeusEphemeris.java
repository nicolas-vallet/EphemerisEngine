package com.nzv.astro.ephemeris;

import java.util.Date;

public interface MeeusEphemeris {

	public Date getEasterDateForYear(int year);

	public double getMeanSiderealTimeAsHoursFromJulianDay(
			double julianDayAsBigDecimal);

	public double getMeanSiderealTimeAsHoursFromJulianDay(
			double julianDayAsBigDecimal, Sexagesimal hourOfDay);

	public double getApparentSiderealTimeAsHoursFromJulianDay(
			double julianDayAsBigDecimal, Sexagesimal hourOfDay);

	public double computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(
			double julianDayAsBigDecimal);

	public Sexagesimal universalTimeToEphemerisTime(
			double dateAsBigDecimal, Sexagesimal universalTime);

	public Sexagesimal ephemerisTimeToUniversalTime(
			double dateAsBigDecimal, Sexagesimal ephemerisTime);
}
