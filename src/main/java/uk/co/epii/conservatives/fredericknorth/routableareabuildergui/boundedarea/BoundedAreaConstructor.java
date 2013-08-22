package uk.co.epii.conservatives.fredericknorth.routableareabuildergui.boundedarea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.*;
import uk.co.epii.conservatives.fredericknorth.extensions.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.extensions.VertexExtensions;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/07/2013
 * Time: 23:59
 */
public class BoundedAreaConstructor extends AbstractBoundedArea implements ExtendableBoundedArea {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaConstructor.class);

    private final BoundedArea parent;
    private List<BoundedArea> previousNeighbours;

    public BoundedAreaConstructor(BoundedArea parent, BoundedAreaType boundedAreaType, String name) {
        super(boundedAreaType, name);
        previousNeighbours = null;
        this.parent = parent;
    }

    @Override
    public void add(Point p, BoundedArea[] neighbours) {
        List<Point> points = getPoints();
        Point previous = points.isEmpty() ? null : points.get(points.size() - 1);
        if (previousNeighbours != null && !previousNeighbours.isEmpty()) {
            for (BoundedArea neighbour : neighbours) {
                if (!previousNeighbours.contains(neighbour)) continue;
                NearestPoint nearestPointToPrevious = neighbour.getNearestGeoPoint(new Point2D.Float(previous.x, previous.y));
                if (nearestPointToPrevious.dSquared > 1.5f) continue;
                addPointsBetween(new Point2D.Float(previous.x, previous.y), new Point2D.Float(p.x, p.y), neighbour);
                break;
            }
        }
        previousNeighbours = Arrays.asList(neighbours);
        LOG.debug("{}: {}", new Object[] {points.size(), p});
        points.add(p);
    }

    private void addPointsBetween(Point2D.Float previous, Point2D.Float toAdd, BoundedArea neighbour) {
        NearestPoint nearestPointToPrevious = neighbour.getNearestGeoPoint(previous);
        NearestPoint nearestPointToNew = neighbour.getNearestGeoPoint(toAdd);
        int[] nearestPointToPreviousVertexIndicies = new int[nearestPointToPrevious.nearestVertices.length];
        int[] nearestPointToNewVertexIndicies = new int[nearestPointToNew.nearestVertices.length];
        Polygon polygon = neighbour.getArea();
        for (int i = 0; i < polygon.npoints; i++) {
            Point2D.Float vertex = new Point2D.Float(polygon.xpoints[i], polygon.ypoints[i]);
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
        List<Point> pointsToAdd = PolygonExtensions.getShortestPathConnecting(polygon, pathsIndexes, previous, toAdd);
        List<Point> points = getPoints();
        for (Point p : pointsToAdd) {
            LOG.debug("{}: {}", new Object[] {points.size(), p});
            points.add(p);
        }
    }

    public BoundedArea getParent() {
        return parent;
    }

    public DefaultBoundedArea lockDown() {
        return new DefaultBoundedArea(this);
    }
}
