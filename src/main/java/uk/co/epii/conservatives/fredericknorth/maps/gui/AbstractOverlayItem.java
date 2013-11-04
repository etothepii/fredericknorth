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
    public int compareTo(OverlayItem o) {
        if (priority != o.getPriority()) return priority - o.getPriority();
        Point location = getGeoLocationOfCenter();
        Point thatLocation = o.getGeoLocationOfCenter();
        if (location == null ^ thatLocation == null) {
            return location == null ? -1 : 1;
        }
        if (location != null && thatLocation != null) {
            if (getGeoLocationOfCenter().x != o.getGeoLocationOfCenter().x)
                return getGeoLocationOfCenter().x - o.getGeoLocationOfCenter().x;
            if (getGeoLocationOfCenter().y != o.getGeoLocationOfCenter().y)
                return getGeoLocationOfCenter().y - o.getGeoLocationOfCenter().y;
        }
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
        if (priority != that.priority) return false;
        if (!t.equals(that.t)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = t.hashCode();
        result = 31 * result + priority;
        return result;
    }
}
