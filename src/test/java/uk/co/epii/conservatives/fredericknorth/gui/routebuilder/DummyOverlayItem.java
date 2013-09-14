package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayRenderer;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 13:54
 */
public class DummyOverlayItem<T> implements OverlayItem<T> {

    private final T item;
    private Point geoLocationOfCentre;

    public DummyOverlayItem(T item) {
        this.item = item;
    }

    @Override
    public Point getGeoLocationOfCenter() {
        return geoLocationOfCentre;
    }

    public void setGeoLocationOfCenter(Point geoLocationOfCentre) {
        this.geoLocationOfCentre = geoLocationOfCentre;
    }

    @Override
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        throw new UnsupportedOperationException("This operation is not supported in the dummy class");
    }

    @Override
    public T getItem() {
        return item;
    }

    @Override
    public int getPriority() {
        throw new UnsupportedOperationException("This operation is not supported in the dummy class");
    }

    @Override
    public int compareTo(OverlayItem o) {
        throw new UnsupportedOperationException("This operation is not supported in the dummy class");
    }
}
