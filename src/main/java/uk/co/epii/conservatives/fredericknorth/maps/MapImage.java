package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 29/06/2013
 * Time: 21:44
 */
public interface MapImage {

    public BufferedImage getMap();
    public Point getGeoTopLeft();
    public Point getGeoCenter();
    public Dimension getSize();
    public Point getGeoBottomLeft();
}
