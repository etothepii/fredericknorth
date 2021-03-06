package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 21:26
 */
public class PolygonExtensions {

    private static final Logger LOG = LoggerFactory.getLogger(PolygonExtensions.class);

    public static boolean isConvex(Polygon polygon) {
        throw new UnsupportedOperationException("This operation is not yet supported");
    }

    public static Point2D.Float getCentreOfGravity(Polygon polygon) {
        return getCentreOfGravity(polygon, getArea(polygon));
    }

    private static Point2D.Float getCentreOfGravity(Polygon polygon, double area) {
        double x = 0;
        double y = 0;
        for (int i = 0; i < polygon.npoints; i++) {
            double x_1 = polygon.xpoints[i];
            double y_1 = polygon.ypoints[i];
            double x_2 = polygon.xpoints[i == polygon.npoints - 1 ? 0 : i + 1];
            double y_2 = polygon.ypoints[i == polygon.npoints - 1 ? 0 : i + 1];
            x += (x_1 + x_2) * (x_1 * y_2 - x_2 * y_1);
            y += (y_1 + y_2) * (x_1 * y_2 - x_2 * y_1);
        }
        return new Point2D.Float((float)(x / (6 * area)), (float)(y / (6 * area)));
    }

    public static double getArea(Polygon polygon) {
        double a = 0;
        for (int i = 0; i < polygon.npoints; i++) {
            double x_1 = polygon.xpoints[i];
            double y_1 = polygon.ypoints[i];
            double x_2 = polygon.xpoints[i == polygon.npoints - 1 ? 0 : i + 1];
            double y_2 = polygon.ypoints[i == polygon.npoints - 1 ? 0 : i + 1];
            a += x_1 * y_2 - x_2 * y_1;
        }
        return a / 2;
    }

    public static boolean isClockwise(Polygon polygon) {
        double a = 0;
        for (int i = 0; i < polygon.npoints; i++) {
            double x_1 = polygon.xpoints[i];
            double y_1 = polygon.ypoints[i];
            double x_2 = polygon.xpoints[i == polygon.npoints - 1 ? 0 : i + 1];
            double y_2 = polygon.ypoints[i == polygon.npoints - 1 ? 0 : i + 1];
            a += (x_2 - x_1) * (y_1 + y_2);
        }
        return a < 0; //Compared to standard access its reflected through x=0
    }

    public static Polygon[] getConvexPolygons(Polygon polygon) {
        ArrayList<Vertex[]> untestedPolygons = new ArrayList<Vertex[]>(polygon.npoints);
        ArrayList<Polygon> convexPolygons = new ArrayList<Polygon>(polygon.npoints);
        untestedPolygons.add(getVertices(polygon));
        Handedness inside = getInside(untestedPolygons.get(0));
        while (!untestedPolygons.isEmpty()) {
            Vertex[] vertices = untestedPolygons.remove(untestedPolygons.size() - 1);
            int reflex = -1;
            for (int i = 0; i < vertices.length; i++) {
                if (vertices[i].getAngle(inside) > Math.PI) {
                    reflex = i;
                    break;
                }
            }
            if (reflex < 0) {
                convexPolygons.add(fromVertices(vertices));
                continue;
            }
            int closestAlternate = -1;
            double leastDSquared = Double.MAX_VALUE;
            for (int i = 2; i < vertices.length - 1; i++) {
                int check = (i + reflex) % vertices.length;
                if (new Vertex(
                        vertices[reflex].getNearEdge(),
                        new Edge(polygon, vertices[reflex].getCommonPoint(), vertices[check].getCommonPoint())
                ).getAngle(inside) > Math.PI) {
                    continue;
                }
                double dSquared = VertexExtensions.dSquared(vertices[reflex], vertices[check]);
                if (dSquared < leastDSquared) {
                    leastDSquared = dSquared;
                    closestAlternate = check;
                }
            }
            Vertex[][] polygons = cut(polygon, vertices, reflex, closestAlternate);
            for (int i = 0; i < 2; i++) {
                if (polygons[i].length > 3) {
                    untestedPolygons.add(polygons[i]);
                }
                else if (polygons[i].length == 3) {
                    convexPolygons.add(fromVertices(polygons[i]));
                }
                else {
                    throw new IllegalStateException("Too few vertices");
                }
            }
        }
        return convexPolygons.toArray(new Polygon[convexPolygons.size()]);
    }

    public static NearestPoint getNearestPoint(Polygon polygon, Point2D.Float point) {
        Edge[] edges = getEdges(polygon);
        NearestPoint nearestPoint = null;
        for (Edge edge : edges) {
            NearestPoint nearestPointToEdge = edge.getNearestPoint(point);
            if (nearestPoint == null || nearestPointToEdge.dSquared < nearestPoint.dSquared) {
                nearestPoint = nearestPointToEdge;
            }
        }
        return nearestPoint;
    }

    private static Polygon fromVertices(Vertex[] vertices) {
        int[] xpoints = new int[vertices.length];
        int[] ypoints = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            xpoints[i] = (int)vertices[i].getCommonPoint().x;
            ypoints[i] = (int)vertices[i].getCommonPoint().y;
        }
        return new Polygon(xpoints, ypoints, vertices.length);
    }

    public static Polygon[] cut(Polygon polygon, int newStart, int newStop) {
        Vertex[] vertices = getVertices(polygon);
        Vertex[][] polygons = cut(polygon, vertices, newStart, newStop);
        return new Polygon[] {fromVertices(polygons[0]), fromVertices(polygons[1])};
    }

    public static boolean equals(Polygon a, Polygon b) {
        if (a.npoints != b.npoints) return false;
        int matchupAt = -1;
        for (int i = 0; i < a.npoints; i++) {
            if (a.xpoints[0] == b.xpoints[i] && a.ypoints[0] == b.ypoints[i]) {
                matchupAt = i;
                break;
            }
        }
        if (matchupAt < 0) {
            return false;
        }
        int[] cycledx = cycle(b.xpoints, matchupAt);
        int[] cycledy = cycle(b.ypoints, matchupAt);
        if (Arrays.equals(a.xpoints, cycledx) && Arrays.equals(a.ypoints, cycledy)) {
            return true;
        }
        cycledx = reverse(cycledx);
        cycledy = reverse(cycledy);
        return Arrays.equals(a.xpoints, cycledx) && Arrays.equals(a.ypoints, cycledy);
    }

    private static int[] reverse(int[] values) {
        int[] reversed = new int[values.length];
        int index = 0;
        reversed[index++] = values[0];
        for (; index < values.length; index++) {
            reversed[index] = values[values.length - index];
        }
        return reversed;
    }

    private static int[] cycle(int[] values, int offset) {
        int[] cycled = new int[values.length];
        int index = 0;
        for (int i = offset; i < values.length; i++) {
            cycled[index++] = values[i];
        }
        for (int i = 0; i < offset; i++) {
            cycled[index++] = values[i];
        }
        return cycled;
    }

    private static Vertex[][] cut(Polygon polygon, Vertex[] vertices, int newStart, int newStop) {
        if (newStop < newStart) {
            return cut(polygon, vertices, newStop, newStart);
        }
        Vertex[] polygon_1 = new Vertex[newStop + 1 - newStart];
        for (int i = newStart + 1; i < newStop; i++) {
            polygon_1[i - newStart] = vertices[i];
        }
        polygon_1[0] =
                new Vertex(
                        new Edge(polygon, vertices[newStop].getCommonPoint(), vertices[newStart].getCommonPoint()),
                        vertices[newStart].getFarEdge());
        polygon_1[polygon_1.length - 1] =
                new Vertex(
                        vertices[newStop].getNearEdge(),
                        new Edge(polygon, vertices[newStop].getCommonPoint(), vertices[newStart].getCommonPoint()));
        Vertex[] polygon_2 = new Vertex[vertices.length + 2 - polygon_1.length];
        int index = 1;
        for (int i = newStop + 1; i < vertices.length; i++) {
            polygon_2[index++] = vertices[i];
        }
        for (int i = 0; i < newStart; i++) {
            polygon_2[index++] = vertices[i];
        }
        polygon_2[0] =
                new Vertex(
                        new Edge(polygon, vertices[newStart].getCommonPoint(), vertices[newStop].getCommonPoint()),
                        vertices[newStop].getFarEdge());
        polygon_2[polygon_2.length - 1] =
                new Vertex(
                        vertices[newStart].getNearEdge(),
                        new Edge(polygon, vertices[newStart].getCommonPoint(), vertices[newStop].getCommonPoint()));
        return new Vertex[][] {polygon_1, polygon_2};
    }

    public static Point[] getPoints(Polygon polygon) {
        Point[] points = new Point[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            points[i] = new Point(polygon.xpoints[i], polygon.ypoints[i]);
        }
        return points;
    }

    public static Edge[] getEdges(Polygon polygon) {
        Point[] points = getPoints(polygon);
        Edge[] edges = new Edge[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            edges[i] = new Edge(polygon, points[i], points[i == polygon.npoints - 1 ? 0 : i + 1]);
        }
        return edges;
    }

    public static Handedness getInside(Polygon polygon) {
        Vertex[] vertices = getVertices(polygon);
        return getInside(vertices);
    }

    private static Handedness getInside(Vertex[] vertices) {
        double left = 0d;
        for (Vertex vertex : vertices) {
            left += vertex.getLeftAngle();
        }
        return left < vertices.length * Math.PI ? Handedness.LEFT : Handedness.RIGHT;
    }

    public static Vertex[] getVertices(Polygon polygon) {
        Edge[] edges = getEdges(polygon);
        Vertex[] vertices = new Vertex[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            vertices[i] = new Vertex(edges[(i == 0 ? polygon.npoints : i ) - 1], edges[i]);
        }
        return vertices;
    }

    public static List<Point> getShortestPathConnecting(Polygon polygon, int[][] pathsIndexes, Point2D.Float previous,
                                                        Point2D.Float next) {
        List<Point> bestRoute = null;
        double d = Double.MAX_VALUE;
        for (int[] path : pathsIndexes) {
            List<Point> pathPoints = getPath(polygon, path[0], path[1], true);
            double length = calculateLength(previous, pathPoints, next);
            if (length < d) {
                bestRoute = pathPoints;
                d = length;
            }
            pathPoints = getPath(polygon, path[0], path[1], false);
            length = calculateLength(previous, pathPoints, next);
            if (length < d) {
                bestRoute = pathPoints;
                d = length;
            }
        }
        return bestRoute;
    }

    private static double calculateLength(Point2D.Float previous, List<Point> pathPoints, Point2D.Float next) {
        Point2D.Float at = previous;
        double d = 0;
        for (Point p : pathPoints) {
            Point2D.Float point = new Point2D.Float(p.x, p.y);
            d += Math.sqrt(VertexExtensions.dSquared(at, point));
            at = point;
        }
        d += Math.sqrt(VertexExtensions.dSquared(at, next));
        return d;
    }

    private static List<Point> getPath(Polygon polygon, int fromIndex, int toIndex, boolean reverse) {
        List<Point> points = new ArrayList<Point>(polygon.npoints);
        if (fromIndex == toIndex) return points;
        int at = fromIndex;
        points.add(new Point(polygon.xpoints[at], polygon.ypoints[at]));
        int increment = reverse ? -1 : 1;
        do {
            at += increment;
            if (at < 0) {
                at += polygon.npoints;
            }
            else if (at >= polygon.npoints) {
                at -= polygon.npoints;
            }
            points.add(new Point(polygon.xpoints[at], polygon.ypoints[at]));
        } while (at != toIndex);
        return points;
    }

    public static NearestPoint getNearestPoint(Polygon[] areas, Point2D.Float point) {
        NearestPoint overallNearestPoint = null;
        for (Polygon polygon : areas) {
            NearestPoint nearestPoint = getNearestPoint(polygon, point);
            if (overallNearestPoint == null || nearestPoint.dSquared < overallNearestPoint.dSquared) {
                overallNearestPoint = nearestPoint;
            }
        }
        return overallNearestPoint;
    }

    public static Rectangle getBounds(Polygon[] areas) {
        Rectangle bounds = null;
        for (Polygon polygon : areas) {
            if (bounds == null) {
                bounds = polygon.getBounds();
            }
            else {
                bounds = bounds.union(polygon.getBounds());
            }
        }
        return bounds;
    }

    public static boolean contains(Polygon[] polygons, Point point) {
        for (Polygon polygon : polygons) {
            if (polygon.contains(point)) return true;
        }
        return false;
    }

    public static boolean contains(Polygon[] polygons, int x, int y) {
        return contains(polygons, new Point(x, y));
    }

    public static boolean contains(Polygon[] polygons, double x, double y) {
        for (Polygon polygon : polygons) {
            if (polygon.contains(x, y)) return true;
        }
        return false;
    }

    public static Point2D.Float getCentreOfGravity(Polygon[] polygons) {
        double x = 0;
        double y = 0;
        double totalArea = 0;
        for (int i = 0; i < polygons.length; i++) {
            Polygon polygon = polygons[i];
            double weight = getArea(polygon);
            Point2D.Float centreOfGravity = getCentreOfGravity(polygon, weight);
            totalArea += weight;
            x += centreOfGravity.getX() * weight;
            y += centreOfGravity.getY() * weight;
        }
        return new Point2D.Float((float)(x / totalArea), (float)(y / totalArea));
    }

    public static boolean contains(Polygon[] polygons, Point2D.Float centreOfGravity) {
        for (Polygon polygon : polygons) {
            if (polygon.contains(centreOfGravity)) return true;
        }
        return false;
    }

    public static boolean contains(Polygon[] polygons, Rectangle rectangle) {
        for (Polygon polygon : polygons) {
            if (polygon.contains(rectangle)) return true;
        }
        return false;
    }

    public static Polygon transform(Polygon polygon, AffineTransform affineTransform) {
        int[] xpoints = new int[polygon.npoints];
        int[] ypoints = new int[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            Point2D.Float transformed = new Point2D.Float();
            affineTransform.transform(new Point2D.Float(polygon.xpoints[i], polygon.ypoints[i]), transformed);
            xpoints[i] = Math.round(transformed.x);
            ypoints[i] = Math.round(transformed.y);
        }
        return new Polygon(xpoints, ypoints, polygon.npoints);
    }

    public static Polygon[] transform(Polygon[] polygons, AffineTransform affineTransform) {
        Polygon[] transformed = new Polygon[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            transformed[i] = transform(polygons[i], affineTransform);
        }
        return transformed;
    }

    public static Polygon construct(List<Point> points) {
        int[] xpoints = new int[points.size()];
        int[] ypoints = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            xpoints[i] = p.x;
            ypoints[i] = p.y;
        }
        return new Polygon(xpoints, ypoints, points.size());
    }

    public static Polygon[] translate(Polygon[] polygons, Point location) {
        Polygon[] translated = new Polygon[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            translated[i] = translate(polygons[i], location);
        }
        return translated;
    }

    public static Polygon translate(Polygon polygon, Point location) {
        int[] xpoints = new int[polygon.npoints];
        int[] ypoints = new int[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            xpoints[i] = polygon.xpoints[i] + location.x;
            ypoints[i] = polygon.ypoints[i] + location.y;
        }
        return new Polygon(xpoints, ypoints, polygon.npoints);
    }

    public static boolean intersects(Polygon[] polygons, Rectangle rectangle) {
        LOG.debug("rectangle: {}", rectangle);
        for (Polygon polygon : polygons) {
            LOG.debug("polygon.getBounds(): {}", polygon.getBounds());
            Rectangle bounds = polygon.getBounds();
            if (bounds.width == 0 || bounds.height == 0) {
                if (rectangle.intersects(new Rectangle(bounds.x, bounds.y,
                        bounds.width == 0 ? 1 : bounds.width,
                        bounds.height == 0 ? 1 : bounds.height))) {
                    LOG.debug("intersects");
                    return true;
                }
            }
            else if (polygon.intersects(rectangle)) {
                LOG.debug("intersects");
                return true;
            }
        }
        LOG.debug("doesn't intersect");
        return false;
    }

    public static Polygon[] removeRedundancies(Polygon[] polygons) {
        Polygon[] reduced = new Polygon[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            reduced[i] = removeRedundancies(polygons[i]);
        }
        return reduced;
    }

    public static Polygon removeRedundancies(Polygon polygon) {
        int[] xpoints = new int[polygon.npoints];
        int[] ypoints = new int[polygon.npoints];
        int reducedPointCount = 0;
        for (int i = 0; i < polygon.npoints; i++) {
            if (reducedPointCount == 0 || polygon.xpoints[i] != xpoints[reducedPointCount - 1] || polygon.ypoints[i] != ypoints[reducedPointCount - 1]) {
                xpoints[reducedPointCount] = polygon.xpoints[i];
                ypoints[reducedPointCount] = polygon.ypoints[i];
                reducedPointCount++;
            }
        }
        if (reducedPointCount > 1 && xpoints[0] == xpoints[reducedPointCount - 1] &&
                ypoints[0] == ypoints[reducedPointCount - 1]) {
            reducedPointCount--;
        }
        return new Polygon(xpoints, ypoints, reducedPointCount);
    }
    public static ClippedPolygons clipAndSegments(Polygon[] polygons, Rectangle clip) {
        List<Shape> shapes = new ArrayList<Shape>();
        List<Point[]> wobblyPoints = new ArrayList<Point[]>();
        for (Polygon polygon : polygons) {
            ClippedPolygons clippedPolygons = clipAndSegments(polygon, clip);
            shapes.addAll(Arrays.asList(clippedPolygons.clips));
            wobblyPoints.addAll(Arrays.asList(clippedPolygons.firstAndLastWobblySegments));
        }
        return new ClippedPolygons(wobblyPoints.toArray(new Point[0][]), shapes.toArray(new Shape[0]));
    }

    public static Shape[] clip(Polygon[] polygons, Rectangle clip) {
        List<Shape> shapes = new ArrayList<Shape>();
        for (Polygon polygon : polygons) {
            for (Shape shape : clip(polygon, clip)) {
                shapes.add(shape);
            }
        }
        return shapes.toArray(new Shape[shapes.size()]);
    }

    public static Shape[] clip(Polygon polygon, Rectangle clip) {
        List<ClippedSegment> clippedSegments = getClippedSegments(polygon, clip);
        return buildClippedShapes(polygon, clip, clippedSegments);
    }

    public static ClippedPolygons clipAndSegments(Polygon polygon, Rectangle clip) {
        List<ClippedSegment> clippedSegments = getClippedSegments(polygon, clip);
        List<Point[]> firstLastInternalSegments = new ArrayList<Point[]>();
        for (ClippedSegment clippedSegment : clippedSegments) {
            if (!clippedSegment.isInside() || clippedSegment.size() < 2) {
                continue;
            }
            firstLastInternalSegments.add(new Point[] {
                    clippedSegment.getPoints().get(0),
                    clippedSegment.getPoints().get(1)});
            firstLastInternalSegments.add(new Point[] {
                    clippedSegment.getPoints().get(clippedSegment.size() - 2),
                    clippedSegment.getPoints().get(clippedSegment.size() - 1)});
        }
        return new ClippedPolygons(firstLastInternalSegments.toArray(new Point[0][]),
                buildClippedShapes(polygon, clip, clippedSegments));
    }

    private static Shape[] buildClippedShapes(Polygon polygon, Rectangle clip, List<ClippedSegment> clippedSegments) {
        if (clippedSegments.size() == 1) {
            if (clippedSegments.get(0).isInside()) {
                return new Shape[] {polygon};
            }
            else if (polygon.contains(clip)) {
                return new Shape[] {clip};
            }
            return new Shape[0];
        }
        return new ClippedPolygonFactory(polygon, clip, clippedSegments).build();
    }

    public static List<ClippedSegment> getClippedSegments(Polygon polygon, Rectangle clip) {
        polygon = PolygonExtensions.removeRedundancies(polygon);
        Point previous = null;
        Boolean inside = null;
        List<Point> points = new ArrayList<Point>();
        List<ClippedSegment> clippedSegments = new ArrayList<ClippedSegment>();
        int start = 0;
        for (; start < polygon.npoints; start++) {
            if ((inside = RectangleExtensions.isInside(clip, new Point(polygon.xpoints[start], polygon.ypoints[start]))) != null) {
                break;
            }
        }
        if (inside == null) {
            inside = true;
        }
        for (int i = start; i <= polygon.npoints + start; i++) {
            Point point = new Point(polygon.xpoints[i % polygon.npoints], polygon.ypoints[i % polygon.npoints]);
            if (previous != null) {
                Integer edgeA = RectangleExtensions.getEdge(clip, previous);
                Integer edgeB = RectangleExtensions.getEdge(clip, point);
                if (edgeA == null || edgeA != edgeB) {
                    RectangleIntersection[] intersections = RectangleExtensions.getIntersection(clip, previous, point);
                    for (RectangleIntersection intersection : intersections) {
                        points.add(intersection.getIntersection());
                        clippedSegments.add(new ClippedSegment(points, inside == null ? true : inside));
                        inside = null;
                        points.clear();
                        points.add(intersection.getIntersection());
                    }
                }
            }
            if (points.size() == 0 || !points.get(points.size() - 1).equals(point)) {
                if (inside == null) {
                    inside = RectangleExtensions.isInside(clip, point);
                }
                points.add(point);
            }
            previous = point;
        }
        for (int i = clippedSegments.size() - 1; i > 0; i--) {
            if (clippedSegments.get(i - 1).isInside() == clippedSegments.get(i).isInside()) {
                clippedSegments.get(i - 1).append(clippedSegments.remove(i).getPoints());
            }
        }
        if (clippedSegments.isEmpty()) {
            clippedSegments.add(new ClippedSegment(points, inside));
        }
        else if (clippedSegments.get(0).isInside() == inside) {
            if (!points.isEmpty()) {
                clippedSegments.get(0).prepend(points.subList(0, points.size() - 1));
            }
        }
        else if (RectangleExtensions.getEdge(clip, clippedSegments.get(0).first()) != null) {
            clippedSegments.add(new ClippedSegment(points, inside));
        }
        else {
            LOG.error("clip: {}", clip);
            LOG.error("polygon: {}", Arrays.toString(PolygonExtensions.toPointArray(polygon)));
            for (int i = 0; i < clippedSegments.size(); i++) {
                LOG.error("clippedSegemet[{}]: {}", i, clippedSegments.get(i));
            }
            LOG.error("activeSegemet: {}, {}", Arrays.toString(points.toArray(new Point[points.size()])), inside);
            throw new RuntimeException("The surface has exhibited the behaviour of a Klein bottle");
        }
        return clippedSegments;
    }

    public static Point[] toPointArray(Polygon polygon) {
        Point[] points = new Point[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            points[i] = new Point(polygon.xpoints[i], polygon.ypoints[i]);
        }
        return points;
    }
}
