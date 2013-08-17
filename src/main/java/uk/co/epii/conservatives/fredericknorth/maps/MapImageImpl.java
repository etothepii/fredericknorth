package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:18
 */
class MapImageImpl implements MapImage {

    private final BufferedImage map;
    private final Point geoTopLeft;
    private final Dimension size;

    MapImageImpl(BufferedImage map, Point geoTopLeft) {
        this.map = map;
        this.geoTopLeft = geoTopLeft;
        this.size = new Dimension(map.getWidth(), map.getHeight());
    }

    @Override
    public BufferedImage getMap() {
        return map;
    }

    @Override
    public Point getGeoTopLeft() {
        return geoTopLeft;
    }

    public Dimension getSize() {
        return size;
    }
}
