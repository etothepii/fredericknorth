package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.RectangleIntersection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 24/08/2013
 * Time: 17:17
 */
public class RectangleExtensions {

    private static Logger LOG = LoggerFactory.getLogger(RectangleExtensions.class);

    public static Point getCenter(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }

    public static Rectangle getScaleInstance(Rectangle rectangle, Point point, double scale) {
        int x = (int)(point.x + scale * (rectangle.x - point.x));
        int y = (int)(point.y + scale * (rectangle.y - point.y));
        return new Rectangle(x, y, (int)(rectangle.width * scale), (int)(rectangle.height * scale));
    }

    public static Rectangle grow(Rectangle rectangle, int growBy) {
        return new Rectangle(rectangle.x - growBy, rectangle.y - growBy,
                rectangle.width + growBy * 2, rectangle.height + growBy * 2);
    }

    public static List<Rectangle> getSurrounding(Rectangle base, Rectangle fillAround) {
        List<Rectangle> rectangles = getOverlappingSurrounding(base, fillAround);
        for (int i = 1; i < rectangles.size(); i++) {
            Rectangle unchanging = rectangles.get(i);
            for (int j = 0; j < i; j++) {
                Rectangle reducing = rectangles.get(j);
                Rectangle intersection = unchanging.intersection(reducing);
                if (intersection.width * (long)intersection.height > 0) {
                    List<Rectangle> surrounding = getOverlappingSurrounding(reducing, intersection);
                    if (surrounding.size() == 1) {
                        Rectangle reduced = surrounding.get(0);
                        reduced = reduced.intersection(reducing);
                        rectangles.set(j, reduced);
//                        rectangles.set(j, surrounding.get(0));
                    }
                    else {
                        throw new RuntimeException("Confusion");
                    }
                }
            }
        }
        for (int i = rectangles.size() - 1; i >= 0; i--) {
            Rectangle rectangle = rectangles.get(i);
            if (rectangle.width * rectangle.height == 0) {
                rectangles.remove(i);
            }
        }
        return rectangles;
    }

    private static List<Rectangle> getOverlappingSurrounding(Rectangle base, Rectangle fillAround) {
        LOG.debug("base: {}", base);
        LOG.debug("fillAround: {}", fillAround);
        java.util.List<Rectangle> rectangles = new ArrayList<Rectangle>(4);
        if (fillAround.x > base.x) {
            rectangles.add(new Rectangle(base.x, base.y, fillAround.x - base.x, base.height));
        }
        if (fillAround.y > base.y) {
            rectangles.add(new Rectangle(base.x, base.y, base.width, fillAround.y - base.y));
        }
        if (base.width + base.x > fillAround.width + fillAround.x) {
            rectangles.add(new Rectangle(fillAround.x + fillAround.width, base.y,
                    base.width + base.x - fillAround.width - fillAround.x, base.height));
        }
        if (base.height + base.y > fillAround.height + fillAround.y) {
            rectangles.add(new Rectangle(base.x, fillAround.y + fillAround.height, base.width,
                    base.height + base.y - fillAround.height - fillAround.y));
        }
        return rectangles;
    }

    public static List<Rectangle> getSurrounding(Rectangle base, Collection<Rectangle> fillAround) {
        List<Rectangle> surrounding = new ArrayList<Rectangle>(Arrays.asList(base));
        for (Rectangle rectangle : fillAround) {
            List<Rectangle> previouslySurrounding = surrounding;
            surrounding = new ArrayList<Rectangle>(surrounding.size() * 4);
            for (Rectangle previous : previouslySurrounding) {
                if (rectangle.intersects(previous)) {
                    surrounding.addAll(getSurrounding(previous, rectangle));
                }
                else {
                    surrounding.add(previous);
                }
            }
        }
        return surrounding;
    }

    public static RectangleIntersection[] getIntersection(Rectangle rectangle, Point from, Point to) {
        List<RectangleIntersection> intersectionsList = new ArrayList<RectangleIntersection>(2);
        Line2D.Float line = new Line2D.Float(from.x, from.y, to.x, to.y);
        if (line.intersectsLine(
                rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y)) {
            double ratio = (rectangle.y - from.getY()) / (from.getY() - to.getY());
            double dx = ratio * (from.getX() - to.getX());
            intersectionsList.add(new RectangleIntersection(new Point((int) (dx + from.getX()), rectangle.y), SwingConstants.NORTH));
        }
        if (line.intersectsLine(
                rectangle.x + rectangle.width, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height)) {
            double ratio = (rectangle.x + rectangle.width - to.getX()) / (to.getX() - from.getX());
            double dy = ratio * (to.getY() - from.getY());
            intersectionsList.add(new RectangleIntersection(new Point(rectangle.x + rectangle.width, (int) (dy + to.getY())), SwingConstants.EAST));
        }
        if (line.intersectsLine(
                rectangle.x + rectangle.width, rectangle.y + rectangle.height, rectangle.x, rectangle.y + rectangle.height)) {
            double ratio = (rectangle.y + rectangle.height - to.getY()) / (to.getY() - from.getY());
            double dx = ratio * (to.getX() - from.getX());
            intersectionsList.add(new RectangleIntersection(new Point((int) (dx + to.getX()), rectangle.y + rectangle.height), SwingConstants.SOUTH));

        }
        if (line.intersectsLine(
                rectangle.x, rectangle.y + rectangle.height, rectangle.x, rectangle.y)) {
            double ratio = (rectangle.x - from.getX()) / (from.getX() - to.getX());
            double dy = ratio * (from.getY() - to.getY());
            intersectionsList.add(new RectangleIntersection(new Point(rectangle.x, (int) (dy + from.getY())), SwingConstants.WEST));
        }
        if (intersectionsList.isEmpty()) {
            return new RectangleIntersection[0];
        }
        else if (intersectionsList.size() == 1) {
            return new RectangleIntersection[] {intersectionsList.get(0)};
        }
        else if (intersectionsList.size() == 2) {
            RectangleIntersection[] intersections = new RectangleIntersection[] {
                    intersectionsList.get(0), intersectionsList.get(1)
            };
            if (shouldSwitchRectangleIntersections(
                    intersections[0].getEdgeCrossed(),
                    intersections[1].getEdgeCrossed(),
                    from, to)) {
                return new RectangleIntersection[] {intersections[1], intersections[0]};
            }
            return intersections;
        }
        throw new RuntimeException("It is not possible to cross more than two edges of a rectangle with a straight line. " +
                "You have a Euclidean Plane Fail");
    }

    public static Integer getEdge(Rectangle rectangle, Point point) {
        if (point.x == rectangle.x && point.y == rectangle.y) {
            return SwingConstants.NORTH_WEST;
        }
        else if (point.x == rectangle.x + rectangle.width && point.y == rectangle.y) {
            return SwingConstants.NORTH_EAST;
        }
        else if (point.x == rectangle.x && point.y == rectangle.y + rectangle.height) {
            return SwingConstants.SOUTH_WEST;
        }
        else if (point.x == rectangle.x + rectangle.width && point.y == rectangle.y + rectangle.height) {
            return SwingConstants.SOUTH_EAST;
        }
        else if (point.y == rectangle.y && point.x > rectangle.x && point.x < rectangle.x + rectangle.width) {
            return SwingConstants.NORTH;
        }
        else if (point.y == rectangle.y + rectangle.height && point.x > rectangle.x && point.x < rectangle.x + rectangle.width) {
            return SwingConstants.SOUTH;
        }
        else if (point.x == rectangle.x && point.y > rectangle.y && point.y < rectangle.y + rectangle.height) {
            return SwingConstants.WEST;
        }
        else if (point.x == rectangle.x + rectangle.width && point.y > rectangle.y && point.y < rectangle.y + rectangle.height) {
            return SwingConstants.EAST;
        }
        return null;
    }

    static boolean shouldSwitchRectangleIntersections(int edgeCrossedA, int edgeCrossedB, Point from, Point to) {
        switch (edgeCrossedA) {
            case SwingConstants.NORTH:
                switch (edgeCrossedB) {
                    case SwingConstants.EAST:
                        return to.x < from.x;
                    case SwingConstants.SOUTH:
                        return to.y < from.y;
                    case SwingConstants.WEST:
                        return to.x > from.x;
                }
            case SwingConstants.EAST:
                switch (edgeCrossedB) {
                    case SwingConstants.NORTH:
                        return to.x > from.x;
                    case SwingConstants.SOUTH:
                        return to.y < from.y;
                    case SwingConstants.WEST:
                        return to.x > from.x;
                }
            case SwingConstants.SOUTH:
                switch (edgeCrossedB) {
                    case SwingConstants.NORTH:
                        return to.y > from.y;
                    case SwingConstants.EAST:
                        return to.y > from.y;
                    case SwingConstants.WEST:
                        return to.x > from.x;
                }
            case SwingConstants.WEST:
                switch (edgeCrossedB) {
                    case SwingConstants.NORTH:
                        return to.x < from.x;
                    case SwingConstants.EAST:
                        return to.x < from.x;
                    case SwingConstants.SOUTH:
                        return to.x < from.x;
                }
        }
        throw new IllegalArgumentException(String.format(
                "The combination of edges crossed %d and %d is not supported", edgeCrossedA, edgeCrossedB));
    }

    public static Polygon toPolygon(Rectangle rectangle) {
        return new Polygon(
                new int[] {rectangle.x, rectangle.x + rectangle.width, rectangle.x + rectangle.width, rectangle.x},
                new int[] {rectangle.y, rectangle.y, rectangle.y+ rectangle.height, rectangle.y + rectangle.height},
                4);
    }

    public static Rectangle fromPoints(Point from, Point to) {
        return new Rectangle(
                new Point(Math.min(from.x, to.x), Math.min(from.y, to.y)),
                new Dimension(Math.abs(to.x - from.x), Math.abs(to.y - from.y)));
    }

    public static Rectangle grow(Rectangle bounds, double growBy) {
        return grow(bounds, (int)(Math.max(bounds.height * growBy, bounds.width * growBy) / 2));
    }
}
