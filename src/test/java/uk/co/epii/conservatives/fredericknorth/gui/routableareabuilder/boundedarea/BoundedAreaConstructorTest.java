package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 01/08/2013
 * Time: 20:42
 */
public class BoundedAreaConstructorTest {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaConstructorTest.class);

    private BoundedArea boundedArea;

    @Before
    public void setUp() {
        int npoints = 8;
        int[] xpoints = {2,6,6,2,2,4,4,2};
        int[] ypoints = {2,2,8,8,6,6,4,4};
        boundedArea = new DummyBoundedArea(null, "Dummy", new Polygon(xpoints, ypoints, npoints));
    }

    @Test
    public void addPointsBetweenTest() {
        BoundedAreaConstructor constructor = new BoundedAreaConstructor(null, null, "Constructor");
        constructor.setCurrent(new Point(2, 3), new BoundedArea[] {boundedArea});
        constructor.addCurrent();
        constructor.setCurrent(new Point(2, 7), new BoundedArea[] {boundedArea});
        constructor.addCurrent();
        constructor.setCurrent(new Point(0,7), new BoundedArea[0]);
        constructor.addCurrent();
        constructor.setCurrent(new Point(0,3), new BoundedArea[0]);
        constructor.addCurrent();
        Polygon polygon = constructor.getAreas()[0];
        for (int i = 0; i < polygon.npoints; i++) {
            LOG.debug("({}, {})", new Object[] {polygon.xpoints[i], polygon.ypoints[i]});
        }
        assertEquals(new Point(2,3), new Point(polygon.xpoints[0], polygon.ypoints[0]));
        assertEquals(new Point(2,4), new Point(polygon.xpoints[1], polygon.ypoints[1]));
        assertEquals(new Point(4,4), new Point(polygon.xpoints[2], polygon.ypoints[2]));
        assertEquals(new Point(4,6), new Point(polygon.xpoints[3], polygon.ypoints[3]));
        assertEquals(new Point(2,6), new Point(polygon.xpoints[4], polygon.ypoints[4]));
        assertEquals(new Point(2,7), new Point(polygon.xpoints[5], polygon.ypoints[5]));
        assertEquals(new Point(0,7), new Point(polygon.xpoints[6], polygon.ypoints[6]));
        assertEquals(new Point(0,3), new Point(polygon.xpoints[7], polygon.ypoints[7]));
    }

    @Test
    public void addPointsBetweenGetDrawnTest1() {
        BoundedAreaConstructor constructor = new BoundedAreaConstructor(null, null, "Constructor");
        constructor.setCurrent(new Point(2, 3), new BoundedArea[]{boundedArea});
        constructor.addCurrent();
        constructor.setCurrent(new Point(2, 7), new BoundedArea[]{boundedArea});
        constructor.addCurrent();
        constructor.setCurrent(new Point(0, 7), new BoundedArea[0]);
        constructor.addCurrent();
        constructor.setCurrent(new Point(0, 3), new BoundedArea[0]);
        Polygon polygon = PolygonExtensions.construct(constructor.getPointsToDraw());
        for (int i = 0; i < polygon.npoints; i++) {
            LOG.debug("({}, {})", new Object[] {polygon.xpoints[i], polygon.ypoints[i]});
        }
        assertEquals(new Point(2,3), new Point(polygon.xpoints[0], polygon.ypoints[0]));
        assertEquals(new Point(2,4), new Point(polygon.xpoints[1], polygon.ypoints[1]));
        assertEquals(new Point(4,4), new Point(polygon.xpoints[2], polygon.ypoints[2]));
        assertEquals(new Point(4,6), new Point(polygon.xpoints[3], polygon.ypoints[3]));
        assertEquals(new Point(2,6), new Point(polygon.xpoints[4], polygon.ypoints[4]));
        assertEquals(new Point(2,7), new Point(polygon.xpoints[5], polygon.ypoints[5]));
        assertEquals(new Point(0,7), new Point(polygon.xpoints[6], polygon.ypoints[6]));
        assertEquals(new Point(0,3), new Point(polygon.xpoints[7], polygon.ypoints[7]));
    }

    @Test
    public void addPointsBetweenGetDrawnTest2() {
        BoundedAreaConstructor constructor = new BoundedAreaConstructor(null, null, "Constructor");
        constructor.setCurrent(new Point(2, 7), new BoundedArea[]{boundedArea});
        constructor.addCurrent();
        constructor.setCurrent(new Point(2, 3), new BoundedArea[]{boundedArea});
        Polygon polygon = PolygonExtensions.construct(constructor.getPointsToDraw());
        for (int i = 0; i < polygon.npoints; i++) {
            LOG.debug("({}, {})", new Object[] {polygon.xpoints[i], polygon.ypoints[i]});
        }
        assertEquals(new Point(2,7), new Point(polygon.xpoints[0], polygon.ypoints[0]));
        assertEquals(new Point(2,6), new Point(polygon.xpoints[1], polygon.ypoints[1]));
        assertEquals(new Point(4,6), new Point(polygon.xpoints[2], polygon.ypoints[2]));
        assertEquals(new Point(4,4), new Point(polygon.xpoints[3], polygon.ypoints[3]));
        assertEquals(new Point(2,4), new Point(polygon.xpoints[4], polygon.ypoints[4]));
        assertEquals(new Point(2,3), new Point(polygon.xpoints[5], polygon.ypoints[5]));
    }

}
