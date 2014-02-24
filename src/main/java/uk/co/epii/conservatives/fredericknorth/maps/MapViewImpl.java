package uk.co.epii.conservatives.fredericknorth.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 22:07
 */
public class MapViewImpl implements MapView {

    private static final Logger LOG = LoggerFactory.getLogger(MapViewImpl.class);

    private final BufferedImage map;
    private final Point geoCenter;
    private final Dimension viewPortSize;
    private final double scale;
    private final LocationFactory locationFactory;
    private final MapLabelFactory mapLabelFactory;

    public MapViewImpl(BufferedImage map, Point geoCenter, Dimension viewPortSize, double scale,
                       LocationFactory locationFactory, MapLabelFactory mapLabelFactory) {
        this.map = map;
        this.geoCenter = geoCenter;
        this.viewPortSize = viewPortSize;
        this.scale = scale;
        this.locationFactory = locationFactory;
        this.mapLabelFactory = mapLabelFactory;
    }

    @Override
    public BufferedImage getMap() {
        return map;
    }

    @Override
    public Dimension getSize() {
        return viewPortSize;
    }

    @Override
    public Point getGeoLocation(Point imageLocation) {
        if (geoCenter == null || viewPortSize == null || imageLocation == null) return null;
        double x = geoCenter.x + (imageLocation.x - viewPortSize.getWidth() / 2) / scale;
        double y = geoCenter.y - (imageLocation.y - viewPortSize.getHeight() / 2) / scale;
        return new Point((int)x, (int)y);
    }

    @Override
    public Point getImageLocation(Point geoLocation) {
        double x = viewPortSize.getWidth() / 2 + (geoLocation.x - geoCenter.x) * scale;
        double y = viewPortSize.getHeight() / 2 - (geoLocation.y - geoCenter.y) * scale;
        return new Point((int)x, (int)y);
    }

    @Override
    public AffineTransform getGeoToImageTransform() {
        AffineTransform transform = AffineTransform.getTranslateInstance(viewPortSize.getWidth() / 2, viewPortSize.getHeight() / 2);
        transform.scale(scale, -scale);
        transform.translate(-geoCenter.x, -geoCenter.y);
        return transform;
    }

    @Override
    public AffineTransform getImageToGeoTransform() {
        try {
            return getGeoToImageTransform().createInverse();
        }
        catch (NoninvertibleTransformException nte) {
            LOG.error(nte.getMessage(), nte);
            throw new RuntimeException(nte);
        }
    }

    @Override
    public Location getGeoLocation(Location imageLocation) {
        return locationFactory.getInstance(imageLocation.getName(), getGeoLocation(imageLocation.getPoint()));
    }

    @Override
    public Location getImageLocation(Location geoLocation) {
        return locationFactory.getInstance(geoLocation.getName(), getImageLocation(geoLocation.getPoint()));
    }

    @Override
    public List<Location> getGeoLocations(List<? extends Location> imageLocations) {
        List<Location> geoLocations = new ArrayList<Location>(imageLocations.size());
        for (Location locationOnImage : imageLocations) {
            geoLocations.add(getGeoLocation(locationOnImage));
        }
        return geoLocations;
    }

    @Override
    public List<Location> getImageLocations(List<? extends Location> geoLocations) {
        List<Location> imageLocations = new ArrayList<Location>(geoLocations.size());
        for (Location geoLocation : geoLocations) {
            imageLocations.add(getImageLocation(geoLocation));
        }
        return imageLocations;
    }

    @Override
    public BufferedImage getLabelledImage(List<? extends Location> locations) {
        BufferedImage bufferedImage = new BufferedImage(viewPortSize.width, viewPortSize.height, map.getType());
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(map, 0, 0, null);
        for (MapLabel mapLabel : mapLabelFactory.getMapLabels(new Rectangle(viewPortSize), getImageLocations(locations), g, this)) {
            mapLabel.paint(g);
        }
        return bufferedImage;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Point getNewGeoCenter(Point zoomAt, double newScale) {
        Point geoZoomAt = getGeoLocation(zoomAt);
        Point2D.Float vectorToMiddle = new Point2D.Float(
                (zoomAt.x - viewPortSize.width / 2f),
                (viewPortSize.height / 2f - zoomAt.y));
        return new Point(
                Math.round(geoZoomAt.x - vectorToMiddle.x / (float)newScale),
                Math.round(geoZoomAt.y - vectorToMiddle.y / (float)newScale));
    }

    @Override
    public Point getNewGeoCenter(Point geoMouseLastPressedAt, Point movedTo) {
        Point2D.Float vectorToMiddle = new Point2D.Float(
                movedTo.x - viewPortSize.width / 2f,
                viewPortSize.height / 2f - movedTo.y);
        return new Point(
                Math.round(geoMouseLastPressedAt.x - vectorToMiddle.x / (float)scale),
                Math.round(geoMouseLastPressedAt.y - vectorToMiddle.y / (float)scale));
    }
}
