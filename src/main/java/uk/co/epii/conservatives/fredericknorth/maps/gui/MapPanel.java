package uk.co.epii.conservatives.fredericknorth.maps.gui;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

/**
 * User: James Robinson
 * Date: 27/06/2013
 * Time: 18:04
 */
public class MapPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

    private static final Logger LOG = Logger.getLogger(MapPanel.class);

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
        LOG.debug("Painting");
        mapPanelModel.setViewportSize(getSize());
        LOG.debug("Drawing map");
        MapView currentMapView = mapPanelModel.getCurrentMapView();
        g.drawImage(currentMapView.getMap(), 0, 0, this);
        for(OverlayItem overlayItem : mapPanelModel.getImmutableOverlayItems()) {
            if (overlayItem.getItem() == null) {
                continue;
            }
            Component renderedOverlay = mapPanelModel.render(overlayItem);
            Dimension renderedSize = renderedOverlay.getSize();
            Point drawFrom = overlayItem.getTopLeft(renderedSize, currentMapView);
            drawFrom = new Point(
                    drawFrom.x + renderedOverlay.getLocation().x,
                    drawFrom.y + renderedOverlay.getLocation().y);
            renderedOverlay.paint(g.create(drawFrom.x, drawFrom.y, renderedSize.width, renderedSize.height));
        }
        Shape selected = mapPanelModel.getSelectedArea();
        if (selected != null) {
            g.setTransform(mapPanelModel.getCurrentMapView().getGeoTransform());
            g.setColor(new Color(0, 0, 255, 32));
            g.fill(selected);
            g.setColor(Color.BLUE);
            g.draw(selected);
            g.setTransform(AffineTransform.getScaleInstance(1d, 1d));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isEnabled()) return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            synchronized (this) {
                mapPanelModel.moveDraggedFrom(e.getPoint());
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!isEnabled()) return;
        mapPanelModel.mouseMovedTo(e.getPoint());
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!isEnabled()) return;
        synchronized (this) {
            mapPanelModel.zoomIn(e.getPoint(), Math.pow(zoomRate, e.getUnitsToScroll()));
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isEnabled()) return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            synchronized (this) {
                switch (e.getClickCount()) {
                    case 1:
                        mapPanelModel.clicked(e);
                        break;
                    case 2:
                        mapPanelModel.doubleClicked(e);
                        break;
                }
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEnabled()) return;
        requestFocus();
        if (e.getButton() == MouseEvent.BUTTON1) {
            synchronized (this) {
                mapPanelModel.setDragFrom(e.getPoint());
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
