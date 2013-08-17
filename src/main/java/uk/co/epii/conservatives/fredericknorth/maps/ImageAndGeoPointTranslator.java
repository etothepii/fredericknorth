package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 19:50
 */
public interface ImageAndGeoPointTranslator {
    public Point getGeoLocation(Point pointOnImage);
    public Point getImageLocation(Point geoLocation);
    public AffineTransform getGeoTransform();
}
