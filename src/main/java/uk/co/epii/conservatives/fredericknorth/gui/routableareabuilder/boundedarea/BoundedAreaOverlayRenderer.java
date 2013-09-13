package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.java2d.SunGraphics2D;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 18:13
 */
class BoundedAreaOverlayRenderer<T extends BoundedArea> extends JPanel implements OverlayRenderer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaOverlayRenderer.class);
    private static final Logger LOG_POINTBYPOINT =
            LoggerFactory.getLogger(BoundedAreaOverlayRenderer.class.getName().concat("_pointByPoint"));
    private static final int STICKY_BOUNDARY_RADIUS = 5;
    private static final int STICKY_BOUNDARY_RADIUS_SQUARED = STICKY_BOUNDARY_RADIUS * STICKY_BOUNDARY_RADIUS;

    protected final Map<BoundedAreaType, Color> colors;
    private Point mouseGeoOnEdge;
    private Point mouseLocationWorldPosition;
    private Polygon[] polygons;
    protected Color color;
    private Point nearestImagePointOnBoundary;
    private int radius = 5;
    private Rectangle imageBounds;
    private OverlayItem<T> overlayItem;
    private boolean whollyContained;

    public BoundedAreaOverlayRenderer(Map<BoundedAreaType, Color> colors) {
        this.colors = colors;
    }

    @Override
    public boolean contains(int x, int y) {
        x = x - imageBounds.x + radius;
        y = y - imageBounds.y + radius;
        if (PolygonExtensions.contains(polygons, x, y)) return true;
        return getMouseOnPolygon(new Point2D.Float(x, y)) != null;
    }

    @Override
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    protected void setGeoPolygons(Polygon[] polygons, OverlayItem overlayItem,
                                  ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        Polygon[] geoPolygons = polygons;
        Polygon[] imagePolygons = PolygonExtensions.transform(geoPolygons,
                imageAndGeoPointTranslator.getGeoToImageTransform());
        imageBounds = PolygonExtensions.getBounds(imagePolygons);
        AffineTransform internalComponentTransform =
                AffineTransform.getTranslateInstance(radius - imageBounds.x, radius - imageBounds.y);
        LOG.debug("imageBounds: {}", imageBounds);
        this.polygons = PolygonExtensions.removeRedundancies(
                PolygonExtensions.transform(imagePolygons, internalComponentTransform));
        if (LOG_POINTBYPOINT.isDebugEnabled()) {
            for (Polygon polygon : this.polygons) {
                LOG_POINTBYPOINT.debug("Setting polygon");
                for (int i = 0; i < polygon.npoints; i++) {
                    LOG_POINTBYPOINT.debug("{}: ({}, {})", new Object[] {i, polygon.xpoints[i], polygon.ypoints[i]});
                }
            }
        }
        setPreferredSize(new Dimension(imageBounds.width + radius * 2 + 1, imageBounds.height + radius * 2 + 1));
        setSize(getPreferredSize());
        Point drawFrom = overlayItem.getTopLeft(getSize(), imageAndGeoPointTranslator);
        setLocation(drawFrom.x - radius, drawFrom.y - radius);
    }

    @Override
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<T> overlayItem,
                                                 ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                 Point mouseLocation) {
        long start = System.nanoTime();
        try {
            color = colors == null ? null : colors.get(overlayItem.getItem().getBoundedAreaType());
            setGeoPolygons(deriveGeoPolygons(overlayItem), overlayItem, imageAndGeoPointTranslator);
            processMouseLocation(overlayItem, imageAndGeoPointTranslator, mouseLocation);
            Polygon[] screenPolygons = PolygonExtensions.translate(polygons, getLocation());
            this.overlayItem = overlayItem;
            Rectangle mapPanelBounds = new Rectangle(mapPanel.getSize());
            boolean polygonsVisible =
                    PolygonExtensions.intersects(screenPolygons, mapPanelBounds);
            whollyContained = PolygonExtensions.contains(screenPolygons, RectangleExtensions.grow(mapPanelBounds, radius));
            LOG.debug("polygonsVisible: {}", polygonsVisible);
            return new RenderedOverlay(this, polygonsVisible ?
                    new RenderedOverlayPolygonBoundaryImpl(overlayItem, screenPolygons, radius) : null);
        }
        finally {
            long took = System.nanoTime() - start;
            LOG.debug("Generating overlay Renderer took {}ns", took);
        }
    }



    protected Polygon[] deriveGeoPolygons(OverlayItem<T> overlayItem) {
        return overlayItem.getItem().getAreas();
    }

    protected void processMouseLocation(OverlayItem<T> overlayItem, ImageAndGeoPointTranslator imageAndGeoPointTranslator, Point mouseLocation) {
        Point mouseGeoLocation = imageAndGeoPointTranslator.getGeoLocation(mouseLocation);
        if (mouseGeoLocation != null) {
            Point mouseOnImage = imageAndGeoPointTranslator.getImageLocation(mouseGeoLocation);
            Point2D.Float mouseOnPolygon = new Point2D.Float(
                    mouseOnImage.x - imageBounds.x + radius,
                    mouseOnImage.y - imageBounds.y + radius);
            setMouseOnPolygon(mouseOnPolygon);
        }
        if (nearestImagePointOnBoundary == null) {
            setMouseGeo(mouseGeoLocation);
        }
        else {
            Point2D.Float geoEdge = PolygonExtensions.getNearestPoint(
                    overlayItem.getItem().getAreas(), new Point2D.Float(mouseGeoLocation.x, mouseGeoLocation.y)).point;
            setMouseGeo(new Point(Math.round(geoEdge.x), Math.round(geoEdge.y)));
        }
    }

    protected Polygon[] getPolygons() {
        return polygons;
    }

    private void setMouseOnPolygon(Point2D.Float mouseOnPolygon) {
        nearestImagePointOnBoundary = getMouseOnPolygon(mouseOnPolygon);
        LOG.debug("nearestImagePointOnBoundary: {}", nearestImagePointOnBoundary);
    }

    private Point getMouseOnPolygon(Point2D.Float mouseOnPolygon) {
        LOG.debug("mouseOnPolygon: {}", mouseOnPolygon);
        long time = System.nanoTime();
        NearestPoint nearestPoint =
                PolygonExtensions.getNearestPoint(polygons, mouseOnPolygon);
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
        long start = System.nanoTime();
        for (Polygon polygon : polygons) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 64));
            if (polygon.npoints < 3) {
                continue;
            }
            long shapeStart = System.nanoTime();
            Shape[] clippedShapes = PolygonExtensions.clip(polygon, g.getClipBounds());
            long took = System.nanoTime() - shapeStart;
            shapeStart = System.nanoTime();
            LOG.debug("Calculating Clips: {}ns", took);
            for (Shape clippedShape : clippedShapes) {
                LOG.debug("clippedPolygon.getBounds(): {}", clippedShape.getBounds());
                g.fill(clippedShape);
            }
            took = System.nanoTime() - shapeStart;
            LOG.debug("Filling Polygon took: {}ns", took);
            g.setColor(color);
            Stroke original = g.getStroke();
            g.setStroke(new BasicStroke(4));
            g.drawPolygon(polygon);
            g.setStroke(original);
            if (nearestImagePointOnBoundary != null) {
                LOG.debug("Nearest Boundary: {}ns", nearestImagePointOnBoundary);
                g.fillOval(nearestImagePointOnBoundary.x - radius, nearestImagePointOnBoundary.y - radius, radius * 2, radius * 2);
            }
        }
        long took = System.nanoTime() - start;
        LOG.debug("Painting Bounded Area {} took: {}ns", overlayItem.getItem().getName(), took);
    }

    private void setMouseGeo(Point mouseGeo) {
        mouseLocationWorldPosition = mouseGeo;
    }
}
