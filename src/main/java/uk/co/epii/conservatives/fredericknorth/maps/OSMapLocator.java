package uk.co.epii.conservatives.fredericknorth.maps;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 17:54
 */
public interface OSMapLocator {
    public OSMap getMap(Point p);
    public List<OSMap> getMaps(Rectangle r);
    public Point getBottomLeftMapCoordinate(OSMap map);
    public OSMap create(String largeSquare, int square, String quadrant);
}
