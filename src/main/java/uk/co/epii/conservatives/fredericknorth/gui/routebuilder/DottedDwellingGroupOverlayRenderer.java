package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 12:58
 */
class DottedDwellingGroupOverlayRenderer extends Component implements OverlayRenderer<DwellingGroup> {

    private static Color UNSELECTED_COLOUR = Color.BLUE;
    private static Color SELECTED_COLOUR = Color.GREEN;

    private final int[] radii = new int[] {0, 2};
    private final Color[] colours = new Color[] {null, Color.WHITE};
    private int totalDiameter;
    private int totalRadii;

    @Override
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<DwellingGroup> overlayItem,
                                                 ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                 Point geoPointOfMouse, boolean selected, boolean focused) {
        setRadii(overlayItem.getItem());
        setColours(selected ? SELECTED_COLOUR : UNSELECTED_COLOUR);
        setSize(getPreferredSize());
        Point overlayCenter = imageAndGeoPointTranslator.getImageLocation(overlayItem.getGeoLocationOfCenter());
        setLocation(new Point(overlayCenter.x - totalRadii, overlayCenter.y - totalRadii));
        return new RenderedOverlay(this,
                new RenderedOverlayOvalBoundaryImpl(
                        overlayItem,
                        imageAndGeoPointTranslator.getImageLocation(overlayItem.getGeoLocationOfCenter()),
                        totalRadii), overlayItem, false);
    }

    private void setRadii(DwellingGroup dwellingGroup) {
        radii[0] = (int)Math.ceil(Math.sqrt(dwellingGroup.size()));
        totalRadii = 2 + radii[0];
        totalDiameter = totalRadii * 2;
    }

    private void setColours(Color colour) {
        this.colours[0] = colour;
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
