package uk.co.epii.conservatives.fredericknorth.maps;


import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 11:08
 */
public class MapViewGeneratorTest {

    @Test
    public void getRectanglesToClearTest1() {
        Set<Rectangle> result =
                new HashSet<Rectangle>(MapViewGeneratorImpl.getRectanglesToClear(
                        new Rectangle(5, 10, 100, 150),
                        new Rectangle(10, 20, 30, 40)));
        Set<Rectangle> expected = buildExpectedList(
                new Rectangle(5, 10, 5, 150),
                new Rectangle(5, 10, 100, 10),
                new Rectangle(5, 60, 100, 100),
                new Rectangle(40, 10, 65, 150)
        );
        assertEquals(expected, result);
    }

    @Test
    public void getRectanglesToClearTest2() {
        Collection<Rectangle> result =
                new HashSet<Rectangle>(MapViewGeneratorImpl.getRectanglesToClear(
                        new Rectangle(5, 10, 100, 150),
                        new Rectangle(5, 10, 30, 40)));
        Collection<Rectangle> expected = buildExpectedList(
                new Rectangle(35, 10, 70, 150),
                new Rectangle(5, 50, 100, 110)
        );
        assertEquals(expected, result);
    }

    @Test
    public void getRectanglesToClearTest3() {
        Set<Rectangle> result =
                new HashSet<Rectangle>(MapViewGeneratorImpl.getRectanglesToClear(
                        new Rectangle(5, 10, 100, 150),
                        new Rectangle(90, 5, 30, 40)));
        Set<Rectangle> expected = buildExpectedList(
                new Rectangle(5, 10, 85, 150),
                new Rectangle(5, 45, 100, 115)
        );
        assertEquals(expected, result);
    }

    @Test
    public void getRectanglesToClearTest4() {
        Set<Rectangle> result =
                new HashSet<Rectangle>(MapViewGeneratorImpl.getRectanglesToClear(
                        new Rectangle(5, 10, 100, 150),
                        new Rectangle(0, 90, 150, 80)));
        Set<Rectangle> expected = buildExpectedList(
                new Rectangle(5, 10, 100, 80)
        );
        assertEquals(expected, result);
    }

    @Test
    public void scaleToFitRectangleTest() {
        MapImage mapImage = new MapImageImpl(
                new BufferedImage(100, 150, BufferedImage.TYPE_INT_ARGB),
                new Point(20, 30), OSMapType.STREET_VIEW);
        EnumMap<OSMapType, MapImage> mapCache = new EnumMap<OSMapType, MapImage>(OSMapType.class);
        mapCache.put(OSMapType.MINI, mapImage);
        MapViewGenerator mapViewGenerator = new MapViewGeneratorImpl(mapCache, null, null);
        mapViewGenerator.setViewPortSize(new Dimension(20, 30), NullProgressTracker.NULL, null);
        mapViewGenerator.scaleToFitRectangle(new Rectangle(40, -100, 30, 20), NullProgressTracker.NULL, null);
        assertEquals("Scale", mapViewGenerator.getScale(), 0.6666666666666, 0.000001);
        assertEquals("Center", mapViewGenerator.getGeoCenter(), new Point(55, -90));
    }

    private Set<Rectangle> buildExpectedList(Rectangle... rectangles) {
        Set<Rectangle> list = new HashSet<Rectangle>(4);
        for (Rectangle rectangle : rectangles) {
            list.add(rectangle);
        }
        return list;
    }

}
