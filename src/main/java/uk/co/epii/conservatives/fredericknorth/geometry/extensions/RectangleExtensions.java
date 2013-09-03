package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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
}
