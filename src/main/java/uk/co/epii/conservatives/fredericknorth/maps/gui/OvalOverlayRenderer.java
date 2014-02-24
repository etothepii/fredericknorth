package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 13:07
 */
public abstract class OvalOverlayRenderer<T> extends Component implements OverlayRenderer<T> {

    private int[] radii;
    private Color[] colours;
    private int totalDiameter;
    private int totalRadii;

    @Override
    public RenderedOverlay getOverlayRendererComponent(MapPanel mapPanel, OverlayItem<T> overlayItem,
                                                       ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                                                       Point mouseGeoLocation, boolean selected, boolean inFocus) {
        setRadii(getRadii(overlayItem, selected));
        setColours(getColours(overlayItem, selected));
        setSize(getPreferredSize());
        Point overlayCenter = imageAndGeoPointTranslator.getImageLocation(overlayItem.getGeoLocationOfCenter());
        setLocation(new Point(overlayCenter.x - totalRadii, overlayCenter.y - totalRadii));
        return new RenderedOverlay(this,
                new RenderedOverlayOvalBoundaryImpl(
                        overlayItem,
                        imageAndGeoPointTranslator.getImageLocation(overlayItem.getGeoLocationOfCenter()),
                        totalRadii), overlayItem, false);
    }

    protected abstract Color[] getColours(OverlayItem<T> overlayItem, boolean selected);

    protected abstract int[] getRadii(OverlayItem<T> overlayItem, boolean selected);

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
