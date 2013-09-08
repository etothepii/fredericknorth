package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.geometry.RectangleIntersection;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: James Robinson
 * Date: 25/08/2013
 * Time: 23:11
 */
public class RectangleExtensionsTest {

    private static HashMap<Integer, Point> compassPoints;
    private static Rectangle originRectangle;

    @BeforeClass
    public static void setUpClass() {
        compassPoints = new HashMap<Integer, Point>(4);
        compassPoints.put(SwingConstants.SOUTH, new Point(0, 10));
        compassPoints.put(SwingConstants.EAST, new Point(10, 0));
        compassPoints.put(SwingConstants.NORTH, new Point(0, -10));
        compassPoints.put(SwingConstants.WEST, new Point(-10, 0));
        originRectangle = new Rectangle(-8, -8, 16, 16);
    }

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


    @Test
    public void getSurroundingTest1() {
        Rectangle base = new Rectangle(5, 10, 100, 150);
        Rectangle fillAround = new Rectangle(10, 20, 30, 40);
        Set<Rectangle> surrounding =
                new HashSet<Rectangle>(RectangleExtensions.getSurrounding(
                        base, fillAround));
        checkResult(base, fillAround, surrounding);
    }

    @Test
    public void getSurroundingTest2() {
        Rectangle base = new Rectangle(5, 10, 100, 150);
        Rectangle fillAround = new Rectangle(5, 10, 30, 40);
        Set<Rectangle> surrounding =
                new HashSet<Rectangle>(RectangleExtensions.getSurrounding(
                        base, fillAround));
        checkResult(base, fillAround, surrounding);
    }

    @Test
    public void getSurroundingTest3() {
        Rectangle base = new Rectangle(5, 10, 100, 150);
        Rectangle fillAround = new Rectangle(90, 5, 30, 40);
        Set<Rectangle> surrounding =
                new HashSet<Rectangle>(RectangleExtensions.getSurrounding(
                        base, fillAround));
        checkResult(base, fillAround, surrounding);
    }

    @Test
    public void getSurroundingTest4() {
        Rectangle base = new Rectangle(5, 10, 100, 150);
        Rectangle fillAround = new Rectangle(0, 90, 150, 80);
        Set<Rectangle> surrounding =
                new HashSet<Rectangle>(RectangleExtensions.getSurrounding(
                        base, fillAround));
        checkResult(base, fillAround, surrounding);
    }

    @Test
    public void getSurroundingTest5() {
        Rectangle base = new Rectangle(5, 10, 100, 150);
        Collection<Rectangle> fillAround = buildExpectedList(
                new Rectangle(30, 20, 10, 80),
                new Rectangle(60, 20, 10, 80));
        Set<Rectangle> surrounding =
                new HashSet<Rectangle>(RectangleExtensions.getSurrounding(
                        base, fillAround));
        checkResult(base, fillAround, surrounding);
    }

    @Test
    public void getSurroundingTest6() {
        Rectangle base = new Rectangle(5, 10, 100, 150);
        Collection<Rectangle> fillAround = buildExpectedList();
        Set<Rectangle> surrounding =
                new HashSet<Rectangle>(RectangleExtensions.getSurrounding(
                        base, fillAround));
        checkResult(base, fillAround, surrounding);
    }

    private Set<Rectangle> buildExpectedList(Rectangle... rectangles) {
        Set<Rectangle> list = new HashSet<Rectangle>(4);
        for (Rectangle rectangle : rectangles) {
            list.add(rectangle);
        }
        return list;
    }

    private void checkResult(Rectangle base, Rectangle fillAround, Collection<Rectangle> surrounding) {
        checkResult(base, Arrays.asList(fillAround), surrounding);
    }

    private void checkResult(Rectangle base, Collection<Rectangle> fillAround, Collection<Rectangle> surrounding) {
        long totalArea = 0;
        ArrayList<Rectangle> all = new ArrayList<Rectangle>(fillAround.size() + surrounding.size());
        for (Rectangle rectangle : fillAround) {
            Rectangle intersection = base.intersection(rectangle);
            totalArea += intersection.width * (long)intersection.height;
            all.add(rectangle);
        }
        for (Rectangle rectangle : surrounding) {
            assertTrue(base.toString() + " contains " + rectangle.toString(), base.contains(rectangle));
            totalArea += rectangle.width * (long)rectangle.height;
            all.add(rectangle);
        }
        for (int i = 1; i < all.size(); i++) {
            for (int j = 0; j < i; j++) {
                Rectangle a = all.get(i);
                Rectangle b = all.get(j);
                assertTrue(a.toString() + " intersects " + b.toString(), !a.intersects(b));
            }
        }
        assertEquals("All sum to base area: ", base.width * (long) base.height, totalArea);
    }

    @Test
    public void shouldSwitchRectangleIntersectionsTest1() {
        int[] edges = new int[] {SwingConstants.NORTH, SwingConstants.EAST};
        Point[] points = new Point[] {RectangleExtensionsTest.compassPoints.get(edges[0]), RectangleExtensionsTest.compassPoints.get(edges[1])};
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[1], points[0]));
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[1], points[0]));
    }

    @Test
    public void shouldSwitchRectangleIntersectionsTest2() {
        int[] edges = new int[] {SwingConstants.NORTH, SwingConstants.SOUTH};
        Point[] points = new Point[] {RectangleExtensionsTest.compassPoints.get(edges[0]), RectangleExtensionsTest.compassPoints.get(edges[1])};
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[1], points[0]));
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[1], points[0]));
    }

    @Test
    public void shouldSwitchRectangleIntersectionsTest3() {
        int[] edges = new int[] {SwingConstants.NORTH, SwingConstants.WEST};
        Point[] points = new Point[] {RectangleExtensionsTest.compassPoints.get(edges[0]), RectangleExtensionsTest.compassPoints.get(edges[1])};
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[1], points[0]));
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[1], points[0]));
    }

    @Test
    public void shouldSwitchRectangleIntersectionsTest4() {
        int[] edges = new int[] {SwingConstants.EAST, SwingConstants.SOUTH};
        Point[] points = new Point[] {RectangleExtensionsTest.compassPoints.get(edges[0]), RectangleExtensionsTest.compassPoints.get(edges[1])};
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[1], points[0]));
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[1], points[0]));
    }

    @Test
    public void shouldSwitchRectangleIntersectionsTest5() {
        int[] edges = new int[] {SwingConstants.EAST, SwingConstants.WEST};
        Point[] points = new Point[] {RectangleExtensionsTest.compassPoints.get(edges[0]), RectangleExtensionsTest.compassPoints.get(edges[1])};
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[1], points[0]));
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[1], points[0]));
    }

    @Test
    public void shouldSwitchRectangleIntersectionsTest6() {
        int[] edges = new int[] {SwingConstants.SOUTH, SwingConstants.WEST};
        Point[] points = new Point[] {RectangleExtensionsTest.compassPoints.get(edges[0]), RectangleExtensionsTest.compassPoints.get(edges[1])};
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[1], points[0]));
        assertTrue(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[0], edges[1], points[0], points[1]));
        assertFalse(RectangleExtensions.shouldSwitchRectangleIntersections(edges[1], edges[0], points[1], points[0]));
    }

    @Test
    public void getIntersectionTest1() {
        Rectangle rectangle = new Rectangle(5, 10, 20, 30);
        Point from = new Point(15, 20);
        Point to = new Point(23, 0);
        RectangleIntersection[] expected =
                new RectangleIntersection[] {new RectangleIntersection(new Point(19, 10), SwingConstants.NORTH)};
        RectangleIntersection[] result = RectangleExtensions.getIntersection(rectangle, from, to);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getIntersectionTest2() {
        Rectangle rectangle = new Rectangle(5, 10, 20, 30);
        Point from = new Point(15, 20);
        Point to = new Point(35, 30);
        RectangleIntersection[] expected =
                new RectangleIntersection[] {new RectangleIntersection(new Point(25, 25), SwingConstants.EAST)};
        RectangleIntersection[] result = RectangleExtensions.getIntersection(rectangle, from, to);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getIntersectionTest3() {
        Rectangle rectangle = new Rectangle(5, 10, 20, 30);
        Point from = new Point(15, 20);
        Point to = new Point(9, 60);
        RectangleIntersection[] expected =
                new RectangleIntersection[] {new RectangleIntersection(new Point(12, 40), SwingConstants.SOUTH)};
        RectangleIntersection[] result = RectangleExtensions.getIntersection(rectangle, from, to);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getIntersectionTest4() {
        Rectangle rectangle = new Rectangle(5, 10, 20, 30);
        Point from = new Point(15, 20);
        Point to = new Point(-5, 18);
        RectangleIntersection[] expected =
                new RectangleIntersection[] {new RectangleIntersection(new Point(5, 19), SwingConstants.WEST)};
        RectangleIntersection[] result = RectangleExtensions.getIntersection(rectangle, from, to);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getIntersectionTest5() {
        int[] sides = new int[] {SwingConstants.NORTH, SwingConstants.WEST};
        Point[] points = new Point[] {compassPoints.get(sides[0]), compassPoints.get(sides[1])};
        RectangleIntersection[] expected = new RectangleIntersection[] {
                new RectangleIntersection(new Point(-2, -8), sides[0]),
                new RectangleIntersection(new Point(-8, -2), sides[1])
        };
        RectangleIntersection[] result =
                RectangleExtensions.getIntersection(originRectangle, points[0], points[1]);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getIntersectionTest6() {
        int[] sides = new int[] {SwingConstants.WEST, SwingConstants.NORTH};
        Point[] points = new Point[] {compassPoints.get(sides[0]), compassPoints.get(sides[1])};
        RectangleIntersection[] expected = new RectangleIntersection[] {
                new RectangleIntersection(new Point(-8, -2), sides[0]),
                new RectangleIntersection(new Point(-2, -8), sides[1])
        };
        RectangleIntersection[] result =
                RectangleExtensions.getIntersection(originRectangle, points[0], points[1]);
        assertArrayEquals(expected, result);
    }


}
