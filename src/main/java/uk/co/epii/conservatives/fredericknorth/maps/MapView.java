package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 22:06
 */
public interface MapView extends ImageAndGeoPointTranslator {

    public BufferedImage getMap();
    public Dimension getSize();
    public Location getGeoLocation(Location pointOnImage);
    public Location getImageLocation(Location geoLocation);
    public List<Location> getGeoLocations(List<? extends Location> pointOnImages);
    public List<Location> getImageLocations(List<? extends Location> geoLocations);
    public BufferedImage getLabelledImage(List<? extends Location> locations);
    public double getScale();
    public Point getNewGeoCenter(Point zoomAt, double newScale);
    public Point getNewGeoCenter(Point geoMouseLastPressedAt, Point movedTo);
}
