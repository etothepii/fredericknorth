package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.Edge;
import uk.co.epii.conservatives.fredericknorth.geometry.Handedness;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.Vertex;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 21:44
 */
public class PolygonExtensionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(PolygonExtensionsTest.class);
    private Polygon polygon;
    private Polygon reversePolygon;

    @Before
    public void setUp() {
        int npoints = 8;
        int[] xpoints = new int[] {1,1,2,2,1,1,3,3};
        int[] ypoints = new int[] {1,2,2,3,3,4,4,1};
        polygon = new Polygon(xpoints, ypoints, npoints);
        xpoints = new int[] {1,3,3,1,1,2,2,1};
        ypoints = new int[] {1,1,4,4,3,3,2,2};
        reversePolygon = new Polygon(xpoints, ypoints, npoints);
    }

    @Test
    public void getConvexPolygonsTest() {
        Polygon[] result = PolygonExtensions.getConvexPolygons(polygon);
        Polygon[] expected = new Polygon[] {
                new Polygon(new int[] {1,1,2,3}, new int[] {1,2,2,1}, 4),
                new Polygon(new int[] {2,2,3,3}, new int[] {2,3,4,1}, 4),
                new Polygon(new int[] {2,1,1,3}, new int[] {3,3,4,4}, 4)
        };
        for (Polygon polygon : result) {
            LOG.debug("{} {} {}", new Object[] {
                    polygon.npoints,
                    Arrays.toString(polygon.xpoints),
                    Arrays.toString(polygon.ypoints)
            });
        }
        assertPolygonArrayEquals(expected, result);
    }

    @Test
    public void getNearestPointTest1() {
        NearestPoint result = PolygonExtensions.getNearestPoint(polygon, new Point2D.Float(2.9f, 2.5f));
        NearestPoint expected = new NearestPoint(new Point2D.Float(3f, 2.5f), 0.01, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

    @Test
    public void getNearestPointTest2() {
        NearestPoint result = PolygonExtensions.getNearestPoint(polygon, new Point2D.Float(4f, 0.9f));
        NearestPoint expected = new NearestPoint(new Point2D.Float(3f, 1f), 1.01, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

    @Test
    public void getPointsTest() {
        Point[] result = PolygonExtensions.getPoints(polygon);
        Point[] expected = new Point[] {
                new Point(1,1),
                new Point(1,2),
                new Point(2,2),
                new Point(2,3),
                new Point(1,3),
                new Point(1,4),
                new Point(3,4),
                new Point(3,1)
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void cutTest() {
        Polygon polygon = new Polygon(new int[] {0, 0, 1, 1, 2, 2}, new int[] {0, 2, 2, 1, 1, 0}, 6);
        Polygon[] results = PolygonExtensions.cut(polygon, 0, 3);
        assertEquals(2, results.length);
        assertEquals(4, results[0].npoints);
        assertEquals(4, results[1].npoints);
        for (Polygon result : results) {
            LOG.debug("{} {} {}", new Object[] {
                    result.npoints,
                    Arrays.toString(result.xpoints),
                    Arrays.toString(result.ypoints)
            });
        }
        Polygon[] expected = new Polygon[] {
                new Polygon(new int[] {0,0,1,1}, new int[] {0,2,2,1}, 4),
                new Polygon(new int[] {1,2,2,0}, new int[] {1,1,0,0}, 4)
        };
        assertPolygonArrayEquals(expected, results);
    }

    private static void assertPolygonArrayEquals(Polygon[] a, Polygon[] b) {
        if (a.length != b.length) throw new RuntimeException("Arrays not of equal length");
        outer: for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (PolygonExtensions.equals(a[i], b[j])) {
                    continue outer;
                }
            }
            throw new RuntimeException(String.format("No match found for x: %s, y: %s",
                    Arrays.toString(a[i].xpoints), Arrays.toString(a[i].ypoints)));
        }

    }

    @Test
    public void cutTest2() {
        Polygon polygon = new Polygon(new int[] {0, 0, 1, 1, 2, 2}, new int[] {0, 2, 2, 1, 1, 0}, 6);
        Polygon[] results = PolygonExtensions.cut(polygon, 3, 0);
        assertEquals(2, results.length);
        assertEquals(4, results[0].npoints);
        assertEquals(4, results[1].npoints);
        for (Polygon result : results) {
            LOG.debug("{} {} {}", new Object[] {
                    result.npoints,
                    Arrays.toString(result.xpoints),
                    Arrays.toString(result.ypoints)
            });
        }
        Polygon[] expected = new Polygon[] {
                new Polygon(new int[] {1,2,2,0}, new int[] {1,1,0,0}, 4),
                new Polygon(new int[] {0,0,1,1}, new int[] {0,2,2,1}, 4),
        };
        assertPolygonArrayEquals(expected, results);
    }

    @Test
    public void getEdgesTest() {
        Edge[] result = PolygonExtensions.getEdges(polygon);
        Edge[] expected = new Edge[] {
                new Edge(new Point(1,1),new Point(1,2)),
                new Edge(new Point(1,2),new Point(2,2)),
                new Edge(new Point(2,2),new Point(2,3)),
                new Edge(new Point(2,3),new Point(1,3)),
                new Edge(new Point(1,3),new Point(1,4)),
                new Edge(new Point(1,4),new Point(3,4)),
                new Edge(new Point(3,4),new Point(3,1)),
                new Edge(new Point(3,1),new Point(1,1))
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void getVerticesTest() {
        Vertex[] result = PolygonExtensions.getVertices(polygon);
        Vertex[] expected = new Vertex[] {
                new Vertex(new Edge(new Point(3,1),new Point(1,1)),new Edge(new Point(1,1),new Point(1,2))),
                new Vertex(new Edge(new Point(1,1),new Point(1,2)),new Edge(new Point(1,2),new Point(2,2))),
                new Vertex(new Edge(new Point(1,2),new Point(2,2)),new Edge(new Point(2,2),new Point(2,3))),
                new Vertex(new Edge(new Point(2,2),new Point(2,3)),new Edge(new Point(2,3),new Point(1,3))),
                new Vertex(new Edge(new Point(2,3),new Point(1,3)),new Edge(new Point(1,3),new Point(1,4))),
                new Vertex(new Edge(new Point(1,3),new Point(1,4)),new Edge(new Point(1,4),new Point(3,4))),
                new Vertex(new Edge(new Point(1,4),new Point(3,4)),new Edge(new Point(3,4),new Point(3,1))),
                new Vertex(new Edge(new Point(3,4),new Point(3,1)),new Edge(new Point(3,1),new Point(1,1)))
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void getInsideTest() {
        assertEquals(Handedness.RIGHT, PolygonExtensions.getInside(polygon));
        assertEquals(Handedness.LEFT, PolygonExtensions.getInside(reversePolygon));
    }

}
