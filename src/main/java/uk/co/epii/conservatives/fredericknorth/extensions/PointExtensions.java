package uk.co.epii.conservatives.fredericknorth.extensions;

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

}
