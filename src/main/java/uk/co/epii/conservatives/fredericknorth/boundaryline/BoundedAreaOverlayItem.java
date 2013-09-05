package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractOverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayRenderer;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 17:16
 */
public class BoundedAreaOverlayItem extends AbstractOverlayItem<BoundedArea> {

    private static final Logger LOG = LoggerFactory.getLogger(BoundedAreaOverlayItem.class);

    private Point boundsCenter;
    private Point boundsTopLeft;

    public BoundedAreaOverlayItem(BoundedArea boundedArea, int priority) {
        super(boundedArea, priority);
        Rectangle bounds = PolygonExtensions.getBounds(boundedArea.getAreas());
        boundsCenter = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        boundsTopLeft = new Point(bounds.x, bounds.y + bounds.height);
    }

    @Override
    public Point getGeoLocationOfCenter() {
        return boundsCenter;
    }

    @Override
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        return imageAndGeoPointTranslator.getImageLocation(boundsTopLeft);
    }

    @Override
    public boolean contains(Point imagePoint, ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                            OverlayRenderer<BoundedArea> overlayRenderer) {
        Point geoPoint = imageAndGeoPointTranslator.getGeoLocation(imagePoint);
        if (!PolygonExtensions.getBounds(getItem().getAreas()).contains(geoPoint)) return false;
        Component component = overlayRenderer.getOverlayRendererComponent(
                this, imageAndGeoPointTranslator, imagePoint);
        return component.contains(imagePoint);
    }

    @Override
    public boolean containedWithin(Shape geoShape) {
        outerLoop: for (Polygon area : getItem().getAreas()) {
            for (int i = 0; i < area.npoints; i++) {
                if (!geoShape.contains(area.xpoints[i], area.ypoints[i])) continue outerLoop;
            }
            return true;
        }
        return false;
    }

    @Override
    protected int compareToSameGeneric(OverlayItem<BoundedArea> o) {
        return getItem().getName().compareTo(o.getItem().getName());
    }
}
