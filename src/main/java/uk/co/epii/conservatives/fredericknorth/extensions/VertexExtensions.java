package uk.co.epii.conservatives.fredericknorth.extensions;

import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 23:45
 */
public class VertexExtensions {

    public static double dSquared(Vertex a, Vertex b) {
        double d_x = a.getCommonPoint().x - b.getCommonPoint().x;
        double d_y = a.getCommonPoint().y - b.getCommonPoint().y;
        return d_x * d_x + d_y * d_y;
    }

    public static double dSquared(Point2D.Float a, Point2D.Float b) {
        double d_x = a.x - b.x;
        double d_y = a.y - b.y;
        return d_x * d_x + d_y * d_y;
    }
}
