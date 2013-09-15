package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 06/09/2013
 * Time: 16:19
 */
public class RenderedOverlay {

    private final Component component;
    private final RenderedOverlayBoundary boundary;
    private final OverlayItem overlayItem;
    private final boolean isReusable;

    public RenderedOverlay(Component component, RenderedOverlayBoundary boundary, OverlayItem overlayItem, boolean reusable) {
        this.component = component;
        this.boundary = boundary;
        this.overlayItem = overlayItem;
        isReusable = reusable;
    }

    public RenderedOverlayBoundary getBoundary() {
        return boundary;
    }

    public Component getComponent() {
        return component;
    }

    public OverlayItem getOverlayItem() {
        return overlayItem;
    }

    public boolean isReusable() {
        return isReusable;
    }
}
