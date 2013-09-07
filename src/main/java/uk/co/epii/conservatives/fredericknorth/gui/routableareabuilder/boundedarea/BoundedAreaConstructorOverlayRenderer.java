package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;

import java.awt.*;
import java.util.Map;
import java.util.List;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 13:29
 */
class BoundedAreaConstructorOverlayRenderer extends BoundedAreaOverlayRenderer<BoundedAreaConstructor> {

    public BoundedAreaConstructorOverlayRenderer(Map<BoundedAreaType, Color> colors) {
        super(colors);
    }

    @Override
    protected void processMouseLocation(OverlayItem<BoundedAreaConstructor> overlayItem,
                                        ImageAndGeoPointTranslator imageAndGeoPointTranslator, Point mouseLocation) {
        // Do nothing with the mouse
    }

    @Override
    protected Polygon[] deriveGeoPolygons(OverlayItem<BoundedAreaConstructor> overlayItem) {
        return new Polygon[] {PolygonExtensions.construct(overlayItem.getItem().getPointsToDraw())};
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
