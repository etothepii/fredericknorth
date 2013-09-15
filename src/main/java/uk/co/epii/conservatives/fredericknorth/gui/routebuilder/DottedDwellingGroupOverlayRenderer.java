package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 12:58
 */
class DottedDwellingGroupOverlayRenderer extends Component implements OverlayRenderer<DottedDwellingGroup> {

    private int[] radii;
    private Color[] colours;
    private int totalDiameter;
    private int totalRadii;
    private Point mouseGeo;

    @Override
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<DottedDwellingGroup> overlayItem,
                                                 ImageAndGeoPointTranslator imageAndGeoPointTranslator, Point geoPointOfMouse) {
        setRadii(overlayItem.getItem().getDot().getRadii());
        setColours(overlayItem.getItem().getDot().getColours());
        setSize(getPreferredSize());
        return new RenderedOverlay(this,
                new RenderedOverlayOvalBoundaryImpl(
                        overlayItem,
                        imageAndGeoPointTranslator.getImageLocation(overlayItem.getGeoLocationOfCenter()),
                        totalRadii), overlayItem, false);
    }

    private void setRadii(int[] radii) {
        this.radii = radii;
        totalRadii = 0;
        for (int radius : radii) {
            totalRadii += radius;
        }
        totalDiameter = totalRadii * 2;
    }

    private void setColours(Color[] colours) {
        this.colours = colours;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(totalDiameter, totalDiameter);
    }

    @Override
    public boolean contains(int x, int y) {
        x = x >= totalRadii ? x - totalRadii : totalRadii - x - 1;
        y = y >= totalRadii ? y - totalRadii : totalRadii - y - 1;
        return x * (x + 1) + y * (y + 1) < totalRadii * totalRadii;
    }

    @Override
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    @Override
    public void paint(Graphics g) {
        int indent = 0;
        for (int index = colours.length - 1; index >= 0; index--) {
            g.setColor(colours[index]);
            g.fillOval(indent, indent, totalDiameter - indent * 2, totalDiameter - indent * 2);
            indent += radii[index];
        }
    }
}
