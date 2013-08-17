package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 02/08/2013
 * Time: 21:31
 */
public interface MouseLocation {
    public Point getImageLocation();
    public Point getGeoLocation();
    public boolean isMouseStuck();
}
