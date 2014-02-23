package uk.co.epii.conservatives.fredericknorth.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.RectangleCollection;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:18
 */
class MapImageImpl implements MapImage {

    private static final Logger LOG = LoggerFactory.getLogger(MapImageImpl.class);

    private final OSMapType osMapType;
    private final BufferedImage map;
    private final Rectangle geoCoverage;
    private final Point geoTopLeft;
    private final Dimension size;
    private final double scale;
    private final RectangleCollection rectangleCollection;
    private boolean completelyLoaded;
    private final AffineTransform geoToImageTransform;
    private final AffineTransform imageToGeoTransform;

    MapImageImpl(BufferedImage map, Rectangle geoCoverage,
                 OSMapType osMapType, double scale) {
        rectangleCollection = new RectangleCollection();
        this.map = map;
        this.osMapType = osMapType;
        this.geoCoverage = geoCoverage;
        geoTopLeft = new Point(geoCoverage.x, geoCoverage.y + geoCoverage.height);
        this.size = new Dimension(map.getWidth(), map.getHeight());
        this.scale = scale;
        geoToImageTransform = AffineTransform.getScaleInstance(scale, -scale);
        geoToImageTransform.translate(-geoTopLeft.x, -geoTopLeft.y);
        try {
            imageToGeoTransform = geoToImageTransform.createInverse();
        }
        catch (NoninvertibleTransformException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalArgumentException("A scale of 0 has no discernible meaning", e);
        }
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
    public AffineTransform getGeoToImageTransform() {
        return geoToImageTransform;
    }

    @Override
    public AffineTransform getImageToGeoTransform() {
        return imageToGeoTransform;
    }

    @Override
    public OSMapType getOSMapType() {
        return osMapType;
    }

    public void reportDrawn(Rectangle rectangle) {
        Rectangle intersection = geoCoverage.intersection(rectangle);
        if (intersection.width * intersection.height > 0) {
            rectangleCollection.add(intersection);
        }
    }

    public Collection<Rectangle> getCleanRectangles() {
        return rectangleCollection;
    }

    public Rectangle getGeoCoverage() {
        return geoCoverage;
    }
}
