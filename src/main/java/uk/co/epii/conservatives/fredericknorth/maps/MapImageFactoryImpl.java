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
    public MapImage getInstance(BufferedImage image, Point geoTopLeft, OSMapType osMapType, double scale) {
            return new MapImageImpl(image, geoTopLeft, osMapType, scale);
    }
}
