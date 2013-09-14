package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 06/09/2013
 * Time: 15:23
 */
public interface RenderedOverlayBoundary {

    public boolean isOnEdge(Point mouseLocation);
    public boolean isInside(Point mouseLocation);
    public OverlayItem getOverlayItem();

}
