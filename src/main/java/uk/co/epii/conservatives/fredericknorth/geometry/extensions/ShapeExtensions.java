package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 23/02/2014
 * Time: 12:17
 */
public class ShapeExtensions {

    public static boolean contains(Shape[] shapes, Point point) {
        for (Shape shape : shapes) {
            if (shape.contains(point)) return true;
        }
        return false;
    }

    public static boolean contains(Shape[] shapes, int x, int y) {
        return contains(shapes, new Point(x, y));
    }

    public static boolean contains(Shape[] shapes, double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) return true;
        }
        return false;
    }

    public static Rectangle getBounds(Shape[] areas) {
        Rectangle bounds = null;
        for (Shape polygon : areas) {
            if (bounds == null) {
                bounds = polygon.getBounds();
            }
            else {
                bounds = bounds.union(polygon.getBounds());
            }
        }
        return bounds;
    }
}
