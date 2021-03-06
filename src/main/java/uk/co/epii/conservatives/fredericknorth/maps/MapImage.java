package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 29/06/2013
 * Time: 21:44
 */
public interface MapImage extends ImageAndGeoPointTranslator {

    public BufferedImage getMap();
    public Point getGeoTopLeft();
    public Dimension getSize();
    public double getScale();
    public OSMapType getOSMapType();

}
