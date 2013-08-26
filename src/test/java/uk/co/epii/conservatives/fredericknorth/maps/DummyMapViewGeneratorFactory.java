package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 16:36
 */
public class DummyMapViewGeneratorFactory {
    public static MapViewGenerator getDummyInstance(OSMapType osMapType, Rectangle coverage) {
        EnumMap<OSMapType, MapImage> mapCache = new EnumMap<OSMapType, MapImage>(OSMapType.class);
        mapCache.put(OSMapType.MINI,
                new MapImageImpl(
                        new BufferedImage(
                                coverage.width,
                                coverage.height,
                                BufferedImage.TYPE_INT_ARGB),
                        coverage.getLocation(), osMapType));
        return new MapViewGeneratorImpl(mapCache, null, null);
    }
}
