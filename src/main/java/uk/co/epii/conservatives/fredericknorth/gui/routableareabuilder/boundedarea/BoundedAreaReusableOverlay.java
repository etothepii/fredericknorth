package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelModel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.ReusableOverlay;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 14/09/2013
 * Time: 11:27
 */
public class BoundedAreaReusableOverlay<T extends BoundedArea> extends ReusableOverlay {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaReusableOverlay.class);
    private static final int STICKY_BOUNDARY_RADIUS = 5;
    private static final int STROKE_WIDTH = 4;
    private static final int STICKY_BOUNDARY_RADIUS_SQUARED = STICKY_BOUNDARY_RADIUS * STICKY_BOUNDARY_RADIUS;

    private final OverlayItem<T> overlayItem;
    private final ImageAndGeoPointTranslator imageAndGeoPointTranslator;
    private final Stroke stroke;

    private Point nearestImagePointOnBoundary;
    private int radius;
    private Rectangle imageBounds;
    private Polygon[] polygons;
    private Color color;
    private BufferedImage bufferedImage;
    private boolean closed;
    private boolean resetOnMouseMove;
    private boolean stillUsable;
    private boolean trackMouse;
    private Point mouseLocation;
    private MapPanelModel mapPanelModel;

    public BoundedAreaReusableOverlay(MapPanelModel mapPanelModel, OverlayItem<T> overlayItem,
                                      ImageAndGeoPointTranslator imageAndGeoPointTranslator, Point mouseLocation,
                                      boolean resetOnMouseMove, Color color, boolean closed, boolean trackMouse,
                                      int radius) {
        this.mapPanelModel = mapPanelModel;
        this.imageAndGeoPointTranslator = imageAndGeoPointTranslator;
        this.radius = radius;
        this.overlayItem = overlayItem;
        this.resetOnMouseMove = resetOnMouseMove;
        this.color = color;
        this.closed = closed;
        this.mouseLocation = mouseLocation;
        this.trackMouse = trackMouse;
        stillUsable = true;
        stroke = new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    @Override
    public boolean contains(int x, int y) {
        x = x - imageBounds.x + radius;
        y = y - imageBounds.y + radius;
        return PolygonExtensions.contains(polygons, x, y) ||
                getMouseOnPolygon(new Point2D.Float(x, y)) != null;
    }

    @Override
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    @Override
    public void paint(Graphics g) {
        if (bufferedImage == null) {
            buffer();
        }
        Point location = getLocation();
        g.drawImage(bufferedImage, Math.max(0, -location.x), Math.max(0, -location.y), null);
        if (nearestImagePointOnBoundary != null) {
            LOG.debug("Nearest Boundary: {}ns", nearestImagePointOnBoundary);
            g.setColor(color);
            g.fillOval(nearestImagePointOnBoundary.x - radius, nearestImagePointOnBoundary.y - radius, radius * 2, radius * 2);
        }
    }

    public void setPolygons(Polygon[] polygons) {
        this.polygons = polygons;
    }

    public void buffer() {
        Graphics2D g = getBufferGraphics();
        long start = System.nanoTime();
        for (Polygon polygon : polygons) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 64));
            if (polygon.npoints >= 3) {
                long shapeStart = System.nanoTime();
                Shape[] clippedShapes = PolygonExtensions.clip(polygon, new Rectangle(getWidth(), getHeight()));
                long took = System.nanoTime() - shapeStart;
                shapeStart = System.nanoTime();
                LOG.debug("Calculating Clips: {}ns", took);
                for (Shape clippedShape : clippedShapes) {
                    LOG.debug("clippedPolygon.getBounds(): {}", clippedShape.getBounds());
                    g.fill(clippedShape);
                }
                took = System.nanoTime() - shapeStart;
                LOG.debug("Filling Polygon took: {}ns", took);
            }
            g.setColor(color);
            Stroke original = g.getStroke();
            g.setStroke(stroke);
            if (closed) {
                if (polygon.npoints > 2) {
                    g.drawPolygon(polygon);
                }
            }
            else {
                if (polygon.npoints > 1) {
                    g.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
                }
                else if (polygon.npoints == 1) {
                    g.fill(RectangleExtensions.grow(
                            new Rectangle(new Point(polygon.xpoints[0], polygon.ypoints[0])),
                            Math.round(STROKE_WIDTH / 2f)));
                }
            }
            g.setStroke(original);
        }
        long took = System.nanoTime() - start;
        LOG.debug("Painting Bounded Area {} took: {}ns", overlayItem.getItem().getName(), took);
    }

    private Graphics2D getBufferGraphics() {
        LOG.debug("buffering image ({} x {})", getWidth(), getHeight());
        Point location = getLocation();
        Rectangle intersection = new Rectangle(location, getSize()
                                        ).intersection(new Rectangle(mapPanelModel.getCurrentMapView().getSize()));
        bufferedImage = new BufferedImage(intersection.width, intersection.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        if (location.x < 0 || location.y < 0) {
            g.setTransform(AffineTransform.getTranslateInstance(Math.min(0, location.x), Math.min(0, location.y)));
        }
        return g;
    }

    public void setImageBounds(Rectangle imageBounds) {
        this.imageBounds = imageBounds;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public void setMouseLocation(Point mouseLocation) {
        if (resetOnMouseMove && !mouseLocation.equals(this.mouseLocation)) {
            stillUsable = false;
            return;
        }
        if (!trackMouse) {
            return;
        }
        Point mouseGeoLocation = imageAndGeoPointTranslator.getGeoLocation(mouseLocation);
        if (mouseGeoLocation != null) {
            Point mouseOnImage = imageAndGeoPointTranslator.getImageLocation(mouseGeoLocation);
            Point2D.Float mouseOnPolygon = new Point2D.Float(
                    mouseOnImage.x - imageBounds.x + radius,
                    mouseOnImage.y - imageBounds.y + radius);
            setMouseOnPolygon(mouseOnPolygon);
        }
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

    public Color getColor() {
        return color;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public boolean isStillUsable() {
        return stillUsable;
    }
}
