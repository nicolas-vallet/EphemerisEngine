package com.nzv.astro.ephemeris.impl;


import com.nzv.astro.ephemeris.Sexagesimal;
import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import org.junit.Assert;
import org.junit.Test;

public class AtmosphericRefractionCalculatorImplTest {

    private AtmosphericRefractionCalculatorImpl underTest = new AtmosphericRefractionCalculatorImpl();

    @Test
    public void testGetTrueElevationFromApparentElevation() {
        // Hauteur vraie d'une étoile dont la hauteur est mesurée à 32° 04' 17"
        Sexagesimal apparentElevation = new Sexagesimal(32, 04, 17);
        double te = underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation));
        Sexagesimal trueElevation = Sexagesimal.decimalToSexagesimal(te);
        Assert.assertTrue("32° 2' 44.2397523882984\"".equals(trueElevation.toString(SexagesimalType.DEGREES)));

        // Hauteur vraie d'une étoile dont la hauteur apparente est de 2° 14' 19.34"
        apparentElevation = new Sexagesimal(2, 14, 19.34);
        te = underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation));
        trueElevation = Sexagesimal.decimalToSexagesimal(te);
        Assert.assertTrue("1° 57' 1.63899861464304\"".equals(trueElevation.toString(SexagesimalType.DEGREES)));

        // Influence de T(38° C) et P(1023 hPa)
        te = underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation), 38, 1023);
        trueElevation = Sexagesimal.decimalToSexagesimal(te);
        Assert.assertTrue("1° 58' 35.03208873932508\"".equals(trueElevation.toString(SexagesimalType.DEGREES)));

        apparentElevation = new Sexagesimal(90, 0, 0);
        te = underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation));
        trueElevation = Sexagesimal.decimalToSexagesimal(te);
        Assert.assertTrue("90° 0' 0.0\"".equals(trueElevation.toString(SexagesimalType.DEGREES)));

    }

    @Test
    public void testGetApparentElevationFromTrueElevation() {
        // Hauteur apparente d'une étoile dont la hauteur vraie est de 32° 2' 44.24"
        Sexagesimal trueElevation = new Sexagesimal(32, 2, 44.24);
        double ae = underTest.getApparentElevation(trueElevation.getValueAsUnits());
        Sexagesimal apparentElevation = Sexagesimal.decimalToSexagesimal(ae);
        Assert.assertTrue("32° 4' 17.000129442972\"".equals(apparentElevation.toString(SexagesimalType.DEGREES)));

        // Hauteur apparente d'une étoile dont la hauteur vraie est de 1° 57'
        trueElevation = new Sexagesimal(1, 57, 0);
        ae = underTest.getApparentElevation(trueElevation.getValueAsUnits());
        apparentElevation = Sexagesimal.decimalToSexagesimal(ae);
        Assert.assertTrue("2° 14' 19.34037953707776\"".equals(apparentElevation.toString(SexagesimalType.DEGREES)));

        // Hauteur apparente d'une étoile dont la hauteur vrai est de 90°
        trueElevation = new Sexagesimal(90, 0, 0);
        ae = underTest.getApparentElevation(trueElevation.getValueAsUnits());
        apparentElevation = Sexagesimal.decimalToSexagesimal(ae);
        Assert.assertTrue("90° 0' 0.0\"".equals(apparentElevation.toString(SexagesimalType.DEGREES)));
    }

    @Test
    public void testGetTrueElevationFromApparentElevationAndNormalClimaticConditions() {
        // Hauteur vraie d'une étoile dont la hauteur est mesurée à 32° 04' 17"
        // avec T(10°C) et P(1013 hPa)
        Sexagesimal apparentElevation = new Sexagesimal(32, 04, 17);
        double te = underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation), 10, 1013);
        Sexagesimal trueElevation = Sexagesimal.decimalToSexagesimal(te);
        Assert.assertTrue("32° 2' 44.2397523882984\"".equals(trueElevation.toString(SexagesimalType.DEGREES)));
        Assert.assertEquals(underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation)),
                underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation), 10, 1013), 0);
    }

    @Test
    public void testGetTrueElevationFromApparentElevationAndClimaticConditions() {
        // Hauteur vraie d'une étoile dont la hauteur est mesurée à 32° 04' 17"
        // avec T(25°C) et P(1020 hPa)
        Sexagesimal apparentElevation = new Sexagesimal(32, 04, 17);
        double te =
                underTest.getTrueElevation(Sexagesimal.sexagesimalToDecimal(apparentElevation), 25, 1020);
        Sexagesimal trueElevation = Sexagesimal.decimalToSexagesimal(te);
        Assert.assertTrue("32° 2' 48.559729634214\"".equals(trueElevation.toString(SexagesimalType.DEGREES)));
    }
}
