package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 00:17
 */
public class MapImageFactoryImpl implements MapImageFactory {

    @Override
    public MapImage getInstance(BufferedImage image, Rectangle geoCoverage, OSMapType osMapType, double scale) {
            return new MapImageImpl(image, geoCoverage, osMapType, scale);
    }
}
