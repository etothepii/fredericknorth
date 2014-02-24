package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;

/**
 * User: James Robinson
 * Date: 03/08/2013
 * Time: 13:20
 */
public class PointExtensions {

    public static Point getMedian(Collection<? extends Point> points) {
        int[] xpoints = new int[points.size()];
        int[] ypoints = new int[points.size()];
        int index = 0;
        for (Point point : points) {
            xpoints[index] = point.x;
            ypoints[index] = point.y;
            index++;
        }
        Arrays.sort(xpoints);
        Arrays.sort(ypoints);
        if (points.size() % 2 == 1) {
            return new Point(xpoints[points.size() / 2], ypoints[points.size() / 2]);
        }
        else {
            return new Point((xpoints[points.size() / 2] + xpoints[points.size() / 2 - 1]) / 2,
                    (ypoints[points.size() / 2] + ypoints[points.size() / 2 - 1]) / 2);
        }
    }

    public static Rectangle getBounds(Collection<? extends Point> points) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point point : points) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public static Point2D.Float toFloat(Point point) {
        return new Point2D.Float(point.x, point.y);
    }

    public static Point2D.Double toDouble(Point point) {
        return new Point2D.Double(point.x, point.y);
    }

    public static Point fromFloat(Point2D.Float point) {
        return new Point(Math.round(point.x), Math.round(point.y));
    }

    public static Point fromDouble(Point2D.Double point) {
        return new Point((int)Math.round(point.x), (int)Math.round(point.y));
    }

    public static String getLocationString(Point point) {
        return String.format("E%sN%s", point.x, point.y);
    }

    public static boolean isNearEdge(Point a, Point b, Point c, double tolerenceSquared) {
        double crossProduct = crossProduct(a, b, c);
        double distSquared = crossProduct * crossProduct / distanceSquared(a, b);
        double dot1 = dotProduct(a, b, c);
        if (dot1 > 0) {
            if (distanceSquared(b, c) < tolerenceSquared) {
                return true;
            }
            return false;
        }
        double dot2 = dotProduct(b, a, c);
        if (dot2 > 0) {
            if (distanceSquared(a, c) < tolerenceSquared) {
                return true;
            }
            return false;
        }
        if (distSquared < tolerenceSquared) {
            return true;
        }
        return false;
    }

    private static double dotProduct(Point a, Point b, Point c)
    {
        double[] AB = new double[2];
        double[] BC = new double[2];
        AB[0] = b.x - a.x;
        AB[1] = b.y - a.y;
        BC[0] = c.x - b.x;
        BC[1] = c.y - b.y;
        double dot = AB[0] * BC[0] + AB[1] * BC[1];

        return dot;
    }

    private static double crossProduct(Point a, Point b, Point c)
    {
        double[] AB = new double[2];
        double[] AC = new double[2];
        AB[0] = b.x - a.x;
        AB[1] = b.y - a.y;
        AC[0] = c.x - a.x;
        AC[1] = c.y - a.y;
        return AB[0] * AC[1] - AB[1] * AC[0];
    }

    private static double distanceSquared(Point a, Point b) {

        double d1 = a.x - b.x;
        double d2 = a.y - b.y;
        return d1 * d1 + d2 * d2;
    }
}
