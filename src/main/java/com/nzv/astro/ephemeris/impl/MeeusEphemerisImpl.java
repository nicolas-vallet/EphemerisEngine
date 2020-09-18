package com.nzv.astro.ephemeris.impl;

import com.nzv.astro.ephemeris.Constants;
import com.nzv.astro.ephemeris.EphemerisEngine;
import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;

public class MeeusEphemerisImpl implements MeeusEphemeris {

    private EphemerisEngine engine = new EphemerisEngineImpl();

    private double getMeanSiderealTimeAsRevolutionFromJulianDay(double julianDayAsBigDecimal) {
        if (julianDayAsBigDecimal - (int) julianDayAsBigDecimal != 0.5) {
            throw new IllegalArgumentException(
                    "The julian day passed parameter should correspond to a date at midnight!");
        }

        double T = (julianDayAsBigDecimal - 2415020) / 36525;
        double revolutionsCount = 0.276919398 + 100.0021359 * T + 0.000001075 * pow(T, 2);
        return revolutionsCount;
    }

    @Override
    public double getMeanSiderealTimeAsHoursFromJulianDay(double julianDayAsBigDecimal) {
        if (julianDayAsBigDecimal - (int) julianDayAsBigDecimal != 0.5) {
            throw new IllegalArgumentException(
                    "The julian day passed parameter should correspond to a date at midnight!");
        }
        double revolutionCounts = getMeanSiderealTimeAsRevolutionFromJulianDay(julianDayAsBigDecimal);
        double hours = (revolutionCounts - ((int) revolutionCounts)) * 24;
        return hours;
    }

    @Override
    public double getMeanSiderealTimeAsHoursFromJulianDay(double julianDayAsBigDecimal,
                                                          Sexagesimal hourOfDay) {
        double siderealTimeForMignight = getMeanSiderealTimeAsHoursFromJulianDay(julianDayAsBigDecimal);
        double time = Sexagesimal.sexagesimalToDecimal(hourOfDay);
        time = time * 1.002737908;
        siderealTimeForMignight = siderealTimeForMignight + time;
        return siderealTimeForMignight;
    }

    @Override
    public double getApparentSiderealTimeAsHoursFromJulianDay(double julianDayAs,
                                                              Sexagesimal hourOfDay) {
        double meanSiderealTime = getMeanSiderealTimeAsHoursFromJulianDay(julianDayAs, hourOfDay);
        double correctionInSeconds = engine.getNutationInLongitude(julianDayAs
                * cos(toRadians(Constants.ECLIPTIC_OBLIQUITY_1950.getValueAsUnits()))) / 15;
        double result = meanSiderealTime + (correctionInSeconds / 3600);
        return result;
    }

    @Override
    public double computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(double julianDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(JulianDay.getDateFromJulianDay(julianDay));
        int year = cal.get(Calendar.YEAR);
        if (cal.get(Calendar.ERA) == GregorianCalendar.BC) {
            year = -(cal.get(Calendar.YEAR) - 1);
        }
        if (year >= -1999 && year <= +3000) {
            // For years between -1999 and +3000 we use the series of polynomial
            // expressions which are presented here :
            // http://eclipse.gsfc.nasa.gov/SEcat5/deltatpoly.html
            int month = cal.get(Calendar.MONTH) + 1;
            double y = year + (month - 0.5) / 12;
            double delta = 0;
            if (year < -500) {
                double u = (y - 1820) / 100;
                delta = -20 + 32 * pow(u, 2);
            } else if (year >= -500 && year < +500) {
                double u = y / 100;
                delta = 10583.6d - 1014.41d * u + 33.78311d * pow(u, 2) - 5.952053d * pow(u, 3)
                        - 0.1798452d * pow(u, 4) + 0.022174192d * pow(u, 5) + 0.0090316521d
                        * pow(u, 6);
            } else if (year >= +500 && year < +1600) {
                double u = (y - 1000) / 100;
                delta = 1574.2d - 556.01d * u + 71.23472d * pow(u, 2) + 0.319781d * pow(u, 3)
                        - 0.8503463d * pow(u, 4) - 0.005050998d * pow(u, 5) + 0.0083572073d
                        * pow(u, 6);
            } else if (year >= 1600 && year < +1700) {
                double t = y - 1600;
                delta = 120 - 0.9808d * t - 0.01532 * pow(t, 5) / 7129;
            } else if (year >= +1700 && year < +1800) {
                double t = y - 1700;
                delta = 8.83 + 0.1603d * t - 0.0059285d * pow(t, 2) + 0.00013336d * pow(t, 3)
                        - pow(t, 4) / 1174000;
            } else if (year >= +1800 && year < +1860) {
                double t = y - 1800;
                delta = 13.72 - 0.332447d * t + 0.0068612d * pow(t, 2) + 0.0041116d * pow(t, 3)
                        - 0.00037436d * pow(t, 4) + 0.0000121272d * pow(t, 5) - 0.0000001699d
                        * pow(t, 6) + 0.000000000875d * pow(t, 7);
            } else if (year >= +1860 && year < +1900) {
                double t = y - 1860;
                delta = 7.62 + 0.5737d * t - 0.251754d * pow(t, 2) + 0.01680668d * pow(t, 3)
                        - 0.0004473624d * pow(t, 4) + pow(t, 5) / 233174;
            } else if (year >= +1900 && year < +1920) {
                double t = y - 1900;
                delta = -2.79 + 1.494119d * t - 0.0598939d * pow(t, 2) + 0.0061966d * pow(t, 3)
                        - 0.000197d * pow(t, 4);
            } else if (year >= +1920 && year < +1941) {
                double t = y - 1920;
                delta = 21.20 + 0.84493d * t - 0.076100d * pow(t, 2) + 0.0020936d * pow(t, 3);
            } else if (year >= +1941 && year < +1961) {
                double t = y - 1950;
                delta = 29.07 + 0.407d * t - pow(t, 2) / 233 + pow(t, 3) / 2547;
            } else if (year >= +1961 && year < +1986) {
                double t = y - 1975;
                delta = 45.45 + 1.067d * t - pow(t, 2) / 260 - pow(t, 3) / 718;
            } else if (year >= +1986 && year < +2005) {
                double t = y - 2000;
                delta = 63.86 + 0.3345d * t - 0.060374d * pow(t, 2) + 0.0017275d * pow(t, 3)
                        + 0.000651814d * pow(t, 4) + 0.00002373599d * pow(t, 5);
            } else if (year >= +2005 && year < +2050) {
                double t = y - 2000;
                delta = 62.92 + 0.32217d * t + 0.005589d * pow(t, 2);
            } else if (year >= +2050 && year < +2150) {
                delta = -20 + 32 * ((y - 1820) / 100) * ((y - 1820) / 100) - 0.5628d * (2150 - y);
            } else if (year >= +2150) {
                double u = (y - 1820) / 100;
                delta = -20 + 32 * pow(u, 2);
            }
            return delta;
        } else {
            // For other years, we used the expression presented in chapter 6 of
            // Jean Meeus'book
            double T = (julianDay - 2415020.0) / 36525;
            double delta = 0.41 + 1.2053 * T + 0.4992 * pow(T, 2);
            return delta / 60;
        }
    }

    @Override
    public Sexagesimal universalTimeToEphemerisTime(double date, Sexagesimal universalTime) {
        double jd = JulianDay.getJulianDayFromDateAsDouble(date);
        if (jd - ((int) jd) != 0.5) {
            throw new IllegalArgumentException(
                    "The date passed parameter should correspond to a date at midnight!");
        }

        // ET = UT + delta
        double delta = computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(jd);
        double ET = universalTime.getValueAsUnits() + delta / 3600;
        return Sexagesimal.decimalToSexagesimal(ET);
    }

    @Override
    public Sexagesimal ephemerisTimeToUniversalTime(double date, Sexagesimal ephemerisTime) {
        double jd = JulianDay.getJulianDayFromDateAsDouble(date);
        if (jd - ((int) jd) != 0.5) {
            throw new IllegalArgumentException(
                    "The date passed parameter should correspond to a date at midnight!");
        }

        // UT = TE - delta
        double delta = computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(jd);
        double UT = ephemerisTime.getValueAsUnits() - delta / 3600;
        return Sexagesimal.decimalToSexagesimal(UT);
    }

    @Override
    public Date getEasterDateForYear(int year) {
        if (year <= 1582) {
            throw new InvalidParameterException(
                    "This method does not work for years before 1583 AD");
        }
        BigDecimal y = new BigDecimal(year);
        BigDecimal a = y.remainder(new BigDecimal(19));

        BigDecimal[] tmp = y.divideAndRemainder(new BigDecimal(100));
        BigDecimal b = tmp[0];
        BigDecimal c = tmp[1];

        tmp = b.divideAndRemainder(new BigDecimal(4));
        BigDecimal d = tmp[0];
        BigDecimal e = tmp[1];

        BigDecimal f = b.add(new BigDecimal(8)).divideToIntegralValue(new BigDecimal(25));

        BigDecimal g = b.subtract(f).add(BigDecimal.ONE).divideToIntegralValue(new BigDecimal(3));

        BigDecimal h = a.multiply(new BigDecimal(19)).add(b).subtract(d).subtract(g)
                .add(new BigDecimal(15)).remainder(new BigDecimal(30));

        tmp = c.divideAndRemainder(new BigDecimal(4));
        BigDecimal i = tmp[0];
        BigDecimal k = tmp[1];

        BigDecimal q = BigDecimal.valueOf(32).add(e.multiply(new BigDecimal(2)))
                .add(i.multiply(new BigDecimal(2))).subtract(h).subtract(k)
                .remainder(new BigDecimal(7));

        BigDecimal m = a.add(h.multiply(new BigDecimal(11))).add(q.multiply(new BigDecimal(22)))
                .divideToIntegralValue(new BigDecimal(451));

        tmp = h.add(q).subtract(m.multiply(new BigDecimal(7))).add(new BigDecimal(114))
                .divideAndRemainder(new BigDecimal(31));
        BigDecimal n = tmp[0];
        BigDecimal p = tmp[1];

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, n.intValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, p.intValue() + 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

}
