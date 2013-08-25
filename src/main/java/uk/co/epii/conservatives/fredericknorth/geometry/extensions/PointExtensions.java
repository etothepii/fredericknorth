package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import java.awt.*;
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

}
