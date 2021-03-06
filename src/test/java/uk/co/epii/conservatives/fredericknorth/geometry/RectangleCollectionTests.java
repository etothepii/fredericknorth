package uk.co.epii.conservatives.fredericknorth.geometry;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.geometry.RectangleCollection;

import java.awt.*;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 03/09/2013
 * Time: 23:53
 */
public class RectangleCollectionTests {

    @Test
    public void containsTest() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 30, 30, 30));
        rectangleCollection.add(new Rectangle(20, 50, 20, 20));
        rectangleCollection.add(new Rectangle(40, 60, 20, 20));
        assertTrue(rectangleCollection.contains(new Rectangle(30, 40, 20, 20)));
    }

    @Test
    public void addTest1() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        assertTrue(!rectangleCollection.isEmpty());
    }

    @Test
    public void addTest2() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 20, 30, 30));
        assertEquals(1, rectangleCollection.size());
    }

    @Test
    public void addTest3() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 20, 30, 30));
        rectangleCollection.add(new Rectangle(10, 50, 30, 30));
        assertEquals(2, rectangleCollection.size());
        rectangleCollection.add(new Rectangle(40, 50, 30, 30));
        assertEquals(1, rectangleCollection.size());
    }

    @Test
    public void addTest4() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 20, 30, 30));
        rectangleCollection.add(new Rectangle(10, 50, 30, 30));
        rectangleCollection.add(new Rectangle(40, 50, 30, 30));
        rectangleCollection.add(new Rectangle(10, 20, 60, 60));
        assertEquals(1, rectangleCollection.size());
    }

    @Test
    public void addTest5() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 20, 30, 30));
        rectangleCollection.add(new Rectangle(10, 50, 30, 30));
        rectangleCollection.add(new Rectangle(40, 50, 30, 30));
        rectangleCollection.add(new Rectangle(9, 19, 62, 62));
        assertEquals(1, rectangleCollection.size());
    }

    @Test
    public void addTest6() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 20, 30, 30));
        rectangleCollection.add(new Rectangle(10, 50, 30, 30));
        rectangleCollection.add(new Rectangle(40, 50, 30, 30));
        rectangleCollection.add(new Rectangle(11, 21, 58, 58));
        assertEquals(1, rectangleCollection.size());
    }

    @Test
    public void removeTest() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 30, 30));
        rectangleCollection.add(new Rectangle(40, 20, 30, 30));
        rectangleCollection.add(new Rectangle(10, 50, 30, 30));
        rectangleCollection.add(new Rectangle(40, 50, 30, 30));
        assertEquals(1, rectangleCollection.size());
        rectangleCollection.remove(new Rectangle(30, 40, 20, 20));
        assertEquals(3200, rectangleCollection.getCoverage());
        assertEquals(4, rectangleCollection.size());
    }

    @Test
    public void containsAllTest1() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 60, 60));
        assertTrue(rectangleCollection.containsAll(Arrays.asList(
                new Rectangle(30, 20, 10, 20),
                new Rectangle(30, 40, 10, 40)
        )));
    }

    @Test
    public void containsAllTest2() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 60, 60));
        assertTrue(!rectangleCollection.containsAll(Arrays.asList(
                new Rectangle(30, 20, 10, 20),
                new Rectangle(30, 40, 10, 41)
        )));
    }

    @Test
    public void addAllTest() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.addAll(Arrays.asList(
                new Rectangle(10, 20, 30, 30), new Rectangle(40, 20, 30, 30),
                new Rectangle(10, 50, 30, 30), new Rectangle(40, 50, 30, 30)));
        assertEquals(1, rectangleCollection.size());
        assertTrue(rectangleCollection.contains(new Rectangle(10, 20, 60, 60)));
    }

    @Test
    public void removeAllTest() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 60, 60));
        rectangleCollection.removeAll(Arrays.asList(
                new Rectangle(30, 20, 10, 20),
                new Rectangle(30, 40, 10, 40)
        ));
        assertEquals(2, rectangleCollection.size());
    }

    @Test
    public void retainAll() {
        RectangleCollection rectangleCollection = new RectangleCollection();
        rectangleCollection.add(new Rectangle(10, 20, 60, 60));
        rectangleCollection.retainAll(Arrays.asList(
                new Rectangle(30, 20, 10, 20),
                new Rectangle(30, 40, 10, 40)
        ));
        assertEquals(1, rectangleCollection.size());
        assertEquals(600, rectangleCollection.getCoverage());
    }
}
