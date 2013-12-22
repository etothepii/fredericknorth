package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.Point;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 16:24
 */
public class LocationImpl implements Location {
    private final String name;
    private final Point point;

    public LocationImpl(String name, Point point) {
        this.name = name;
        this.point = point;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return "LocationImpl{" +
                "name='" + name + '\'' +
                ", point=" + point +
                '}';
    }
}
