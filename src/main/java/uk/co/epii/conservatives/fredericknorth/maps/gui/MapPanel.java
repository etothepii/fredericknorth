package uk.co.epii.conservatives.fredericknorth.maps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 27/06/2013
 * Time: 18:04
 */
public class MapPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

    private static final Logger LOG = LoggerFactory.getLogger(MapPanel.class);
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(MapPanel.class.getName().concat("_sync"));
    private static final Logger LOG_PAINT = LoggerFactory.getLogger(MapPanel.class.getName().concat("_paint"));

    private final MapPanelModel mapPanelModel;
    private final double zoomRate;

    public MapPanel(MapPanelModel mapPanelModel, double zoomRate) {
        this.mapPanelModel = mapPanelModel;
        this.zoomRate = zoomRate;
        setFocusable(true);
        addKeyListener(this);
        addMouseEvents();
    }

    private void addMouseEvents() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paint(Graphics g) {
        paint((Graphics2D) g);
    }

    public void paint(Graphics2D g) {
        LOG_PAINT.debug("Painting");
        mapPanelModel.setViewportSize(getSize());
        LOG_PAINT.debug("Drawing map");
        MapView currentMapView = mapPanelModel.getCurrentMapView();
        LOG_PAINT.debug("Got currentMapView");
        g.drawImage(currentMapView.getMap(), 0, 0, this);
        LOG_PAINT.debug("Drawn Image");
        List<OverlayItem> overlays = mapPanelModel.getImmutableOverlayItems();
        List<RenderedOverlayBoundary> boundaries = new ArrayList<RenderedOverlayBoundary>(overlays.size());
        for(OverlayItem overlayItem : overlays) {
            if (overlayItem.getItem() == null) {
                continue;
            }
            RenderedOverlay renderedOverlay = mapPanelModel.render(this, overlayItem);
            if (renderedOverlay.getBoundary() == null) {
                LOG_PAINT.debug("No RenderedOverlayBoundary returned for {}", overlayItem.getItem().toString());
                continue;
            }
            Dimension renderedSize = renderedOverlay.getComponent().getSize();
            Point location = renderedOverlay.getComponent().getLocation();
            renderedOverlay.getComponent().paint(g.create(location.x, location.y, renderedSize.width, renderedSize.height));
            boundaries.add(renderedOverlay.getBoundary());
        }
        LOG_PAINT.debug("Drawn Overlays");
        mapPanelModel.setRenderedOverlayBoundaries(boundaries);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isEnabled()) return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            LOG_SYNC.debug("Awaiting this");
            try {
                synchronized (this) {
                    LOG_SYNC.debug("Received this");
                    mapPanelModel.moveDraggedFrom(e.getPoint());
                }
            }
            finally {
                LOG_SYNC.debug("Released this");
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        LOG.debug("mouseMovedTo: {}", e.getPoint());
        if (!isEnabled()) return;
        mapPanelModel.mouseMovedTo(e.getPoint());
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!isEnabled()) return;
        LOG_SYNC.debug("Awaiting this");
        try {
            synchronized (this) {
                LOG_SYNC.debug("Received this");
                mapPanelModel.zoomIn(e.getPoint(), Math.pow(zoomRate, e.getUnitsToScroll()));
            }
        }
        finally {
            LOG_SYNC.debug("Released this");
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isEnabled()) return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            LOG_SYNC.debug("Awaiting this");
            try {
                synchronized (this) {
                    LOG_SYNC.debug("Received this");
                    switch (e.getClickCount()) {
                        case 1:
                            mapPanelModel.clicked(e);
                            break;
                        case 2:
                            mapPanelModel.doubleClicked(e);
                            break;
                    }
                }
            }
            finally {
                LOG_SYNC.debug("Released this");
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) return;
        requestFocus();
        if (e.getButton() == MouseEvent.BUTTON1) {
            LOG_SYNC.debug("Awaiting this");
            try {
                synchronized (this) {
                    LOG_SYNC.debug("Received this");
                    mapPanelModel.setDragFrom(e.getPoint());
                }
            }
            finally {
                LOG_SYNC.debug("Released this");
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isEnabled()) return;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            mapPanelModel.cancel();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!isEnabled()) return;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!isEnabled()) return;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!isEnabled()) return;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!isEnabled()) return;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!isEnabled()) return;
    }
}
