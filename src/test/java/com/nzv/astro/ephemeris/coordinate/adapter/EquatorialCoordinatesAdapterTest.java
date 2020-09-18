package com.nzv.astro.ephemeris.coordinate.adapter;

import com.nzv.astro.ephemeris.JulianDay;
import com.nzv.astro.ephemeris.MeeusEphemeris;
import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;
import com.nzv.astro.ephemeris.impl.MeeusEphemerisImpl;
import org.junit.Assert;
import org.junit.Test;

public class EquatorialCoordinatesAdapterTest {

    @Test
    public void testConvertEquatorialCoordinatesToEclipticCoordinates() {
        // Coordonnées ecliptiques de Pollux dont les coordonnées équatoriaux
        // sont RA=7h 42m 15.525s / DEC=+28° 08' 55.11
        EquatorialCoordinatesAdapter eca = new EquatorialCoordinatesAdapter(
                new EquatorialCoordinates(115.564688d, 28.148642d));
        Assert.assertEquals(112.52566509627357, eca.getEcliptiqueLongitude(), 0);
        Assert.assertEquals(6.686583627239734, eca.getEcliptiqueLatitude(), 0);
    }

    @Test
    public void testConvertEquatorialCoordinatesToGalacticCoordinates() {
        // Coordonnées galactiques de Nova Serpentis 1978 (RA=17h 48m 59.74s /
        // DEC=-14° 43' 08.2"
        Sexagesimal novaSerpentisRA = new Sexagesimal(17, 48, 59.74);
        Sexagesimal novaSerpentisDEC = new Sexagesimal(-14, 43, 8.2);
        EquatorialCoordinatesAdapter eca = new EquatorialCoordinatesAdapter(
                new EquatorialCoordinates(novaSerpentisRA.getValueAsUnits() * 15,
                        novaSerpentisDEC.getValueAsUnits()));
        Assert.assertEquals(12.959250041566406, eca.getGalacticLongitude(), 0);
        Assert.assertEquals(6.046298477995704, eca.getGalacticLatitude(), 0);
    }

    @Test
    public void testConvertEquatorialCoordinatesToHorizontalCoordinates() {
        MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();
        // Coordonnées Alt/Az de Saturne le 13 novembre 1978 à 4h 34m 00s UT
        // (RA=10h 57' 35.681\" / DEC=8° 25' 58.10\")
        // à l'Observatoire d'Uccle (longitude -0h 17m 25.94s / latitude +50°
        // 47' 55\")
        EquatorialCoordinatesAdapter eca = new EquatorialCoordinatesAdapter(
                new EquatorialCoordinates(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(10, 57,
                        35.681)), Sexagesimal.sexagesimalToDecimal(new Sexagesimal(8, 25, 58.10))));
        GeographicCoordinates observerSite = new GeographicCoordinates(
                Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
                -(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));
        double siderealTime = meeusEngine.getApparentSiderealTimeAsHoursFromJulianDay(
                JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));
        Sexagesimal azimut = new Sexagesimal(eca.getAzimuth(observerSite, siderealTime));
        Sexagesimal elevation = new Sexagesimal(eca.getElevation(observerSite, siderealTime));
        Assert.assertTrue("128° 17' 50.28337808412\"".equals(azimut.toString(SexagesimalType.DEGREES)));
        Assert.assertTrue("36° 32' 19.79962207152\"".equals(elevation.toString(SexagesimalType.DEGREES)));
    }
}
