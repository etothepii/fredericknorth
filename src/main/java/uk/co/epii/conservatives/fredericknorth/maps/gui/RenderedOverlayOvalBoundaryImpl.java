package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 06/09/2013
 * Time: 17:26
 */
public class RenderedOverlayOvalBoundaryImpl implements RenderedOverlayBoundary {

    private final OverlayItem overlayItem;
    private final Point centre;
    private final boolean canBeOnEdge;
    private final double radiusSquared;
    private final double edgeOutsideSquared;
    private final double edgeInsideSquared;
    private Point mouseLocation;
    private boolean onEdge;
    private boolean inside;
    private final double outsideRadius;

    public RenderedOverlayOvalBoundaryImpl(OverlayItem overlayItem, Point centre, double radius) {
        this(overlayItem, centre, radius, false, Double.NaN);
    }

    public RenderedOverlayOvalBoundaryImpl(OverlayItem overlayItem, Point centre, double radius, double edgeDistance) {
        this(overlayItem, centre, radius, edgeDistance >= 0, edgeDistance);
    }

    private RenderedOverlayOvalBoundaryImpl(OverlayItem overlayItem, Point centre, double radius,
                                            boolean canBeOnEdge, double edgeDistance) {
        this.overlayItem = overlayItem;
        this.centre = centre;
        this.outsideRadius = edgeDistance + radius;
        this.radiusSquared = radius * radius;
        this.canBeOnEdge = canBeOnEdge;
        this.edgeOutsideSquared = (edgeDistance + radius) * (edgeDistance + radius);
        this.edgeInsideSquared = (radius - edgeDistance) * (radius - edgeDistance);
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
        return null;
    }

    private void setMouseLocation(Point mouseLocation) {
        if (this.mouseLocation != null && this.mouseLocation.equals(mouseLocation)) {
            return;
        }
        this.mouseLocation = mouseLocation;
        double dx = mouseLocation.x - centre.x + .5;
        double dy = mouseLocation.y - centre.y + .5;
        double dSquared = dx * dx + dy * dy;
        onEdge = canBeOnEdge && dSquared >= edgeInsideSquared && dSquared <= edgeOutsideSquared;
        inside = onEdge || dSquared <= radiusSquared;
    }

    @Override
    public OverlayItem getOverlayItem() {
        return overlayItem;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(
                (int)Math.round(Math.floor(centre.x - outsideRadius)),
                (int)Math.round(Math.floor(centre.y - outsideRadius)),
                (int)Math.round(Math.floor(outsideRadius * 2)),
                (int)Math.round(Math.floor(outsideRadius * 2)));
    }
}
