package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:19
 */
public interface MapImageFactory {

    public MapImage getInstance(BufferedImage image, Rectangle geoCoverage, OSMapType osMapType, double scale);

}
