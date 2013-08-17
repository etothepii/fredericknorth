package uk.co.epii.conservatives.fredericknorth.extensions;

import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 25/07/2013
 * Time: 13:14
 */
public class NearestPoint {

    public final Point2D.Float point;
    public final double dSquared;
    public final Vertex[] nearestVertices;

    public NearestPoint(Point2D.Float point, double dSquared, Vertex[] nearestVertices) {
        this.point = point;
        this.dSquared = dSquared;
        this.nearestVertices = nearestVertices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NearestPoint that = (NearestPoint) o;

        if (Double.compare(that.dSquared, dSquared) != 0) return false;
        if (!point.equals(that.point)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = point.hashCode();
        temp = dSquared != +0.0d ? Double.doubleToLongBits(dSquared) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
