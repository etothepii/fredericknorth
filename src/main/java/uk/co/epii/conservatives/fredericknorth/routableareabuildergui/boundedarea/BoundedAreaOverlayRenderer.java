package uk.co.epii.conservatives.fredericknorth.routableareabuildergui.boundedarea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.extensions.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 18:13
 */
class BoundedAreaOverlayRenderer extends JPanel implements OverlayRenderer<BoundedArea> {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaOverlayRenderer.class);
    private static final int STICKY_BOUNDARY_RADIUS = 5;
    private static final int STICKY_BOUNDARY_RADIUS_SQUARED = STICKY_BOUNDARY_RADIUS * STICKY_BOUNDARY_RADIUS;

    private final Map<BoundedAreaType, Color> colors;
    private Point mouseGeoOnEdge;
    private Point mouseLocationWorldPosition;
    private Polygon polygon;
    protected Color color;
    private Point nearestImagePointOnBoundary;
    private int radius = 5;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;

    public BoundedAreaOverlayRenderer(Map<BoundedAreaType, Color> colors) {
        this.colors = colors;
    }

    @Override
    public boolean contains(int x, int y) {
        x = x - minX + radius;
        y = y - minY + radius;
        if (polygon.contains(x, y)) return true;
        return getMouseOnPolygon(new Point2D.Float(x, y)) != null;
    }

    @Override
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    @Override
    public Component getOverlayRendererComponent(OverlayItem<BoundedArea> overlayItem,
                                                 ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                 Point mouseLocation) {
        Point mouseGeoLocation = imageAndGeoPointTranslator.getGeoLocation(mouseLocation);
        color = colors == null ? null : colors.get(overlayItem.getItem().getBoundedAreaType());
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        Polygon polygon = overlayItem.getItem().getArea();
        List<Point> points = new ArrayList<Point>(polygon.npoints);
        for (int i = 0; i < polygon.npoints; i++) {
            Point point = imageAndGeoPointTranslator.getImageLocation(
                    new Point(polygon.xpoints[i], polygon.ypoints[i]));
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
            if (i == 0 || !points.get(points.size() - 1).equals(point)) {
                points.add(point);
            }
        }
        int[] xpoints = new int[points.size()];
        int[] ypoints = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            xpoints[i] = point.x - minX + radius;
            ypoints[i] = point.y - minY + radius;
        }
        setPreferredSize(new Dimension(maxX - minX + radius * 2 + 1, maxY - minY + radius * 2 + 1));
        setSize(getPreferredSize());
        this.polygon = new Polygon(xpoints, ypoints, points.size());
        if (mouseGeoLocation != null) {
            Point mouseOnImage = imageAndGeoPointTranslator.getImageLocation(mouseGeoLocation);
            Point2D.Float mouseOnPolygon = new Point2D.Float(
                    mouseOnImage.x - minX + radius,
                    mouseOnImage.y - minY + radius);
            setMouseOnPolygon(mouseOnPolygon);
        }
        if (nearestImagePointOnBoundary == null) {
            setMouseGeo(mouseGeoLocation);
        }
        else {
            Point2D.Float geoEdge = PolygonExtensions.getNearestPoint(
                    overlayItem.getItem().getArea(), new Point2D.Float(mouseGeoLocation.x, mouseGeoLocation.y)).point;
            setMouseGeo(new Point(Math.round(geoEdge.x), Math.round(geoEdge.y)));
        }
        setLocation(-radius, -radius);
        return this;
    }

    protected Polygon getPolygon() {
        return polygon;
    }

    private void setMouseOnPolygon(Point2D.Float mouseOnPolygon) {
        nearestImagePointOnBoundary = getMouseOnPolygon(mouseOnPolygon);
        LOG.debug("nearestImagePointOnBoundary: {}", nearestImagePointOnBoundary);
    }

    private Point getMouseOnPolygon(Point2D.Float mouseOnPolygon) {
        LOG.debug("mouseOnPolygon: {}", mouseOnPolygon);
        long time = System.nanoTime();
        NearestPoint nearestPoint =
                PolygonExtensions.getNearestPoint(polygon, mouseOnPolygon);
        long took = System.nanoTime() - time;
        if (nearestPoint == null) {
            return null;
        }
        LOG.debug("Finding nearest point took: {}ns", took);
        LOG.debug("nearestPoint: {} {} {}", new Object[] {nearestPoint.point.x, nearestPoint.point.y, nearestPoint.dSquared});
        return nearestPoint.dSquared > STICKY_BOUNDARY_RADIUS_SQUARED ? null :
                new Point((int)nearestPoint.point.x, (int)nearestPoint.point.y);
    }

    @Override
    public void paint(Graphics g) {
        paint((Graphics2D) g);
    }

    protected void paint(Graphics2D g) {
        long time = System.nanoTime();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 64));
        if (polygon.npoints < 3) {
            return;
        }
        g.fillPolygon(polygon);
        g.setColor(color);
        Stroke original = g.getStroke();
        g.setStroke(new BasicStroke(4));
        g.drawPolygon(polygon);
        g.setStroke(original);
        if (nearestImagePointOnBoundary != null) {
            LOG.debug("Nearest Boundary: {}", nearestImagePointOnBoundary);
            g.fillOval(nearestImagePointOnBoundary.x - radius, nearestImagePointOnBoundary.y - radius, radius * 2, radius * 2);
        }
        long took = System.nanoTime() - time;
        LOG.debug("Rendering overlay item: {}ns", took);
    }

    @Override
    public Point getMouseGeo() {
        return mouseLocationWorldPosition;
    }

    private void setMouseGeo(Point mouseGeo) {
        mouseLocationWorldPosition = mouseGeo;
    }

    @Override
    public boolean isMouseOnBoundary() {
        return nearestImagePointOnBoundary != null;
    }
}
