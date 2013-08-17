package uk.co.epii.conservatives.fredericknorth.maps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 19/07/2013
 * Time: 19:22
 */
public abstract class AbstractMapPanelModel implements MapPanelModel {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMapPanelModel.class);

    private final SortedSet<OverlayItem> overlays;
    private final Set<MapPanelDataListener> mapPanelDataListeners;
    private final Map<Class<?>, OverlayRenderer> overlayRenderers;
    private final MapViewGenerator mapViewGenerator;
    private MapView currentMapView;
    private boolean mapViewIsDirty;
    private Point geoDragFrom;
    private Point mouseAt;
    private Shape selected;
    private final Object selectionChangingSync = new Object();

    protected AbstractMapPanelModel(MapViewGenerator mapViewGenerator) {
        this.overlays = new TreeSet<OverlayItem>();
        this.mapPanelDataListeners = new HashSet<MapPanelDataListener>();
        mapViewIsDirty = true;
        overlayRenderers = new HashMap<Class<?>, OverlayRenderer>();
        this.mapViewGenerator = mapViewGenerator;
    }

    @Override
    public void addOverlay(OverlayItem overlayItem) {
        boolean fireOverlaysChanged;
        synchronized (overlays) {
            fireOverlaysChanged = overlays.add(overlayItem);
        }
        if (fireOverlaysChanged) {
            fireOverlaysChanged();
        }
    }

    @Override
    public void removeOverlay(OverlayItem overlayItem) {
        boolean fireOverlaysChanged;
        synchronized (overlays) {
            fireOverlaysChanged = overlays.remove(overlayItem);
        }
        if (fireOverlaysChanged) {
            fireOverlaysChanged();
        }
    }

    @Override
    public void clearOverlays() {
        if (overlays.isEmpty()) {
            return;
        }
        synchronized (overlays) {
            overlays.clear();
        }
        fireOverlaysChanged();
    }

    @Override
    public void addAllOverlay(Collection<? extends OverlayItem> overlayItems) {
        boolean fireOverlaysChanged = false;
        synchronized (overlays) {
            for (OverlayItem overlayItem : overlayItems) {
                fireOverlaysChanged |= overlays.add(overlayItem);
            }
        }
        if (fireOverlaysChanged) {
            fireOverlaysChanged();
        }
    }

    @Override
    public void removeAllOverlay(Collection<? extends OverlayItem> overlayItems) {
        boolean fireOverlaysChanged = false;
        synchronized (overlays) {
            for (OverlayItem overlayItem : overlayItems) {
                fireOverlaysChanged |= overlays.remove(overlayItem);
            }
        }
        if (fireOverlaysChanged) {
            fireOverlaysChanged();
        }
    }

    @Override
    public Component render(OverlayItem overlayItem) {
        return getOverlayRenderer(overlayItem.getItem().getClass())
                .getOverlayRendererComponent(overlayItem, currentMapView, mouseAt);
    }

    public OverlayRenderer getOverlayRenderer(Class<?> overlayRendererClassStartingPoint) {
        Class<?> overlayRendererClass = overlayRendererClassStartingPoint;
        while (overlayRendererClass != null && !overlayRenderers.containsKey(overlayRendererClass)) {
            overlayRendererClass = overlayRendererClass.getSuperclass();
        }
        if (overlayRendererClass == null) {
            overlayRendererClass = overlayRendererClassStartingPoint;
            for(Class<?> clazz : overlayRenderers.keySet()) {
                if (clazz.isAssignableFrom(overlayRendererClass)) {
                    overlayRendererClass = clazz;
                    break;
                }
            }
        }
        return overlayRenderers.get(overlayRendererClass);
    }

    protected void fireOverlaysChanged() {
        MapPanelDataEvent e = new MapPanelDataEvent(this);
        synchronized (mapPanelDataListeners) {
            for (MapPanelDataListener l : mapPanelDataListeners) {
                l.overlaysChanged(e);
            }
        }
    }

    @Override
    public Point getMouseAt() {
        return mouseAt;
    }

    protected void fireMapChanged() {
        MapPanelDataEvent e = new MapPanelDataEvent(this);
        synchronized (mapPanelDataListeners) {
            for (MapPanelDataListener l : mapPanelDataListeners) {
                l.mapChanged(e);
            }
        }
    }

    protected void fireUniverseChanged() {
        MapPanelDataEvent e = new MapPanelDataEvent(this);
        synchronized (mapPanelDataListeners) {
            LOG.debug("Informing {} Listeners that universe has changed", mapPanelDataListeners.size());
            for (MapPanelDataListener l : mapPanelDataListeners) {
                l.universeChanged(e);
            }
        }
    }

    protected void fireOverlaysMouseOverChanged() {
        MapPanelDataEvent e = new MapPanelDataEvent(this);
        synchronized (mapPanelDataListeners) {
            for (MapPanelDataListener l : mapPanelDataListeners) {
                l.overlaysMouseOverChanged(e);
            }
        }
    }

    @Override
    public void setOverlays(Collection<? extends OverlayItem> overlayItems) {
        synchronized (overlays) {
            if (collectionsEqual(overlayItems, overlays)) {
                return;
            }
            overlays.clear();
            overlays.addAll(overlayItems);
        }
        fireOverlaysChanged();
    }

    @Override
    public void addMapPanelDataListener(MapPanelDataListener l) {
        mapPanelDataListeners.add(l);
    }

    @Override
    public void removeMapPanelDataListener(MapPanelDataListener l) {
        mapPanelDataListeners.remove(l);
    }

    @Override
    public MapView getCurrentMapView() {
        synchronized (this) {
            if (mapViewIsDirty) {
                currentMapView = mapViewGenerator.getView();
            }
            mapViewIsDirty = false;
        }
        return currentMapView;
    }

    @Override
    public List<OverlayItem> getImmutableOverlayItems() {
        synchronized (overlays) {
            return new ArrayList<OverlayItem>(overlays);
        }
    }


    @Override
    public void setViewportSize(Dimension size) {
        boolean fireMapChange;
        synchronized (this) {
            mapViewIsDirty |= mapViewGenerator.setViewPortSize(size);
            fireMapChange = mapViewIsDirty;
        }
        if (fireMapChange) {
            fireMapChanged();
        }
    }

    @Override
    public void setScale(double scale) {
        boolean fireMapChange;
        synchronized (this) {
            mapViewIsDirty |= mapViewGenerator.setScale(scale);
            fireMapChange = mapViewIsDirty;
        }
        if (fireMapChange) {
            fireMapChanged();
        }

    }

    @Override
    public void setGeoCenter(Point geoCenter) {
        boolean fireMapChange;
        synchronized (this) {
            mapViewIsDirty |= mapViewGenerator.setGeoCenter(geoCenter);
            fireMapChange = mapViewIsDirty;
        }
        if (fireMapChange) {
            fireMapChanged();
        }
    }

    @Override
    public void zoomIn(Point zoomAt, double zoomBy) {
        boolean fireMapChange;
        synchronized (this) {
            double newScale = mapViewGenerator.getScale() * zoomBy;
            Point newGeoCenter = currentMapView.getNewGeoCenter(zoomAt, newScale);
            mapViewIsDirty |= mapViewGenerator.setGeoCenter(newGeoCenter);
            mapViewIsDirty |= mapViewGenerator.setScale(newScale);
            fireMapChange = mapViewIsDirty;
        }
        if (fireMapChange) {
            fireMapChanged();
        }
    }

    @Override
    public void setDragFrom(Point point) {
        geoDragFrom = currentMapView.getGeoLocation(point);
    }

    @Override
    public void moveDraggedFrom(Point draggedTo) {
        Point newGeoCenter = currentMapView.getNewGeoCenter(geoDragFrom, draggedTo);
        boolean fireMapChange;
        synchronized (this) {
            mapViewIsDirty |= mapViewGenerator.setGeoCenter(newGeoCenter);
            fireMapChange = mapViewIsDirty;
        }
        if (fireMapChange) {
            fireMapChanged();
        }
    }

    @Override
    public void setOverlayRenderer(Class<?> clazz, OverlayRenderer overlayRenderer) {
        this.overlayRenderers.put(clazz, overlayRenderer);
    }

    @Override
    public abstract void doubleClicked(MouseEvent e);

    @Override
    public Map<OverlayItem, MouseLocation> getImmutableOverlaysMouseOver() {
        Map<OverlayItem, MouseLocation> overlaysMouseOver;
        synchronized (overlays) {
            overlaysMouseOver = new HashMap<OverlayItem, MouseLocation>(overlays.size());
            for (OverlayItem overlayItem : overlays) {
                if (overlayItem.getItem() == null) {
                    continue;
                }
                MouseLocation mouseOverAt = mouseOverAt(overlayItem);
                if (mouseOverAt != null) {
                    overlaysMouseOver.put(overlayItem, mouseOverAt);
                }
            }
        }
        return overlaysMouseOver;
    }

    @Override
    public boolean isMouseOverItems() {
        Map<OverlayItem, MouseLocation> overlaysMouseOver;
        synchronized (overlays) {
            for (OverlayItem overlayItem : overlays) {
                MouseLocation mouseOverAt = mouseOverAt(overlayItem);
                if (mouseOverAt != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setUniverse(Rectangle rectangle) {
        setUniverse(rectangle, null);
    }

    @Override
    public void setUniverse(Rectangle rectangle, ProgressTracker progressTracker) {
        mapViewGenerator.loadUniverse(rectangle, progressTracker);
        LOG.debug("Universe loading complete");
        fireUniverseChanged();
    }

    private MouseLocation mouseOverAt(OverlayItem overlayItem) {
        OverlayRenderer renderer = getOverlayRenderer(overlayItem.getItem().getClass());
        if (!overlayItem.contains(getMouseAt(), getCurrentMapView(), renderer)) {
            return null;
        }
        return new MouseLocationImpl(
                getMouseAt(),
                renderer.getMouseGeo(),
                renderer.isMouseOnBoundary());
    }

    @Override
    public Shape getSelectedArea() {
        synchronized (selectionChangingSync) {
            return selected;
        }
    }

    @Override
    public abstract void clicked(MouseEvent e);

    public void setSelected(Shape selected) {
        synchronized (selectionChangingSync) {
            this.selected = selected;
        }
    }

    @Override
    public void mouseMovedTo(Point point) {
        if (mouseAt == null || !mouseAt.equals(point)) {
            mouseAt = point;
        }
    }

    protected static boolean collectionsEqual(Collection<? extends OverlayItem> a, Collection<? extends OverlayItem> b) {
        HashSet<OverlayItem> A = new HashSet<OverlayItem>(a);
        HashSet<OverlayItem> B = new HashSet<OverlayItem>(b);
        if (A.size() != B.size()) return false;
        for (OverlayItem overlayItem : A) {
            if (!B.contains(overlayItem)) {
                return false;
            }
        }
       return true;
    }

    @Override
    public void zoomToFitUniverse(Dimension size) {
        Rectangle universe = getUniverse();
        double scale = Math.min(size.getWidth() / universe.getWidth(), size.getHeight() / universe.getHeight());
        setGeoCenter(new Point(universe.x + universe.width / 2, universe.y + universe.height / 2));
        setScale(scale);
    }

    @Override
    public Rectangle getUniverse() {
        return mapViewGenerator.getUniverse();
    }

    private class MouseLocationImpl implements MouseLocation  {

        private final Point imageLocation;
        private final Point geoLocation;
        private final boolean mouseStuck;

        private MouseLocationImpl(Point imageLocation, Point geoLocation, boolean mouseStuck) {
            this.imageLocation = imageLocation;
            this.geoLocation = geoLocation;
            this.mouseStuck = mouseStuck;
        }

        @Override
        public Point getImageLocation() {
            return imageLocation;
        }

        @Override
        public Point getGeoLocation() {
            return geoLocation;
        }

        @Override
        public boolean isMouseStuck() {
            return mouseStuck;
        }

    }

}
