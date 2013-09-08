package uk.co.epii.conservatives.fredericknorth.geometry;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 08/09/2013
 * Time: 21:26
 */
public class RectangleIntersection {

    private final Point intersection;
    private final int edgeCrossed;

    public RectangleIntersection(Point intersection, int edgeCrossed) {
        this.intersection = intersection;
        this.edgeCrossed = edgeCrossed;
    }

    public Point getIntersection() {
        return intersection;
    }

    public int getEdgeCrossed() {
        return edgeCrossed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RectangleIntersection that = (RectangleIntersection) o;

        if (edgeCrossed != that.edgeCrossed) return false;
        if (!intersection.equals(that.intersection)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = intersection.hashCode();
        result = 31 * result + edgeCrossed;
        return result;
    }

    @Override
    public String toString() {
        return "RectangleIntersection{" +
                "intersection=" + intersection +
                ", edgeCrossed=" + edgeCrossed +
                '}';
    }
}
