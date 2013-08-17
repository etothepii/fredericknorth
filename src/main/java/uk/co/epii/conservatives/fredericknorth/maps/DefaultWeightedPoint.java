package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 12/08/2013
 * Time: 21:47
 */
public class DefaultWeightedPoint implements WeightedPoint {

    private final Point point;
    private final double weight;

    public DefaultWeightedPoint(Point point, double weight) {
        this.point = point;
        this.weight = weight;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public Point getPoint() {
        return point;
    }

    public double getWeight() {
        return weight;
    }
}
