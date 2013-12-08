package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.DummyMapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 16:36
 */
public class DummyMapViewGeneratorFactory {
    public static MapViewGenerator getDummyInstance(ApplicationContext applicationContext, OSMapType osMapType, Rectangle coverage) {
        EnumMap<OSMapType, MapImage> mapCache = new EnumMap<OSMapType, MapImage>(OSMapType.class);
        mapCache.put(OSMapType.MINI,
                new MapImageImpl(
                        new BufferedImage(
                                coverage.width,
                                coverage.height,
                                BufferedImage.TYPE_INT_ARGB),
                        coverage, osMapType, 1d));
        return new MapViewGeneratorImpl(applicationContext, mapCache, null, null);
    }
}
