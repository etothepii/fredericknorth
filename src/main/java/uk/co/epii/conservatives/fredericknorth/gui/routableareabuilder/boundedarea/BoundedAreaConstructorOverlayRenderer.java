package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;

import java.awt.*;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 13:29
 */
class BoundedAreaConstructorOverlayRenderer extends BoundedAreaOverlayRenderer<BoundedAreaConstructor> {

    public BoundedAreaConstructorOverlayRenderer(Map<BoundedAreaType, Color> colors, int onBoundedAreaEdgeRadius) {
        super(colors, onBoundedAreaEdgeRadius);
    }

    @Override
    protected void processMouseLocation(OverlayItem<BoundedAreaConstructor> overlayItem,
                                        ImageAndGeoPointTranslator imageAndGeoPointTranslator, Point mouseLocation) {
        // Do nothing with the mouse
    }

    @Override
    protected boolean shouldResetOnMouseMove() {
        return true;
    }

    protected boolean shouldTrackMouse() {
        return false;
    }

    @Override
    protected boolean isClosed() {
        return false;
    }

    @Override
    protected Polygon[] deriveGeoPolygons(OverlayItem<BoundedAreaConstructor> overlayItem) {
        return new Polygon[] {PolygonExtensions.construct(overlayItem.getItem().getPointsToDraw())};
    }
}
