package uk.co.epii.conservatives.fredericknorth.extensions;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 23:36
 */
public class Vertex {

    public final Edge a;
    public final Edge b;

    public Vertex(Edge a, Edge b) {
        if (!a.b.equals(b.a)) {
            throw new IllegalArgumentException(
                    "The second point of the first edge must be equal to the first point of the second edge");
        }
        this.a = a;
        this.b = b;
    }

    public Point.Float getCommonPoint() {
        return a.b;
    }

    public Point.Float getNearPoint() {
        return a.a;
    }

    public Point.Float getFarPoint() {
        return b.b;
    }

    public Edge getNearEdge() {
        return a;
    }

    public Edge getFarEdge() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        if (!a.equals(vertex.a)) return false;
        if (!b.equals(vertex.b)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

    public double getLeftAngle() {
        double cross_product_z = getCrossProduct();
        double theta = getNonReflexAngle();
        if (cross_product_z < 0) {
            return Math.PI * 2 - theta;
        }
        else {
            return theta;
        }
    }

    private double getCrossProduct() {
        double x_1 = getNearPoint().x;
        double y_1 = getNearPoint().y;
        double x_2 = getCommonPoint().x;
        double y_2 = getCommonPoint().y;
        double x_3 = getFarPoint().x;
        double y_3 = getFarPoint().y;
        double a_x = x_1 - x_2;
        double a_y = y_1 - y_2;
        double b_x = x_3 - x_2;
        double b_y = y_3 - y_2;
        return b_x * a_y - b_y * a_x;
    }

    public double getNonReflexAngle() {
        double x_1 = getNearPoint().x;
        double y_1 = getNearPoint().y;
        double x_2 = getCommonPoint().x;
        double y_2 = getCommonPoint().y;
        double x_3 = getFarPoint().x;
        double y_3 = getFarPoint().y;
        double a_x = x_1 - x_2;
        double a_y = y_1 - y_2;
        double b_x = x_3 - x_2;
        double b_y = y_3 - y_2;
        double dot_product = a_x * b_x + a_y * b_y;
        double mod_a = Math.sqrt(a_x * a_x + a_y * a_y);
        double mod_b = Math.sqrt(b_x * b_x + b_y * b_y);
        double cos_theta = dot_product / mod_a / mod_b;
        return Math.acos(cos_theta);
    }

    public double getRightAngle() {
        return Math.PI * 2 - getLeftAngle();
    }

    public double getAngle(Handedness handedness) {
        switch (handedness) {
            case LEFT:
                return getLeftAngle();
            case RIGHT:T:
            return getRightAngle();
        }
        throw new IllegalArgumentException(String.format("The handedness, %s, provided is not supported", handedness));
    }
}
