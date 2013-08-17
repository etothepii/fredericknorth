package uk.co.epii.conservatives.fredericknorth.maps.gui;

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
    public Component render(OverlayItem overlayItem);
    public Map<OverlayItem, MouseLocation> getImmutableOverlaysMouseOver();
    public Point getMouseAt();
    public void doubleClicked(MouseEvent e);
    public void clicked(MouseEvent e);
    public Shape getSelectedArea();
    public boolean isMouseOverItems();
    public void cancel();
    public void setUniverse(Rectangle rectangle);
    public void setUniverse(Rectangle rectangle, ProgressTracker progressTracker);
    public Rectangle getUniverse();
    public void zoomToFitUniverse(Dimension size);
}
