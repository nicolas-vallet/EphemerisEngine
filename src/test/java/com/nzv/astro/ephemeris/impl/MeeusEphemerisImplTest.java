package com.nzv.astro.ephemeris.impl;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import org.junit.Assert;
import org.junit.Test;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;

public class MeeusEphemerisImplTest {

    private MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();

    @Test(expected = InvalidParameterException.class)
    public void testGetEasterDayGivenYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Assert.assertTrue("26/03/1978".equals(sdf.format(meeusEngine.getEasterDateForYear(1978))));
        Assert.assertTrue("15/04/1979".equals(sdf.format(meeusEngine.getEasterDateForYear(1979))));
        Assert.assertTrue("06/04/1980".equals(sdf.format(meeusEngine.getEasterDateForYear(1980))));
        Assert.assertTrue("18/04/1954".equals(sdf.format(meeusEngine.getEasterDateForYear(1954))));
        Assert.assertTrue("23/04/2000".equals(sdf.format(meeusEngine.getEasterDateForYear(2000))));

        // We can't compute the date of easter at this year so we expect an
        // exception to be raise
        meeusEngine.getEasterDateForYear(1000);
    }

    @Test
    public void testGetMeanSideralTime() {
        // Temps sideral moyen a Greenwich pour le 13 novembre 1978 à 0h
        double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
        Sexagesimal s = Sexagesimal.decimalToSexagesimal(meeusEngine
                .getMeanSiderealTimeAsHoursFromJulianDay(jd));
        Assert.assertEquals(3, s.getUnit(), 0);
        Assert.assertEquals(27, s.getMinute(), 0);
        Assert.assertEquals(1.3302423518148, s.getSecond(), 0);

        // Temps sideral moyen a Greenwich pour le 13 novembre 1978 à 4h 34m 0s
        jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
        s = Sexagesimal.decimalToSexagesimal(meeusEngine.getMeanSiderealTimeAsHoursFromJulianDay(
                jd, new Sexagesimal(4, 34, 0)));
        Assert.assertEquals(8, s.getUnit(), 0);
        Assert.assertEquals(1, s.getMinute(), 0);
        Assert.assertEquals(46.341449871816, s.getSecond(), 0);
    }

    @Test
    public void testGetApparentSiderealTimeAsHoursFromJulianDay() {
        // Temps sidéral apparent à Greenwich le 13 novembre 1978 à 4h 34m TU
        Sexagesimal apparentSiderealTime = Sexagesimal.decimalToSexagesimal(meeusEngine
                .getApparentSiderealTimeAsHoursFromJulianDay(
                        JulianDay.getJulianDayFromDateAsDouble(1978.1113),
                        new Sexagesimal(4, 34, 0)));
        Assert.assertTrue("8H 1m 45.3386817801444s".equals(apparentSiderealTime
                .toString(SexagesimalType.HOURS)));
    }

    @Test
    public void testUniversalTimeToEphemerisTime() {
        // Temps des ephemerides pour le 6 fevrier -555 à 6h TU = "
        Assert.assertTrue("11H 0m 28.100050000002s".equals(meeusEngine
                .universalTimeToEphemerisTime(-555.0206, new Sexagesimal(6, 0, 0)).toString(
                        SexagesimalType.HOURS)));
    }

    @Test
    public void testEphemerisTimeToUniversalTime() {
        // Temps universel pour le 4 avril 1977 à 4h 19m = "
        Assert.assertTrue("4H 18m 12.1417528085172s".equals(meeusEngine
                .ephemerisTimeToUniversalTime(1977.0404, new Sexagesimal(4, 19, 0)).toString(
                        SexagesimalType.HOURS)));
    }

    @Test
    public void testComputeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond() {
        // dT en seconde pour -2000
        Assert.assertEquals(11.877875897303616, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(-2000.0101)), 0);
        // dT en seconde pour 200
        Assert.assertEquals(8640.309030098942, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(200.0101)), 0);
        // dT en seconde pour 600
        Assert.assertEquals(4738.848120923205, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(600.0101)), 0);
        // dT en seconde pour 1673
        Assert.assertEquals(-4419.33432217818, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1673.0101)), 0);
        // dT en seconde pour 1750
        Assert.assertEquals(13.375979008768493, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1750.0101)), 0);
        // dT en seconde pour 1850
        Assert.assertEquals(7.112129821862254, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1850.0101)), 0);
        // dT en seconde pour 1870
        Assert.assertEquals(0.902134179753771, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1870.0101)), 0);
        // dT en seconde pour 1910
        Assert.assertEquals(10.445380968083992, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1910.0101)), 0);
        // dT en seconde pour 1930
        Assert.assertEquals(24.130835825057865, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1930.0101)), 0);
        // dT en seconde pour 1950
        Assert.assertEquals(29.086950910613957, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1950.0101)), 0);
        // dT en seconde pour 1990
        Assert.assertEquals(56.92137028332472, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(1990.0101)), 0);
        // dT en seconde pour 2014
        Assert.assertEquals(68.54577795312504, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(2014.0101)), 0);
        // dT en seconde pour 2100
        Assert.assertEquals(202.8381222222219, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(2100.0101)), 0);
        // dT en seconde pour 2200
        Assert.assertEquals(442.18133888888855, meeusEngine
                .computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
                        .getJulianDayFromDateAsDouble(2200.0101)), 0);

    }
}
