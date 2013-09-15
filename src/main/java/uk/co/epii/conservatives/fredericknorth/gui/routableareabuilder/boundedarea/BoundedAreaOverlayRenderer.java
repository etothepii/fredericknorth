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
class BoundedAreaOverlayRenderer<T extends BoundedArea> implements OverlayRenderer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaOverlayRenderer.class);
    private static final Logger LOG_POINTBYPOINT =
            LoggerFactory.getLogger(BoundedAreaOverlayRenderer.class.getName().concat("_pointByPoint"));

    protected final Map<BoundedAreaType, Color> colors;
    private Polygon[] polygons;
    private BoundedAreaReusableOverlay currentlyProducing;
    private int onBoundedAreaEdgeRadius;

    public BoundedAreaOverlayRenderer(Map<BoundedAreaType, Color> colors, int onBoundedAreaEdgeRadius) {
        this.colors = colors;
        this.onBoundedAreaEdgeRadius = onBoundedAreaEdgeRadius;
    }

    protected void setGeoPolygons(Polygon[] polygons, OverlayItem overlayItem,
                                  ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        Polygon[] geoPolygons = polygons;
        Polygon[] imagePolygons = PolygonExtensions.transform(geoPolygons,
                imageAndGeoPointTranslator.getGeoToImageTransform());
        Rectangle imageBounds = PolygonExtensions.getBounds(imagePolygons);
        currentlyProducing.setImageBounds(imageBounds);
        int radius = currentlyProducing.getRadius();
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
        currentlyProducing.setPolygons(this.polygons);
        currentlyProducing.setPreferredSize(new Dimension(imageBounds.width + radius * 2 + 1, imageBounds.height + radius * 2 + 1));
        currentlyProducing.setSize(currentlyProducing.getPreferredSize());
        Point drawFrom = overlayItem.getTopLeft(currentlyProducing.getSize(), imageAndGeoPointTranslator);
        currentlyProducing.setLocation(drawFrom.x - radius, drawFrom.y - radius);
    }

    @Override
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<T> overlayItem,
                                                 ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                 Point mouseLocation) {
        long start = System.nanoTime();
        try {
            currentlyProducing = new BoundedAreaReusableOverlay(mapPanel.getModel(), overlayItem, imageAndGeoPointTranslator, mouseLocation,
                    shouldResetOnMouseMove(), colors == null ? null : colors.get(overlayItem.getItem().getBoundedAreaType()),
                    isClosed(), shouldTrackMouse(), onBoundedAreaEdgeRadius);
            setGeoPolygons(deriveGeoPolygons(overlayItem), overlayItem, imageAndGeoPointTranslator);
            processMouseLocation(overlayItem, imageAndGeoPointTranslator, mouseLocation);
            Polygon[] screenPolygons = PolygonExtensions.translate(polygons, currentlyProducing.getLocation());
            Rectangle mapPanelBounds = new Rectangle(mapPanel.getSize());
            boolean polygonsVisible =
                    PolygonExtensions.intersects(screenPolygons, mapPanelBounds);
            LOG.debug("polygonsVisible: {}", polygonsVisible);
            return new RenderedOverlay(currentlyProducing, polygonsVisible ?
                    new RenderedOverlayPolygonBoundaryImpl(overlayItem, screenPolygons, currentlyProducing.getRadius()) :
                    null, overlayItem, true);
        }
        finally {
            long took = System.nanoTime() - start;
            LOG.debug("Generating overlay Renderer took {}ns", took);
        }
    }

    protected boolean isClosed() {
        return true;
    }

    protected boolean shouldResetOnMouseMove() {
        return false;
    }

    protected boolean shouldTrackMouse() {
        return true;
    }

    protected Polygon[] deriveGeoPolygons(OverlayItem<T> overlayItem) {
        return overlayItem.getItem().getAreas();
    }

    protected void processMouseLocation(OverlayItem<T> overlayItem, ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                        Point mouseLocation) {
        currentlyProducing.setMouseLocation(mouseLocation);
    }

    protected Polygon[] getPolygons() {
        return polygons;
    }
}
