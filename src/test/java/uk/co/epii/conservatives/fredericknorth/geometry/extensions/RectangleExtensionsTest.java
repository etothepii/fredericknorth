package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 25/08/2013
 * Time: 23:11
 */
public class RectangleExtensionsTest {

    @Test
    public void getCenterTest1() {
        Rectangle r = new Rectangle(100, 200, 300, 400);
        Point result = RectangleExtensions.getCenter(r);
        Point expected = new Point(250, 400);
        assertEquals(expected, result);
    }

    @Test
    public void getCenterTest2() {
        Rectangle r = new Rectangle(-100, -200, 300, 400);
        Point result = RectangleExtensions.getCenter(r);
        Point expected = new Point(50, 0);
        assertEquals(expected, result);
    }

    @Test
    public void getCenterTest3() {
        Rectangle r = new Rectangle(-100, 200, 300, 400);
        Point result = RectangleExtensions.getCenter(r);
        Point expected = new Point(50, 400);
        assertEquals(expected, result);
    }

    @Test
    public void getCenterTest4() {
        Rectangle r = new Rectangle(100, -200, 300, 400);
        Point result = RectangleExtensions.getCenter(r);
        Point expected = new Point(250, 0);
        assertEquals(expected, result);
    }

    @Test
    public void getScaleInstanceTest1() {
        Rectangle r = new Rectangle(100, 200, 300, 400);
        Point point = new Point(0, 0);
        double scale = 0.5;
        Rectangle result = RectangleExtensions.getScaleInstance(r, point, scale);
        Rectangle expected = new Rectangle(50, 100, 150, 200);
        assertEquals(expected, result);
    }

    @Test
    public void getScaleInstanceTest2() {
        Rectangle r = new Rectangle(100, 200, 300, 400);
        Point point = new Point(100, 200);
        double scale = 0.5;
        Rectangle result = RectangleExtensions.getScaleInstance(r, point, scale);
        Rectangle expected = new Rectangle(100, 200, 150, 200);
        assertEquals(expected, result);
    }

    @Test
    public void getScaleInstanceTest3() {
        Rectangle r = new Rectangle(100, 200, 300, 400);
        Point point = new Point(200, 300);
        double scale = 0.5;
        Rectangle result = RectangleExtensions.getScaleInstance(r, point, scale);
        Rectangle expected = new Rectangle(150, 250, 150, 200);
        assertEquals(expected, result);
    }

    @Test
    public void getScaleInstanceTest4() {
        Rectangle r = new Rectangle(100, 200, 300, 400);
        Point point = new Point(300, 600);
        double scale = 2;
        Rectangle result = RectangleExtensions.getScaleInstance(r, point, scale);
        Rectangle expected = new Rectangle(-100, -200, 600, 800);
        assertEquals(expected, result);
    }

    @Test
    public void getScaleInstanceTest5() {
        Rectangle r = new Rectangle(100, 200, 300, 400);
        Point point = new Point(100, 200);
        double scale = 3;
        Rectangle result = RectangleExtensions.getScaleInstance(r, point, scale);
        Rectangle expected = new Rectangle(100, 200, 900, 1200);
        assertEquals(expected, result);
    }
}
