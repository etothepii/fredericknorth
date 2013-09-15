package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.maps.MapImageObserver;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 09:41
 */
public interface MapPanelModel {

    public void addOverlay(OverlayItem overlayItem);
    public void removeOverlay(OverlayItem overlayItem);
    public void clearOverlays();
    public void addAllOverlay(Collection<? extends OverlayItem> overlayItems);
    public void removeAllOverlay(Collection<? extends OverlayItem> overlayItems);
    public void setOverlays(Collection<? extends OverlayItem> overlayItems);
    public void addMapPanelDataListener(MapPanelDataListener l);
    public void removeMapPanelDataListener(MapPanelDataListener l);
    public MapView getCurrentMapView();
    public List<OverlayItem> getImmutableOverlayItems();
    public void setViewportSize(Dimension size);
    public void setScale(double scale);
    public void setGeoCenter(Point geoCenter);
    public void zoomIn(Point point, double pow);
    public void setDragFrom(Point point);
    public void moveDraggedFrom(Point point);
    public void mouseMovedTo(Point point);
    public void setOverlayRenderer(Class<?> clazz, OverlayRenderer overlayRenderer);
    public RenderedOverlay render(MapPanel mapPanel, OverlayItem overlayItem);
    public Map<OverlayItem, MouseLocation> getImmutableOverlaysMouseOver();
    public void setRenderedOverlays(Collection<? extends RenderedOverlay> renderedOverlays);
    public Point getMouseAt();
    public void doubleClicked(MouseEvent e);
    public void clicked(MouseEvent e);
    public Shape getSelectedArea();
    public boolean isMouseOverItems();
    public void cancel();
    public void setProgressTracker(ProgressTracker progressTracker);
    public void display(Rectangle rectangle);
    public void setMapImageObserver(MapImageObserver mapImageObserver);
    public Collection<Rectangle> getRepaintAreas(MapPanel mapPanel);
    public void monitorRepaintAreas();
    public void setMapPanel(MapPanel mapPanel);
}
