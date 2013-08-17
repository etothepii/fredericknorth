package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 10:05
 */
public interface OverlayItem<T> extends Comparable<OverlayItem> {
    public Point getGeoLocationOfCenter();
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator);
    public T getItem();
    public int getPriority();
    public boolean contains(Point imagePoint, ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                            OverlayRenderer<T> overlayRenderer);
    public boolean containedWithin(Shape geoShape);
}
