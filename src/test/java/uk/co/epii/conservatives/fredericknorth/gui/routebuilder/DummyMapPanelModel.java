package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.MapImageObserver;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 07/09/2013
 * Time: 10:35
 */
public class DummyMapPanelModel implements MapPanelModel {
    private MapPanel mapPanel;

    @Override
    public void addOverlay(OverlayItem overlayItem) {}

    @Override
    public void removeOverlay(OverlayItem overlayItem) {}

    @Override
    public void clearOverlays() {}

    @Override
    public void addAllOverlay(Collection<? extends OverlayItem> overlayItems) {}

    @Override
    public void removeAllOverlay(Collection<? extends OverlayItem> overlayItems) {}

    @Override
    public void setOverlays(Collection<? extends OverlayItem> overlayItems) {}

    @Override
    public void addMapPanelDataListener(MapPanelDataListener l) {}

    @Override
    public void removeMapPanelDataListener(MapPanelDataListener l) {}

    @Override
    public MapView getCurrentMapView() {
        return null;
    }

    @Override
    public List<OverlayItem> getImmutableOverlayItems() {
        return null;
    }

    @Override
    public void setViewportSize(Dimension size) {}

    @Override
    public void setScale(double scale) {}

    @Override
    public void setGeoCenter(Point geoCenter) {}

    @Override
    public void zoomIn(Point point, double pow) {}

    @Override
    public void setDragFrom(Point point) {}

    @Override
    public void moveDraggedFrom(Point point) {}

    @Override
    public void mouseMovedTo(Point point) {}

    @Override
    public void setOverlayRenderer(Class<?> clazz, OverlayRenderer overlayRenderer) {}

    @Override
    public RenderedOverlay render(MapPanel mapPanel, OverlayItem overlayItem) {
        return null;
    }

    @Override
    public Map<OverlayItem, MouseLocation> getImmutableOverlaysMouseOver() {
        return null;
    }

    @Override
    public void setRenderedOverlays(Collection<? extends RenderedOverlay> renderedOverlays) {}

    @Override
    public Point getMouseAt() {
        return null;
    }

    @Override
    public void doubleClicked(MouseEvent e) {}

    @Override
    public void clicked(MouseEvent e) {}

    @Override
    public Rectangle getSelectedArea() {
        return null;
    }

    @Override
    public boolean isMouseOverItems() {
        return false;
    }

    @Override
    public void cancel() {}

    @Override
    public void setProgressTracker(ProgressTracker progressTracker) {}

    @Override
    public void display(Rectangle rectangle) {}

    @Override
    public void setMapImageObserver(MapImageObserver mapImageObserver) {}

    @Override
    public Collection<Rectangle> getRepaintAreas(MapPanel mapPanel) {
        return Arrays.asList(new Rectangle[] {new Rectangle(mapPanel.getSize())});
    }

    @Override
    public void monitorRepaintAreas() {}

    @Override
    public void setMapPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    @Override
    public boolean hasFocus() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void select(OverlayItem overlayItem) {}

    @Override
    public void deselect(OverlayItem overlayItem) {}
}
