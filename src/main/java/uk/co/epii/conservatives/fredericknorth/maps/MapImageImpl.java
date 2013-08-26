package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
    private final double scale;
    private boolean completelyLoaded;

    MapImageImpl(BufferedImage map, Point geoTopLeft, OSMapType osMapType, double scale) {
        this.map = map;
        this.osMapType = osMapType;
        this.geoTopLeft = geoTopLeft;
        this.size = new Dimension(map.getWidth(), map.getHeight());
        this.scale = scale;
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
    public Dimension getSize() {
        return size;
    }

    public boolean isCompletelyLoaded() {
        return completelyLoaded;
    }

    public void setCompletelyLoaded(boolean completelyLoaded) {
        this.completelyLoaded = completelyLoaded;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Point getGeoLocation(Point imageLocation) {
        if (geoTopLeft == null || size == null || imageLocation == null) return null;
        double x = geoTopLeft.x + imageLocation.x / scale;
        double y = geoTopLeft.y - imageLocation.y / scale;
        return new Point((int)x, (int)y);
    }

    @Override
    public Point getImageLocation(Point geoLocation) {
        double x = (geoLocation.x - geoTopLeft.x) * scale;
        double y = (geoTopLeft.y - geoLocation.y) * scale;
        return new Point((int)x, (int)y);
    }

    @Override
    public AffineTransform getGeoTransform() {
        AffineTransform transform = AffineTransform.getTranslateInstance(-geoTopLeft.x, -geoTopLeft.y);
        transform.scale(scale, scale);
        return transform;
    }

    @Override
    public OSMapType getOSMapType() {
        return osMapType;
    }
}
