package uk.co.epii.conservatives.fredericknorth.maps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapImageObserver;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
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
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(AbstractMapPanelModel.class.getName().concat("_sync"));

    private final SortedSet<OverlayItem> overlays;
    private final Set<MapPanelDataListener> mapPanelDataListeners;
    private final Map<Class<?>, OverlayRenderer> overlayRenderers;
    private final MapViewGenerator mapViewGenerator;
    private MapView currentMapView;
    private boolean mapViewIsDirty;
    private Point geoDragFrom;
    private Point mouseAt;
    private Shape selected;
    private ProgressTracker progressTracker;
    private final Object selectionChangingSync = new Object();
    private MapImageObserver mapImageObserver;

    protected AbstractMapPanelModel(MapViewGenerator mapViewGenerator) {
        this.progressTracker = NullProgressTracker.NULL;
        this.overlays = new TreeSet<OverlayItem>();
        this.mapPanelDataListeners = new HashSet<MapPanelDataListener>();
        mapViewIsDirty = true;
        overlayRenderers = new HashMap<Class<?>, OverlayRenderer>();
        this.mapViewGenerator = mapViewGenerator;
        mapImageObserver = null;
    }

    @Override
    public void setMapImageObserver(MapImageObserver mapImageObserver) {
        this.mapImageObserver = mapImageObserver;
    }

    @Override
    public void addOverlay(OverlayItem overlayItem) {
        boolean fireOverlaysChanged;
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                fireOverlaysChanged = overlays.add(overlayItem);
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
        }
        if (fireOverlaysChanged) {
            fireOverlaysChanged();
        }
    }

    @Override
    public void removeOverlay(OverlayItem overlayItem) {
        boolean fireOverlaysChanged;
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                fireOverlaysChanged = overlays.remove(overlayItem);
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
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
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                overlays.clear();
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
        }
        fireOverlaysChanged();
    }

    @Override
    public void addAllOverlay(Collection<? extends OverlayItem> overlayItems) {
        boolean fireOverlaysChanged = false;
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                for (OverlayItem overlayItem : overlayItems) {
                    fireOverlaysChanged |= overlays.add(overlayItem);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
        }
        if (fireOverlaysChanged) {
            fireOverlaysChanged();
        }
    }

    @Override
    public void removeAllOverlay(Collection<? extends OverlayItem> overlayItems) {
        boolean fireOverlaysChanged = false;
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                for (OverlayItem overlayItem : overlayItems) {
                    fireOverlaysChanged |= overlays.remove(overlayItem);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
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
        LOG_SYNC.debug("Awaiting mapPanelDataListeners");
        try {
            synchronized (mapPanelDataListeners) {
                LOG_SYNC.debug("Received mapPanelDataListeners");
                for (MapPanelDataListener l : mapPanelDataListeners) {
                    l.overlaysChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released mapPanelDataListeners");
        }
    }

    @Override
    public Point getMouseAt() {
        return mouseAt;
    }

    protected void fireMapChanged() {
        MapPanelDataEvent e = new MapPanelDataEvent(this);
        LOG_SYNC.debug("Awaiting mapPanelDataListeners");
        try {
            synchronized (mapPanelDataListeners) {
                LOG_SYNC.debug("Received mapPanelDataListeners");
                for (MapPanelDataListener l : mapPanelDataListeners) {
                    l.mapChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released mapPanelDataListeners");
        }
    }

    protected void fireOverlaysMouseOverChanged() {
        MapPanelDataEvent e = new MapPanelDataEvent(this);
        LOG_SYNC.debug("Awaiting mapPanelDataListeners");
        try {
            synchronized (mapPanelDataListeners) {
                LOG_SYNC.debug("Received mapPanelDataListeners");
                for (MapPanelDataListener l : mapPanelDataListeners) {
                    l.overlaysMouseOverChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released mapPanelDataListeners");
        }
    }

    @Override
    public void setOverlays(Collection<? extends OverlayItem> overlayItems) {
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                if (collectionsEqual(overlayItems, overlays)) {
                    return;
                }
                overlays.clear();
                overlays.addAll(overlayItems);
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
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
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
//                if (mapViewIsDirty) {
                    currentMapView = mapViewGenerator.getView();
//                }
                mapViewIsDirty = false;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        return currentMapView;
    }

    @Override
    public List<OverlayItem> getImmutableOverlayItems() {
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                return new ArrayList<OverlayItem>(overlays);
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
        }
    }


    @Override
    public void setViewportSize(Dimension size) {
        boolean fireMapChange;
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                mapViewIsDirty |= mapViewGenerator.setViewPortSize(size, progressTracker, mapImageObserver);
                fireMapChange = mapViewIsDirty;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        if (fireMapChange) {
            fireMapChanged();
        }
    }

    @Override
    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
    }

    @Override
    public void setScale(double scale) {
        boolean fireMapChange;
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                mapViewIsDirty |= mapViewGenerator.setScale(scale, progressTracker, mapImageObserver);
                fireMapChange = mapViewIsDirty;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        if (fireMapChange) {
            fireMapChanged();
        }

    }

    @Override
    public void setGeoCenter(Point geoCenter) {
        boolean fireMapChange;
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                mapViewIsDirty |= mapViewGenerator.setGeoCenter(geoCenter, progressTracker, mapImageObserver);
                fireMapChange = mapViewIsDirty;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        if (fireMapChange) {
            fireMapChanged();
        }
    }

    @Override
    public void zoomIn(Point zoomAt, double zoomBy) {
        boolean fireMapChange;
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                double newScale = mapViewGenerator.getScale() * zoomBy;
                Point newGeoCenter = currentMapView.getNewGeoCenter(zoomAt, newScale);
                mapViewIsDirty |= mapViewGenerator.setScaleAndCenter(newScale, newGeoCenter, progressTracker, mapImageObserver);
                fireMapChange = mapViewIsDirty;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
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
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                mapViewIsDirty |= mapViewGenerator.setGeoCenter(newGeoCenter, progressTracker, mapImageObserver);
                fireMapChange = mapViewIsDirty;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        if (fireMapChange) {
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
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
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
        }
        finally {
            LOG_SYNC.debug("Released overlays");
        }
        return overlaysMouseOver;
    }

    @Override
    public boolean isMouseOverItems() {
        Map<OverlayItem, MouseLocation> overlaysMouseOver;
        LOG_SYNC.debug("Awaiting overlays");
        try {
            synchronized (overlays) {
                LOG_SYNC.debug("Received overlays");
                for (OverlayItem overlayItem : overlays) {
                    MouseLocation mouseOverAt = mouseOverAt(overlayItem);
                    if (mouseOverAt != null) {
                        return true;
                    }
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released overlays");
        }
        return false;
    }

    @Override
    public void display(Rectangle rectangle) {
        mapViewGenerator.scaleToFitRectangle(rectangle, progressTracker, mapImageObserver);
        LOG.debug("Universe loading complete");
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
        LOG_SYNC.debug("Awaiting selectionChangingSync");
        try {
            synchronized (selectionChangingSync) {
                LOG_SYNC.debug("Received selectionChangingSync");
                return selected;
            }
        }
        finally {
            LOG_SYNC.debug("Released selectionChangingSync");
        }
    }

    @Override
    public abstract void clicked(MouseEvent e);

    public void setSelected(Shape selected) {
        LOG_SYNC.debug("Awaiting selectionChangingSync");
        try {
            synchronized (selectionChangingSync) {
                LOG_SYNC.debug("Received selectionChangingSync");
                this.selected = selected;
            }
        }
        finally {
            LOG_SYNC.debug("Released selectionChangingSync");
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
