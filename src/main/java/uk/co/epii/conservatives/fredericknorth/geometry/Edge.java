package uk.co.epii.conservatives.fredericknorth.geometry;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.VertexExtensions;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 22:43
 */
public class Edge {

    public final Polygon polygon;
    public final Point.Float a;
    public final Point.Float b;

    public Edge(Polygon polygon, Point a, Point b) {
        this(polygon, new Point.Float(a.x, a.y), new Point.Float(b.x, b.y));
    }

    public Edge(Polygon polygon, Point.Float a, Point.Float b) {
        this.polygon = polygon;
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (!a.equals(edge.a)) return false;
        if (!b.equals(edge.b)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

    public NearestPoint getNearestPoint(Point2D.Float point) {
        Vertex A = new Vertex(new Edge(polygon,point, a), this);
        Vertex B = new Vertex(this, new Edge(polygon, b, point));
        if (A.getCommonPoint().equals(B.getCommonPoint())) {
            return new NearestPoint(polygon, A.getCommonPoint(), VertexExtensions.dSquared(A.getCommonPoint(), point), new Vertex[] {A});
        }
        double alpha;
        double beta;
        if ((alpha = A.getNonReflexAngle()) >= Math.PI / 2) {
            return new NearestPoint(polygon, A.getCommonPoint(), VertexExtensions.dSquared(A.getCommonPoint(), point), new Vertex[] {A});
        }
        if ((beta = B.getNonReflexAngle()) >= Math.PI / 2) {
            return new NearestPoint(polygon, B.getCommonPoint(), VertexExtensions.dSquared(B.getCommonPoint(), point), new Vertex[] {B});
        }
        float along = (float)(Math.cos(alpha) * Math.sqrt(VertexExtensions.dSquared(point, a)));
        float magnitudeOfV = (float)(Math.sqrt(VertexExtensions.dSquared(a, b)));
        Point2D.Float nearest = new Point2D.Float(
                a.x + along / magnitudeOfV * (b.x - a.x),
                a.y + along / magnitudeOfV * (b.y - a.y)
        );
        return new NearestPoint(polygon, nearest, VertexExtensions.dSquared(nearest, point), new Vertex[] {A, B});
    }
}
