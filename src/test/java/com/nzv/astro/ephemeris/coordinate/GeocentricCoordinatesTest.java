package com.nzv.astro.ephemeris.coordinate;

import org.junit.Assert;
import org.junit.Test;

public class GeocentricCoordinatesTest {

    @Test
    public void testConstructor() {
        GeocentricCoordinates gc = new GeocentricCoordinates(50, 47, 55, 105);
        Assert.assertEquals(0.7713061425409441, gc.getAbscissa(), 0);
        Assert.assertEquals(0.6333327780316349, gc.getOrdinate(), 0);
    }
}
