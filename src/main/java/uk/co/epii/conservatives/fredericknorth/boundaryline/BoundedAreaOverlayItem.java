package uk.co.epii.conservatives.fredericknorth.boundaryline;

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

    private Point boundsCenter;
    private Point boundsTopLeft;

    public BoundedAreaOverlayItem(BoundedArea boundedArea, int priority) {
        super(boundedArea, priority);
        Rectangle bounds = boundedArea.getArea().getBounds();
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
        if (!getItem().getArea().getBounds().contains(geoPoint)) return false;
        Component component = overlayRenderer.getOverlayRendererComponent(
                this, imageAndGeoPointTranslator, imagePoint);
        return component.contains(imagePoint);
    }

    @Override
    public boolean containedWithin(Shape geoShape) {
        Polygon area = getItem().getArea();
        for (int i = 0; i < area.npoints; i++) {
            if (!geoShape.contains(area.xpoints[i], area.ypoints[i])) return false;
        }
        return true;
    }

    @Override
    protected int compareToSameGeneric(OverlayItem<BoundedArea> o) {
        return getItem().getName().compareTo(o.getItem().getName());
    }
}
