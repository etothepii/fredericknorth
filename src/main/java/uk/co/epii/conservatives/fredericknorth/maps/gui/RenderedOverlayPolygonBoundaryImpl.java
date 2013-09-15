package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 06/09/2013
 * Time: 16:21
 */
public class RenderedOverlayPolygonBoundaryImpl implements RenderedOverlayBoundary {

    private final OverlayItem overlayItem;
    private final Polygon[] rendered;
    private final boolean canBeOnEdge;
    private final double edgeDistanceSquared;
    private final double edgeDistance;
    private Rectangle _bounds;
    private Point mouseLocation;
    private boolean onEdge;
    private boolean inside;
    private NearestPoint nearestPoint;

    public RenderedOverlayPolygonBoundaryImpl(OverlayItem overlayItem, Polygon[] rendered) {
        this(overlayItem, rendered, false, Double.NaN);
    }

    public RenderedOverlayPolygonBoundaryImpl(OverlayItem overlayItem, Polygon[] rendered, double edgeDistance) {
        this(overlayItem, rendered, edgeDistance > 0, edgeDistance);
    }

    private RenderedOverlayPolygonBoundaryImpl(OverlayItem overlayItem, Polygon[] rendered, boolean canBeOnEdge, double edgeDistanceSquared) {
        this.overlayItem = overlayItem;
        this.rendered = rendered;
        this.canBeOnEdge = canBeOnEdge;
        this.edgeDistanceSquared = edgeDistanceSquared;
        edgeDistance = canBeOnEdge ? Math.sqrt(edgeDistanceSquared) : Double.NaN;
        nearestPoint = null;
    }

    @Override
    public OverlayItem getOverlayItem() {
        return overlayItem;
    }

    private void setMouseLocation(Point mouseLocation) {
        if (this.mouseLocation == mouseLocation || (
                this.mouseLocation != null && this.mouseLocation.equals(mouseLocation))) {
            return;
        }
        this.mouseLocation = mouseLocation;
        if (!getBounds().contains(mouseLocation)) {
            onEdge = false;
            inside = false;
            return;
        }
        NearestPoint nearestPoint =
                PolygonExtensions.getNearestPoint(rendered, new Point2D.Float(mouseLocation.x, mouseLocation.y));
        onEdge = nearestPoint.dSquared < edgeDistanceSquared;
        this.nearestPoint = onEdge ? nearestPoint : null;
        inside = onEdge || PolygonExtensions.contains(rendered, mouseLocation);
    }

    @Override
    public Rectangle getBounds() {
        if (_bounds == null) {
            _bounds = RectangleExtensions.grow(
                    PolygonExtensions.getBounds(rendered), canBeOnEdge ?
                    Math.round((float)Math.ceil(edgeDistance)) : 0);
        }
        return _bounds;
    }

    @Override
    public boolean isOnEdge(Point mouseLocation) {
        setMouseLocation(mouseLocation);
        return onEdge;
    }

    @Override
    public boolean isInside(Point mouseLocation) {
        setMouseLocation(mouseLocation);
        return inside;
    }

    @Override
    public NearestPoint getNearestPoint() {
        return nearestPoint;
    }
}
