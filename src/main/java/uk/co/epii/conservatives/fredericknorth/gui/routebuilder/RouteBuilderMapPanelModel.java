package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractMapPanelModel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelMouseTracker;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MouseStableEvent;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MouseStableListener;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 09:39
 */
class RouteBuilderMapPanelModel extends AbstractMapPanelModel {

    private static final Logger LOG_SYNC =
            LoggerFactory.getLogger(RouteBuilderMapPanelModel.class.getName().concat("_sync"));

    private Point multiSelectionSquareDrawnFrom = null;
    private Point stablePoint = null;

    private final RouteBuilderMapFrameModel routeBuilderMapFrameModel;
    private final MapPanelMouseTracker mapPanelMouseTracker;

    public RouteBuilderMapPanelModel(RouteBuilderMapFrameModel routeBuilderMapFrameModel, long stationaryMouseRequirement) {
        super(routeBuilderMapFrameModel.getMapViewGenerator());
        this.routeBuilderMapFrameModel = routeBuilderMapFrameModel;
        mapPanelMouseTracker = new MapPanelMouseTracker(stationaryMouseRequirement);
        mapPanelMouseTracker.addMouseStableListener(new MouseStableListener() {
            @Override
            public void mouseStable(MouseStableEvent e) {
                mouseStablized(e.getStable());
            }
        });
        mapPanelMouseTracker.start();
    }

    private void selectedAreaExtendedTo(Point point) {
        ImageAndGeoPointTranslator imageAndGeoPointTranslator;
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                imageAndGeoPointTranslator = getCurrentMapView();
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        Point multiSelectionSquareDrawnFromInPanel = multiSelectionSquareDrawnFrom;
        Point geoPoint = imageAndGeoPointTranslator.getGeoLocation(point);
        setSelected(new Rectangle(
                new Point(
                        Math.min(geoPoint.x, multiSelectionSquareDrawnFromInPanel.x),
                        Math.min(geoPoint.y, multiSelectionSquareDrawnFromInPanel.y)),
                new Dimension(
                        Math.abs(geoPoint.x - multiSelectionSquareDrawnFromInPanel.x),
                        Math.abs(geoPoint.y - multiSelectionSquareDrawnFromInPanel.y))
                ));
        fireOverlaysMouseOverChanged();
    }

    void mouseStablized(Point stablePoint) {
        super.mouseMovedTo(stablePoint);
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                if (this.stablePoint != null && this.stablePoint.equals(stablePoint))
                    return;
                this.stablePoint = stablePoint;
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        fireOverlaysMouseOverChanged();
    }

    @Override
    public void doubleClicked(MouseEvent e) {
        if (getSelectedArea() == null) {
            mouseStablized(e.getPoint());
        }
        if (multiSelectionSquareDrawnFrom == null && !isMouseOverItems()) {
            multiSelectionSquareDrawnFrom = getCurrentMapView().getGeoLocation(e.getPoint());
        }
        else {
            routeBuilderMapFrameModel.invertSelectionInRoutedAndUnrouted();
            multiSelectionSquareDrawnFrom = null;
            setSelected(null);
        }
    }

    @Override
    public void clicked(MouseEvent e) {}

    @Override
    public void cancel() {
        multiSelectionSquareDrawnFrom = null;
        setSelected(null);
    }

    @Override
    public void zoomIn(Point zoomAt, double zoomBy) {
        super.zoomIn(zoomAt, zoomBy);
        if (multiSelectionSquareDrawnFrom != null) {
            selectedAreaExtendedTo(zoomAt);
        }
    }

    @Override
    public void mouseMovedTo(Point point) {
        super.mouseMovedTo(point);
        if (multiSelectionSquareDrawnFrom == null) {
            mapPanelMouseTracker.setMouseLocation(point);
        }
        else {
            selectedAreaExtendedTo(point);
        }
    }
}
