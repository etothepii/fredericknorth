package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractMapPanelModel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MouseLocation;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaConstructor;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaExtensions;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.ConstructorOverlay;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 13:20
 */
class RoutableAreaBuilderMapPanelModel extends AbstractMapPanelModel {

    private static final Logger LOG = LoggerFactory.getLogger(RoutableAreaBuilderMapPanelModel.class);

    private final RoutableAreaBuilderPanelModel routableAreaBuilderPanelModel;
    private final ConstructorOverlay constructorOverlay;
    private final int onBoundaryDotRadius = 5;

    RoutableAreaBuilderMapPanelModel(MapViewGenerator mapViewGenerator, RoutableAreaBuilderPanelModel routableAreaBuilderPanelModel, ConstructorOverlay constructorOverlay) {
        super(mapViewGenerator);
        this.routableAreaBuilderPanelModel = routableAreaBuilderPanelModel;
        this.constructorOverlay = constructorOverlay;
        enableOverlayRenderers();
    }

    private void enableOverlayRenderers() {
        Map<BoundedAreaType, Color> colorMap = new EnumMap<BoundedAreaType, Color>(BoundedAreaType.class);
        for (BoundedAreaType boundedAreaType : BoundedAreaType.values()) {
            if (!colorMap.containsKey(boundedAreaType)) {
                colorMap.put(boundedAreaType, boundedAreaType.getDefaultColour());
            }
        }
        setOverlayRenderer(BoundedArea.class,
                BoundedAreaExtensions.getOverlayRenderer(colorMap, onBoundaryDotRadius));
        setOverlayRenderer(BoundedAreaConstructor.class,
                BoundedAreaExtensions.getConstructorOverlayRenderer(colorMap, onBoundaryDotRadius));
    }

    @Override
    public void doubleClicked(MouseEvent e) {
        LOG.debug("You double clicked me");
        if (e.getButton() == MouseEvent.BUTTON1) {
            routableAreaBuilderPanelModel.getMapPanelModel().removeOverlay(constructorOverlay);
            routableAreaBuilderPanelModel.getBoundedAreaSelectionModel().add(
                    constructorOverlay.getItem().getParent(), constructorOverlay.getItem().lockDown());
            constructorOverlay.setBoundedAreaConstructor(null);
        }
    }

    @Override
    public void mouseMovedTo(Point point) {
        LOG.debug("mouseMovedTo: {}", point);
        if (constructorOverlay.getItem() != null && rectanglesToRepaint != null) {
            rectanglesToRepaint.add(render(mapPanel, constructorOverlay).getComponent().getBounds());
        }
        else if (getMouseAt() != null && rectanglesToRepaint != null) {
            rectanglesToRepaint.add(RectangleExtensions.grow(new Rectangle(getMouseAt()), onBoundaryDotRadius * 2 + 1));
        }
        super.mouseMovedTo(point);
        if (constructorOverlay.getItem() != null) {
            recalculateConstructorOverlay(point);
        }
        if (constructorOverlay.getItem() != null && rectanglesToRepaint != null) {
            rectanglesToRepaint.add(render(mapPanel, constructorOverlay).getComponent().getBounds());
        }
        else if (rectanglesToRepaint != null) {
            rectanglesToRepaint.add(RectangleExtensions.grow(new Rectangle(getMouseAt()), onBoundaryDotRadius * 2 + 1));
        }
    }

    private void recalculateConstructorOverlay(Point point) {
        Map<BoundedArea, Point> boundedAreas =
                new HashMap<BoundedArea, Point>();
        Map<OverlayItem, MouseLocation> itemsMouseOver =
                routableAreaBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
        LOG.debug("itemsMouseOver.size(): {}", itemsMouseOver.size());
        for (Map.Entry<OverlayItem, MouseLocation> entry : itemsMouseOver.entrySet()) {
            if (!(entry.getKey().getItem() instanceof BoundedArea)) {
                continue;
            }
            OverlayItem<BoundedArea> overlay = (OverlayItem<BoundedArea>)entry.getKey();
            MouseLocation mouseLocation = entry.getValue();
            LOG.debug("mouseLocations.isMouseStuck(): {}", mouseLocation.isMouseStuck());
            if (!mouseLocation.isMouseStuck()) {
                continue;
            }
            if (overlay.getItem() != constructorOverlay.getItem()) {
                boundedAreas.put(overlay.getItem(), mouseLocation.getGeoLocation());
            }
        }
        if (boundedAreas.isEmpty()) {
            constructorOverlay.getItem().setCurrent(
                    routableAreaBuilderPanelModel.getMapPanelModel().getCurrentMapView().getGeoLocation(point),
                    new BoundedArea[0]);
            return;
        }
        HashSet<Point> points = new HashSet<Point>(boundedAreas.size());
        BoundedArea[] neighbours = new BoundedArea[boundedAreas.size()];
        int index = 0;
        for (Map.Entry<BoundedArea, Point> mouseLocation : boundedAreas.entrySet()) {
            neighbours[index++] = mouseLocation.getKey();
            points.add(mouseLocation.getValue());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("neighbours");
            for (BoundedArea boundedArea : neighbours) {
                LOG.debug("    {}", boundedArea.getName());
            }
        }
        if (points.size() == 1) {
            constructorOverlay.getItem().setCurrent(points.iterator().next(), neighbours);
        }
        else {
            LOG.warn("There are too many points: {}", points);
            Point median = PointExtensions.getMedian(boundedAreas.values());
            LOG.warn("Taking median: {}", median);
            constructorOverlay.getItem().setCurrent(median, neighbours);
        }
    }

    @Override
    public void clicked(MouseEvent e) {
        LOG.debug("You clicked me");
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (constructorOverlay.getItem() == null) {
                return;
            }
            constructorOverlay.getItem().addCurrent();
        }
    }

    @Override
    public void cancel() {
        constructorOverlay.setBoundedAreaConstructor(null);
    }
}
