package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
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
class BuilderMapPanelModel extends AbstractMapPanelModel {

    private static final Logger LOG = LoggerFactory.getLogger(BuilderMapPanelModel.class);

    private final BuilderMapFrameModel builderMapFrameModel;
    private final ConstructorOverlay constructorOverlay;

    BuilderMapPanelModel(MapViewGenerator mapViewGenerator, BuilderMapFrameModel builderMapFrameModel, ConstructorOverlay constructorOverlay) {
        super(mapViewGenerator);
        this.builderMapFrameModel = builderMapFrameModel;
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
        setOverlayRenderer(BoundedArea.class, BoundedAreaExtensions.getOverlayRenderer(colorMap));
        setOverlayRenderer(BoundedAreaConstructor.class, BoundedAreaExtensions.getConstructorOverlayRenderer(colorMap));
    }

    @Override
    public void doubleClicked(MouseEvent e) {
        LOG.debug("You double clicked me");
        if (e.getButton() == MouseEvent.BUTTON1) {
            builderMapFrameModel.getMapPanelModel().removeOverlay(constructorOverlay);
            builderMapFrameModel.getBoundedAreaSelectionModel().add(
                    constructorOverlay.getItem().getParent(), constructorOverlay.getItem().lockDown());
            constructorOverlay.setBoundedAreaConstructor(null);
        }
    }

    @Override
    public void clicked(MouseEvent e) {
        LOG.debug("You clicked me");
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (constructorOverlay.getItem() == null) return;
            Map<BoundedArea, Point> boundedAreas =
                    new HashMap<BoundedArea, Point>();
            int priority = Integer.MIN_VALUE;
            Map<OverlayItem, MouseLocation> itemsMouseOver =
                    builderMapFrameModel.getMapPanelModel().getImmutableOverlaysMouseOver();
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
                if (priority == overlay.getPriority() ) {
                    boundedAreas.put(overlay.getItem(), mouseLocation.getGeoLocation());
                }
                else if (overlay.getPriority() > priority && overlay.getItem() != constructorOverlay.getItem()) {
                    priority = overlay.getPriority();
                    boundedAreas.clear();
                    boundedAreas.put(overlay.getItem(), mouseLocation.getGeoLocation());
                }
            }
            if (boundedAreas.isEmpty()) {
                constructorOverlay.getItem().add(
                        builderMapFrameModel.getMapPanelModel().getCurrentMapView().getGeoLocation(e.getPoint()),
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
            if (points.size() == 1) {
                constructorOverlay.getItem().add(points.iterator().next(), neighbours);
            }
            else {
                LOG.warn("There are too many points: {}", points);
                LOG.warn("Taking median");
                constructorOverlay.getItem().add(PointExtensions.getMedian(boundedAreas.values()), neighbours);
            }
        }
    }

    @Override
    public void cancel() {
        constructorOverlay.setBoundedAreaConstructor(null);
    }
}
