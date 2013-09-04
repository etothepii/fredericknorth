package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.junit.Before;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.geometry.Edge;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 25/07/2013
 * Time: 13:24
 */
public class EdgeExtensionsTest {

    private Edge edge;

    @Before
    public void setUp() {
        edge = new Edge(null, new Point(4,1), new Point(1, 4));
    }

    @Test
    public void getNearestPointTest1() {
        NearestPoint result = edge.getNearestPoint(new Point2D.Float(3f, 3f));
        NearestPoint expected = new NearestPoint(null, new Point2D.Float(2.5f, 2.5f), 0.5, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

    @Test
    public void getNearestPointTest2() {
        NearestPoint result = edge.getNearestPoint(new Point2D.Float(20f, 10f));
        NearestPoint expected = new NearestPoint(null, new Point2D.Float(4f, 1f), 337, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

    @Test
    public void getNearestPointTest3() {
        NearestPoint result = edge.getNearestPoint(new Point2D.Float(-30f, -10f));
        NearestPoint expected = new NearestPoint(null, new Point2D.Float(1f, 4f), 1157, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

    @Test
    public void getNearestPointTest4() {
        NearestPoint result = edge.getNearestPoint(new Point2D.Float(0f, 2f));
        NearestPoint expected = new NearestPoint(null, new Point2D.Float(1.5f, 3.5f), 4.5, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

}
