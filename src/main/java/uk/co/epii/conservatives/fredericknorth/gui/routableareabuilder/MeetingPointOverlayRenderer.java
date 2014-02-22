package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 01:46
 */
public class MeetingPointOverlayRenderer extends Component implements OverlayRenderer<MeetingPoint> {

    private static final Color[] UNSELECTED_COLOUR = new Color[] {Color.BLACK};
    private static final Color[] SELECTED_COLOUR = new Color[] {Color.WHITE, Color.RED, Color.WHITE, Color.RED, Color.WHITE, Color.RED, Color.WHITE, Color.RED};
    private static final int[] UNSELECTED_RADII = new int[] {10};
    private static final int[] SELECTED_RADII = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

    private int[] radii;
    private Color[] colours;
    private int totalDiameter;
    private int totalRadii;

    @Override
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<MeetingPoint> overlayItem,
                                                       ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                       Point mouseGeoLocation, boolean selected, boolean inFocus) {
        setRadii(selected ? SELECTED_RADII : UNSELECTED_RADII);
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
        return new Dimension(totalDiameter, totalDiameter + (radii[0] == 1 ? 1 : 0));
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
        executePaint(g);
        if (radii[0] == 1) {
            g.setClip(0, totalRadii + 1, totalDiameter, totalRadii);
            g.translate(0, 1);
            executePaint(g);
        }
    }

    private void executePaint(Graphics g) {
        int indent = 0;
        for (int index = colours.length - 1; index >= 0; index--) {
            g.setColor(colours[index]);
            g.fillOval(indent, indent, totalDiameter - indent * 2, totalDiameter - indent * 2);
            indent += radii[index];
        }
    }
}
