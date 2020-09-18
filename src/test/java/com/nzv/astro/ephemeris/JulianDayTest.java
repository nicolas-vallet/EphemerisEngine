package com.nzv.astro.ephemeris;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.System.getProperties;
import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JulianDayTest {

    private static final String CALENDAR_DATE_AND_TIME_PATTERN = "dd/MM/yyy HH:mm:ss";
    private static final String CALENDAR_DATE_AND_TIME_AND_ERA_PATTERN = "dd/MM/yyy GGG HH:mm:ss";

    static {
        getProperties().list(out);
        getProperties().setProperty("user.language", "en");
        getProperties().setProperty("user.language.format", "en");
    }

    @Test
    public void testGetJulianDayFromDateAsDoubleAfterGregorianEra() {
        // Jour julien pour le 4.81 octobre 1957
        double result = JulianDay.getJulianDayFromDateAsDouble(1957.100481d);
        assertEquals(2436116.31, result, 0);
    }

    @Test
    public void testGetDateFromJulianDayAfterGregorianEra() {
        // Date pour le jour julian 2436116.31
        SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_PATTERN);
        Date date = JulianDay.getDateFromJulianDay(2436116.31);
        assertTrue("04/10/1957 19:26:24".equals(sdf.format(date)));
    }

    @Test
    public void testGetJulianDayFromDateAsDoubleBeforeGregorianEra() {
        // Jour julien pour le 27.5 janvier 333
        double result = JulianDay.getJulianDayFromDateAsDouble(333.01275d);
        assertEquals(1842713.0, result, 0);

    }

    @Test
    public void testGetDateFromJulianDayBeforeGregorianEra() {
        // Date pour le jour julian 1842713.0
        SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_PATTERN);
        Date date = JulianDay.getDateFromJulianDay(1842713.0);
        assertTrue("27/01/333 12:00:00".equals(sdf.format(date)));
    }

    @Test
    public void testGetJulianDayFromDateAsDoubleBeforeJC() {
        // Jour julien pour le 28.63 mai -584
        double result = JulianDay.getJulianDayFromDateAsDouble(-584.052863d);
        assertEquals(1507900.13, result, 0);
    }

    @Test
    public void testGetDateFromJulianDayBeforeJC() {
        // Date pour le jour julian 1507900.13
        SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_AND_ERA_PATTERN, Locale.ENGLISH);
        Date date = JulianDay.getDateFromJulianDay(1507900.13);
        assertTrue("28/05/585 BC 15:07:12".equals(sdf.format(date)));
    }

    @Test
    public void testIntervalInDaysBetweenTwoDates() {
        // Intervalle de temps entre le 16/11/1835 et le 20/04/1910
        double result = JulianDay.getJulianDayFromDateAsDouble(1910.0420d)
                - JulianDay.getJulianDayFromDateAsDouble(1835.1116d);
        assertEquals(27183.0, result, 0);
    }

    @Test
    public void testGetDateNDaysAfterGivenDate() {
        // Date se trouvant 10 000 jours après le 30 juin 1954
        Date date = JulianDay.getDateFromJulianDay(JulianDay
                .getJulianDayFromDateAsDouble(1954.0630d) + 10000);
        SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_DATE_AND_TIME_PATTERN);
        assertTrue("15/11/1981 00:00:00".equals(sdf.format(date)));
    }

    @Test
    public void testGetDayOfWeekGivenDate() {
        // Jour de la semaine correspondant au 30 juin 1954
        assertTrue(DayOfWeek.WEDNESDAY == JulianDay.getDayOfWeekFromDayAsDouble(1954.0630d));
    }

    @Test
    public void testGetDayOfYearGivenDate() {
        // Jour de l'année correspondant au 14/11/1978
        assertTrue(318 == JulianDay.getDayOfYearFromDateAsDouble(1978.1114d));

        // Jour de l'année correspondant au 22/04/1980
        assertTrue(113 == JulianDay.getDayOfYearFromDateAsDouble(1980.0422d));
    }

}
