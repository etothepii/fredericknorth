package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.*;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.VertexExtensions;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/07/2013
 * Time: 23:59
 */
public class BoundedAreaConstructor extends AbstractBoundedArea implements ExtendableBoundedArea {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaConstructor.class);
    private static final Logger LOG_POINT_BY_POINT =
            LoggerFactory.getLogger(BoundedAreaConstructor.class.getName().concat("_PointByPoint"));

    private final BoundedArea parent;
    private List<BoundedArea> previousNeighbours;
    private Point currentPoint;
    private List<BoundedArea> currentNeighbours;
    private List<Point> inbetweenPoints;

    public BoundedAreaConstructor(BoundedArea parent, BoundedAreaType boundedAreaType, String name) {
        super(parent, boundedAreaType, name);
        previousNeighbours = null;
        this.parent = parent;
        getPoints().add(new ArrayList<Point>());
    }

    @Override
    public void addCurrent() {
        if (currentPoint == null) {
            return;
        }
        List<Point> points = getPoints().get(0);
        Point previous = points.isEmpty() ? null : points.get(points.size() - 1);
        if (currentPoint.equals(previous)) {
            return;
        }
        points.addAll(inbetweenPoints);
        points.add(currentPoint);
        previousNeighbours = currentNeighbours;
        inbetweenPoints = null;
        currentNeighbours = null;
        currentPoint = null;
    }

    public void setCurrent(Point p, BoundedArea[] neighbours) {
        LOG.debug("setCurrent: {}", p);
        LOG.debug("neighbours.length: {}", neighbours.length);
        List<Point> points = getPoints().get(0);
        Point previous = points.isEmpty() ? null : points.get(points.size() - 1);
        inbetweenPoints = new ArrayList<Point>();
        if (previousNeighbours != null && !previousNeighbours.isEmpty()) {
            for (BoundedArea neighbour : neighbours) {
                if (!previousNeighbours.contains(neighbour)) continue;
                NearestPoint nearestPointToPrevious = neighbour.getNearestGeoPoint(PointExtensions.toFloat(previous));
                if (nearestPointToPrevious.dSquared > 1.5f) {
                    LOG.warn("The previous point though registering as a neighbour was not nailed on the shared boundary");
                };
                NearestPoint nearestPointToThis = neighbour.getNearestGeoPoint(PointExtensions.toFloat(p));
                calculateCurrentPointsBetween(nearestPointToPrevious.point, nearestPointToThis.point,
                        nearestPointToPrevious.polygon);
                Point firstOnPoygon = PointExtensions.fromFloat(nearestPointToPrevious.point);
                if (!previous.equals(firstOnPoygon)) {
                    inbetweenPoints.add(0, firstOnPoygon);
                }
                break;
            }
        }
        currentNeighbours = Arrays.asList(neighbours);
        LOG.debug("{}: {}", new Object[] {points.size(), p});
        currentPoint = p;
    }

    @Override
    public List<Point> getPointsToDraw() {
        List<Point> points = getPoints().get(0);
        int pointCount = 1 + points.size() + (inbetweenPoints == null ? 0 : inbetweenPoints.size());
        ArrayList<Point> pointsToDraw = new ArrayList<Point>(pointCount);
        pointsToDraw.addAll(points);
        if (inbetweenPoints != null) {
            pointsToDraw.addAll(inbetweenPoints);
        }
        if (currentPoint != null) {
            pointsToDraw.add(currentPoint);
        }
        if (LOG_POINT_BY_POINT.isDebugEnabled()) {
            for (Point point : pointsToDraw) {
                LOG_POINT_BY_POINT.debug("DrawPoint: {}", point);
            }
        }
        return pointsToDraw;
    }

    private void calculateCurrentPointsBetween(Point2D.Float previous, Point2D.Float toAdd, Polygon neighbour) {
        NearestPoint nearestPointToPrevious = PolygonExtensions.getNearestPoint(neighbour, previous);
        NearestPoint nearestPointToNew = PolygonExtensions.getNearestPoint(neighbour, toAdd);
        int[] nearestPointToPreviousVertexIndicies = new int[nearestPointToPrevious.nearestVertices.length];
        int[] nearestPointToNewVertexIndicies = new int[nearestPointToNew.nearestVertices.length];
        for (int i = 0; i < neighbour.npoints; i++) {
            Point2D.Float vertex = new Point2D.Float(neighbour.xpoints[i], neighbour.ypoints[i]);
            for (int j = 0; j < nearestPointToPrevious.nearestVertices.length; j++) {
                if (VertexExtensions.dSquared(nearestPointToPrevious.nearestVertices[j].getCommonPoint(), vertex) < 1) {
                    nearestPointToPreviousVertexIndicies[j] = i;
                }
            }
            for (int j = 0; j < nearestPointToNew.nearestVertices.length; j++) {
                if (VertexExtensions.dSquared(nearestPointToNew.nearestVertices[j].getCommonPoint(), vertex) < 1) {
                    nearestPointToNewVertexIndicies[j] = i;
                }
            }
        }
        int[][] pathsIndexes = new int[nearestPointToNewVertexIndicies.length * nearestPointToPreviousVertexIndicies.length][];
        int index = 0;
        for (int i = 0; i < nearestPointToPreviousVertexIndicies.length; i++) {
            for (int j = 0; j < nearestPointToNewVertexIndicies.length; j++) {
                pathsIndexes[index++] = new int[] {
                        nearestPointToPreviousVertexIndicies[i],
                        nearestPointToNewVertexIndicies[j]
                };
            }
        }
        List<Point> pointsToAdd = PolygonExtensions.getShortestPathConnecting(neighbour, pathsIndexes, previous, toAdd);
        for (Point p : pointsToAdd) {
            LOG.debug("{}: {}", new Object[] {inbetweenPoints.size(), p});
            inbetweenPoints.add(p);
        }
    }

    public BoundedArea getParent() {
        return parent;
    }

    public DefaultBoundedArea lockDown() {
        return new DefaultBoundedArea(this);
    }

    public List<Point> getActivePoints() {
        if (_points.isEmpty()) {
            _points.add(new ArrayList<Point>());
        }
        return _points.get(_points.size());
    }
}
