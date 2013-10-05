package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: James Robinson
 * Date: 07/09/2013
 * Time: 10:43
 */
public class DummyImageAndGeoPointTranslator implements ImageAndGeoPointTranslator {

    @Override
    public Point getGeoLocation(Point pointOnImage) {
        return pointOnImage;
    }

    @Override
    public Point getImageLocation(Point geoLocation) {
        return geoLocation;
    }

    @Override
    public AffineTransform getGeoToImageTransform() {
        return AffineTransform.getTranslateInstance(0, 0);
    }

    @Override
    public AffineTransform getImageToGeoTransform() {
        return AffineTransform.getTranslateInstance(0, 0);
    }
}
