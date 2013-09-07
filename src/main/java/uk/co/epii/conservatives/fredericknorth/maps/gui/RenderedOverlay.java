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

    public RenderedOverlay(Component component, RenderedOverlayBoundary boundary) {
        this.component = component;
        this.boundary = boundary;
    }

    public RenderedOverlayBoundary getBoundary() {
        return boundary;
    }

    public Component getComponent() {
        return component;
    }
}
