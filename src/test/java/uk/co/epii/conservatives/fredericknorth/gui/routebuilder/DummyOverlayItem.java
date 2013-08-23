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

    public DummyOverlayItem(T item) {
        this.item = item;
    }

    @Override
    public Point getGeoLocationOfCenter() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public boolean contains(Point imagePoint,
                            ImageAndGeoPointTranslator imageAndGeoPointTranslator, OverlayRenderer<T> overlayRenderer) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containedWithin(Shape geoShape) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(OverlayItem o) {
        throw new UnsupportedOperationException("This operation is not supported in the dummy class");
    }
}
