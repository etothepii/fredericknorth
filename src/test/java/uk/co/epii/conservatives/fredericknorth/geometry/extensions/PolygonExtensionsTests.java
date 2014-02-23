package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 21:44
 */
public class PolygonExtensionsTests {

    private static final Logger LOG = LoggerFactory.getLogger(PolygonExtensionsTests.class);
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
        NearestPoint expected = new NearestPoint(null, new Point2D.Float(3f, 2.5f), 0.01, null);
        assertEquals(expected.point.getX(), result.point.getX(), 0.000001);
        assertEquals(expected.point.getY(), result.point.getY(), 0.000001);
        assertEquals(expected.dSquared, result.dSquared, 0.000001);
    }

    @Test
    public void getNearestPointTest2() {
        NearestPoint result = PolygonExtensions.getNearestPoint(polygon, new Point2D.Float(4f, 0.9f));
        NearestPoint expected = new NearestPoint(null, new Point2D.Float(3f, 1f), 1.01, null);
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
                new Edge(polygon, new Point(1,1),new Point(1,2)),
                new Edge(polygon, new Point(1,2),new Point(2,2)),
                new Edge(polygon, new Point(2,2),new Point(2,3)),
                new Edge(polygon, new Point(2,3),new Point(1,3)),
                new Edge(polygon, new Point(1,3),new Point(1,4)),
                new Edge(polygon, new Point(1,4),new Point(3,4)),
                new Edge(polygon, new Point(3,4),new Point(3,1)),
                new Edge(polygon, new Point(3,1),new Point(1,1))
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void getVerticesTest() {
        Vertex[] result = PolygonExtensions.getVertices(polygon);
        Vertex[] expected = new Vertex[] {
                new Vertex(new Edge(polygon, new Point(3,1),new Point(1,1)),new Edge(polygon, new Point(1,1),new Point(1,2))),
                new Vertex(new Edge(polygon, new Point(1,1),new Point(1,2)),new Edge(polygon, new Point(1,2),new Point(2,2))),
                new Vertex(new Edge(polygon, new Point(1,2),new Point(2,2)),new Edge(polygon, new Point(2,2),new Point(2,3))),
                new Vertex(new Edge(polygon, new Point(2,2),new Point(2,3)),new Edge(polygon, new Point(2,3),new Point(1,3))),
                new Vertex(new Edge(polygon, new Point(2,3),new Point(1,3)),new Edge(polygon, new Point(1,3),new Point(1,4))),
                new Vertex(new Edge(polygon, new Point(1,3),new Point(1,4)),new Edge(polygon, new Point(1,4),new Point(3,4))),
                new Vertex(new Edge(polygon, new Point(1,4),new Point(3,4)),new Edge(polygon, new Point(3,4),new Point(3,1))),
                new Vertex(new Edge(polygon, new Point(3,4),new Point(3,1)),new Edge(polygon, new Point(3,1),new Point(1,1)))
        };
        assertArrayEquals(expected, result);
    }

    @Test
    public void getInsideTest() {
        assertEquals(Handedness.RIGHT, PolygonExtensions.getInside(polygon));
        assertEquals(Handedness.LEFT, PolygonExtensions.getInside(reversePolygon));
    }

    @Test
    public void intersectsTest1() {
        Polygon polygon = new Polygon(new int[] {6}, new int[] {6}, 1);
        boolean result = PolygonExtensions.intersects(new Polygon[]{polygon}, new Rectangle(new Dimension(10, 10)));
        boolean expected = true;
        assertEquals(expected, result);
    }

    @Test
    public void clipTest1() {
        Rectangle clip = new Rectangle(5, 10, 20, 30);
        Polygon polygon = new Polygon(new int[] {0,10,15,20,30,20,15,10}, new int[] {25,15,-10,15,25,35,60,35}, 8);
        Point[] expected = PolygonExtensions.toPointArray(
                new Polygon(new int[] {19,20,25,25,20,19,11,10,5,5,10,11},
                        new int[] {10,15,20,30,35,40,40,35,30,20,15,10},
                        12));
        Point[] result = PolygonExtensions.toPointArray((Polygon)(PolygonExtensions.clip(polygon, clip)[0]));
        LOG.debug("expected: {}", Arrays.toString(expected));
        LOG.debug("result: {}", Arrays.toString(result));
        assertArrayEquals(expected, result);
    }

    @Test
    public void clipTest3() {
        Rectangle clip = new Rectangle(50, 100, 150, 200);
        Polygon polygon = new Polygon(new int[] {0, 0, 60, 60, 70, 70, 80, 80}, new int[] {0, 400, 400, 10, 10, 110, 110, 0}, 8);
        Point[] expected = PolygonExtensions.toPointArray(
                new Polygon(new int[] {60,60,50,50},
                        new int[] {300,100,100,300}, 4));
        Shape[] result = PolygonExtensions.clip(polygon, clip);
        Point[] test = PolygonExtensions.toPointArray((Polygon)(result[0]));
        LOG.debug("expected: {}", Arrays.toString(expected));
        LOG.debug("result: {}", Arrays.toString(test));
        assertArrayEquals(expected, test);
        expected = PolygonExtensions.toPointArray(
                new Polygon(new int[] {70,70,80,80},
                        new int[] {100,110,110,100}, 4));
        test = PolygonExtensions.toPointArray((Polygon)(result[1]));
        LOG.debug("expected: {}", Arrays.toString(expected));
        LOG.debug("result: {}", Arrays.toString(test));
        assertArrayEquals(expected, test);
    }

    @Test
    public void clipTest2() {
        Rectangle clip = new Rectangle(5, 10, 20, 30);
        Polygon polygon = new Polygon(new int[] {-5, 20, 75}, new int[] {25, 0, 9}, 3);
        Point[] expected = PolygonExtensions.toPointArray(
                new Polygon(new int[] {25,5,5,10,25},
                        new int[] {19,23,15,10,10}, 5));
        Point[] result = PolygonExtensions.toPointArray((Polygon)(PolygonExtensions.clip(polygon, clip)[0]));
        LOG.debug("expected: {}", Arrays.toString(expected));
        LOG.debug("result: {}", Arrays.toString(result));
        assertArrayEquals(expected, result);
    }

    @Test
    public void getClippedSegmentsTest1() {
        Rectangle clip = new Rectangle(5, 10, 20, 30);
        Polygon polygon = new Polygon(new int[] {0,10,15,20,30,20,15,10}, new int[] {25,15,-10,15,25,35,60,35}, 8);
        ClippedSegment[] expected = new ClippedSegment[] {
                createClippedSegment(new Point[] {new Point(5, 30), new Point(0, 25), new Point(5, 20)}, false),
                createClippedSegment(new Point[] {new Point(5, 20), new Point(10, 15), new Point(11, 10)}, true),
                createClippedSegment(new Point[] {new Point(11, 10), new Point(15, -10), new Point(19, 10)}, false),
                createClippedSegment(new Point[] {new Point(19, 10), new Point(20, 15), new Point(25, 20)}, true),
                createClippedSegment(new Point[] {new Point(25, 20), new Point(30, 25), new Point(25, 30)}, false),
                createClippedSegment(new Point[] {new Point(25, 30), new Point(20, 35), new Point(19, 40)}, true),
                createClippedSegment(new Point[] {new Point(19, 40), new Point(15, 60), new Point(11, 40)}, false),
                createClippedSegment(new Point[] {new Point(11, 40), new Point(10, 35), new Point(5, 30)}, true)};
        ClippedSegment[] result = PolygonExtensions.getClippedSegments(polygon, clip).toArray(new ClippedSegment[0]);
        assertArrayEquals(expected, result);

    }

    private ClippedSegment createClippedSegment(Point[] points, boolean inside) {
        return new ClippedSegment(Arrays.asList(points), inside);
    }

    @Test
    public void getClippedSegmentsTest2() {
        Rectangle clip = new Rectangle(5, 10, 20, 30);
        Polygon polygon = new Polygon(new int[] {-5, 20, 75}, new int[] {25, 0, 9}, 3);
        ClippedSegment[] expected = new ClippedSegment[] {
                createClippedSegment(new Point[]{new Point(5,23), new Point(-5,25), new Point(5,15)},false),
                createClippedSegment(new Point[]{new Point(5,15), new Point(10,10)},true),
                createClippedSegment(new Point[]{new Point(10,10), new Point(20,0), new Point(75,9), new Point(25,19)},false),
                createClippedSegment(new Point[]{new Point(25,19), new Point(5,23)},true)
        };
        ClippedSegment[] result = PolygonExtensions.getClippedSegments(polygon, clip).toArray(new ClippedSegment[0]);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getClippedSegmentsTest3() {
        Rectangle clip = new Rectangle(5, 10, 20, 30);
        Polygon polygon = new Polygon(new int[] {0,10,15,20,30,20,15,11,10,10}, new int[] {25,15,-10,15,25,35,60,40,40,35}, 10);
        ClippedSegment[] expected = new ClippedSegment[] {
                createClippedSegment(new Point[] {new Point(5, 30), new Point(0, 25), new Point(5, 20)}, false),
                createClippedSegment(new Point[] {new Point(5, 20), new Point(10, 15), new Point(11, 10)}, true),
                createClippedSegment(new Point[] {new Point(11, 10), new Point(15, -10), new Point(19, 10)}, false),
                createClippedSegment(new Point[] {new Point(19, 10), new Point(20, 15), new Point(25, 20)}, true),
                createClippedSegment(new Point[] {new Point(25, 20), new Point(30, 25), new Point(25, 30)}, false),
                createClippedSegment(new Point[] {new Point(25, 30), new Point(20, 35), new Point(19, 40)}, true),
                createClippedSegment(new Point[] {new Point(19, 40), new Point(15, 60), new Point(11, 40)}, false),
                createClippedSegment(new Point[] {new Point(11, 40), new Point(10, 40), new Point(10, 35), new Point(5, 30)}, true)};
        ClippedSegment[] result = PolygonExtensions.getClippedSegments(polygon, clip).toArray(new ClippedSegment[0]);
        assertArrayEquals(expected, result);
    }

    @Test
    public void getClippedSegmentsTest4() {
        Rectangle clip = new Rectangle(5, 10, 20, 30);
        Polygon polygon = new Polygon(new int[] {0,10,15,20,30,20,15,11,12,10}, new int[] {25,15,-10,15,25,35,60,40,40,35}, 10);
        ClippedSegment[] expected = new ClippedSegment[] {
                createClippedSegment(new Point[] {new Point(5, 30), new Point(0, 25), new Point(5, 20)}, false),
                createClippedSegment(new Point[] {new Point(5, 20), new Point(10, 15), new Point(11, 10)}, true),
                createClippedSegment(new Point[] {new Point(11, 10), new Point(15, -10), new Point(19, 10)}, false),
                createClippedSegment(new Point[] {new Point(19, 10), new Point(20, 15), new Point(25, 20)}, true),
                createClippedSegment(new Point[] {new Point(25, 20), new Point(30, 25), new Point(25, 30)}, false),
                createClippedSegment(new Point[] {new Point(25, 30), new Point(20, 35), new Point(19, 40)}, true),
                createClippedSegment(new Point[] {new Point(19, 40), new Point(15, 60), new Point(11, 40)}, false),
                createClippedSegment(new Point[] {new Point(11, 40), new Point(12, 40), new Point(10, 35), new Point(5, 30)}, true)};
        ClippedSegment[] result = PolygonExtensions.getClippedSegments(polygon, clip).toArray(new ClippedSegment[0]);
        assertArrayEquals(expected, result);
    }

}
