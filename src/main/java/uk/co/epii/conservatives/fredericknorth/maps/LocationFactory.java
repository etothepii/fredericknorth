package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:22
 */
public interface LocationFactory {

    public Location getInstance(String name, Point p);
    public Rectangle calculatePaddedRectangle(java.util.List<? extends Location> locations);

}
