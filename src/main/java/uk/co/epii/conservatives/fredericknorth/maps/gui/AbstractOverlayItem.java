package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 00:10
 */
public abstract class AbstractOverlayItem<T> implements OverlayItem<T> {

    private final T t;
    private final int priority;

    protected AbstractOverlayItem(T t, int priority) {
        this.t = t;
        this.priority = priority;
    }

    @Override
    public abstract Point getGeoLocationOfCenter();

    @Override
    public abstract Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator);

    @Override
    public T getItem() {
        return t;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean contains(Point imagePoint, ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                            OverlayRenderer<T> overlayRenderer) {
        Component rendered = overlayRenderer.getOverlayRendererComponent(this, imageAndGeoPointTranslator, null);
        rendered.setSize(rendered.getPreferredSize());
        Point topLeft = getTopLeft(rendered.getPreferredSize(), imageAndGeoPointTranslator);
        Point localPoint = new Point(imagePoint.x - topLeft.x, imagePoint.y - topLeft.y);
        return rendered.contains(localPoint);
    }

    @Override
    public abstract boolean containedWithin(Shape geoShape);

    @Override
    public int compareTo(OverlayItem o) {
        if (priority != o.getPriority()) return priority - o.getPriority();
        if (getGeoLocationOfCenter().x != o.getGeoLocationOfCenter().x)
            return getGeoLocationOfCenter().x - o.getGeoLocationOfCenter().x;
        if (getGeoLocationOfCenter().y != o.getGeoLocationOfCenter().y)
            return getGeoLocationOfCenter().y - o.getGeoLocationOfCenter().y;
        try {
            return compareToSameGeneric((OverlayItem<T>) o);
        }
        catch (ClassCastException cce) {
            return o.getClass().getName().compareTo(getClass().getName());
        }
    }

    protected abstract int compareToSameGeneric(OverlayItem<T> o);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractOverlayItem that = (AbstractOverlayItem) o;
        return t.equals(that.t);
    }

    @Override
    public int hashCode() {
        return t.hashCode();
    }
}
