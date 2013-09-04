package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;

import java.awt.*;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 13:29
 */
class BoundedAreaConstructorOverlayRenderer extends BoundedAreaOverlayRenderer {

    public BoundedAreaConstructorOverlayRenderer(Map<BoundedAreaType, Color> color) {
        super(color);
    }

    protected void paint(Graphics2D g) {
        Polygon[] polygons = getPolygons();
        for (Polygon polygon : polygons) {
            if (polygon.npoints >= 3) {
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 64));
                g.fillPolygon(polygon);
            }
        }
        g.setColor(color);
        Stroke original = g.getStroke();
        g.setStroke(new BasicStroke(4));
        for (Polygon polygon : polygons) {
            if (polygon.npoints > 1) {
                g.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
            }
            else if (polygon.npoints == 1) {
                g.drawLine(polygon.xpoints[0], polygon.ypoints[0], polygon.xpoints[0], polygon.ypoints[0]);
            }
        }
        g.setStroke(original);
    }
}
