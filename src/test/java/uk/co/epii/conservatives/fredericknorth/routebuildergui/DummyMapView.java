package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 16:12
 */
public class DummyMapView implements MapView {

    @Override
    public BufferedImage getMap() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Dimension getSize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Point getGeoLocation(Point pointOnImage) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Point getImageLocation(Point geoLocation) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AffineTransform getGeoTransform() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Location getGeoLocation(Location pointOnImage) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Location getImageLocation(Location geoLocation) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Location> getGeoLocations(List<? extends Location> pointOnImages) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Location> getImageLocations(List<? extends Location> geoLocations) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BufferedImage getLabelledImage(List<? extends Location> locations) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getScale() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Point getNewGeoCenter(Point zoomAt, double newScale) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Point getNewGeoCenter(Point geoMouseLastPressedAt, Point movedTo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
