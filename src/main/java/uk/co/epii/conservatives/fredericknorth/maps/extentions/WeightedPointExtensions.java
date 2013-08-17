package uk.co.epii.conservatives.fredericknorth.maps.extentions;

import uk.co.epii.conservatives.fredericknorth.maps.WeightedPoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 12/08/2013
 * Time: 21:53
 */
public class WeightedPointExtensions {

    public static Point.Double getMedian(Collection<? extends WeightedPoint> weightedPoints) {
        Point2D.Double[] xpoints = new Point2D.Double[weightedPoints.size()];
        Point2D.Double[] ypoints = new Point2D.Double[weightedPoints.size()];
        double total = 0d;
        int index = 0;
        for (WeightedPoint weightedPoint : weightedPoints) {
            xpoints[index] = new Point2D.Double(weightedPoint.getX(), weightedPoint.getWeight());
            ypoints[index] = new Point2D.Double(weightedPoint.getY(), weightedPoint.getWeight());
            total += weightedPoint.getWeight();
            index++;
        }
        Arrays.sort(xpoints, new Comparator<Point2D.Double>() {
            @Override
            public int compare(Point2D.Double o1, Point2D.Double o2) {
                return Double.compare(o1.getX(), o2.getX());
            }
        });
        Arrays.sort(ypoints, new Comparator<Point2D.Double>() {
            @Override
            public int compare(Point2D.Double o1, Point2D.Double o2) {
                return Double.compare(o1.getX(), o2.getX());
            }
        });
        double target = total / 2d;
        double epsilon = target / 1000000d;
        double currentX = 0d;
        double currentY = 0d;
        boolean xDone = false;
        boolean yDone = false;
        double xVal = Double.NaN;
        double yVal = Double.NaN;
        for (int i = 0; i < weightedPoints.size(); i++) {
            if (xDone && yDone) break;
            if (!xDone) {
                currentX += xpoints[i].getY();
                double delta = currentX - target;
                if (Math.abs(delta) < epsilon) {
                    xVal = (xpoints[i].getX() + xpoints[i + 1].getX()) / 2d;
                    xDone = true;
                }
                else if (delta > 0) {
                    xVal = xpoints[i].getX();
                    xDone = true;
                }
            }
            if (!yDone) {
                currentY += ypoints[i].getY();
                double delta = currentY - target;
                if (Math.abs(delta) < epsilon) {
                    yVal = (ypoints[i].getX() + ypoints[i + 1].getX()) / 2d;
                    yDone = true;
                }
                else if (delta > 0) {
                    yVal = ypoints[i].getX();
                    yDone = true;
                }
            }
        }
        return new Point2D.Double(xVal, yVal);
    }


}
