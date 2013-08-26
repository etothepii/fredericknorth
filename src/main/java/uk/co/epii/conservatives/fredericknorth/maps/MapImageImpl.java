package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:18
 */
class MapImageImpl implements MapImage {

    private final OSMapType osMapType;
    private final BufferedImage map;
    private final Point geoTopLeft;
    private final Dimension size;

    MapImageImpl(BufferedImage map, Point geoTopLeft, OSMapType osMapType) {
        this.map = map;
        this.osMapType = osMapType;
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

    @Override
    public Point getGeoCenter() {
        return new Point(
                (int)(geoTopLeft.x + size.width / osMapType.getScale() / 2),
                (int)(geoTopLeft.y - size.height / osMapType.getScale() / 2));
    }

    public Dimension getSize() {
        return size;
    }

    @Override
    public Point getGeoBottomLeft() {
        return new Point(
                (int)(geoTopLeft.x + size.width / osMapType.getScale() / 2),
                (int)(geoTopLeft.y - size.height / osMapType.getScale()));
    }
}
