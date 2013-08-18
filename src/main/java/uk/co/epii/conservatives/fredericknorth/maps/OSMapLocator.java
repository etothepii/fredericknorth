package uk.co.epii.conservatives.fredericknorth.maps;


import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 17:54
 */
public interface OSMapLocator {
    public OSMap getMap(OSMapType mapType, Point p);
    public Set<OSMap> getMaps(OSMapType osMapType, Rectangle r);
    public Point getBottomLeftMapCoordinate(OSMap map);
    public Dimension getImageSize(OSMapType osMapType);
    public Dimension getRepresentedSize(OSMapType osMapType);
}
