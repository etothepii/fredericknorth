package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 10:24
 */
public interface OverlayRenderer<T> {
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<T> overlayItem,
                                                 ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                 Point mouseGeoLocation);
}
