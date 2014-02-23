package uk.co.epii.conservatives.fredericknorth.geometry;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 09/09/2013
 * Time: 00:31
 */
public class ClippedSegment {
    private final List<Point> points;
    private final boolean inside;

    public ClippedSegment(Collection<Point> points, boolean inside) {
        this.points = new ArrayList<Point>(points);
        this.inside = inside;
    }

    public Point first() {
        return points.get(0);
    }

    public Point last() {
        return points.get(points.size() - 1);
    }

    public int size() {
        return points.size();
    }

    public boolean isInside() {
        return inside;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClippedSegment that = (ClippedSegment) o;

        if (inside != that.inside) return false;
        if (!points.equals(that.points)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = points.hashCode();
        result = 31 * result + (inside ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClippedSegment{" +
                "points=" + Arrays.toString(points.toArray(new Point[points.size()])) +
                ", inside=" + inside +
                '}';
    }

    public void prepend(Collection<Point> points) {
        this.points.addAll(0, points);
    }

    public List<Point> getPoints() {
        return points;
    }
}
