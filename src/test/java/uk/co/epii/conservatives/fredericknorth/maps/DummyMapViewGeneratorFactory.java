package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 16:36
 */
public class DummyMapViewGeneratorFactory {
    public static MapViewGenerator getDummyInstance(Rectangle coverage) {
        return new MapViewGeneratorImpl(
                new MapImageImpl(
                        new BufferedImage(
                                coverage.width,
                                coverage.height,
                                BufferedImage.TYPE_INT_ARGB),
                        coverage.getLocation()), null, null);
    }
}
