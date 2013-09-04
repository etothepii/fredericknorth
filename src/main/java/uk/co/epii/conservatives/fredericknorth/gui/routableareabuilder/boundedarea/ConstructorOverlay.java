package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayRenderer;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 02/08/2013
 * Time: 13:31
 */
public class ConstructorOverlay implements OverlayItem<BoundedArea> {

    private BoundedAreaConstructor boundedAreaConstructor;
    private int priority;

    public ConstructorOverlay(BoundedAreaConstructor boundedAreaConstructor, int priority) {
        this.boundedAreaConstructor = boundedAreaConstructor;
        this.priority = priority;
    }

    @Override
    public Point getGeoLocationOfCenter() {
        if (boundedAreaConstructor == null) {
            return new Point(0,0);
        }
        Rectangle bounds = PolygonExtensions.getBounds(boundedAreaConstructor.getAreas());
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    @Override
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        if (boundedAreaConstructor == null) {
            return new Point(0,0);
        }
        Rectangle bounds = PolygonExtensions.getBounds(boundedAreaConstructor.getAreas());
        return imageAndGeoPointTranslator.getImageLocation(new Point(bounds.x, bounds.y + bounds.height));
    }

    @Override
    public BoundedAreaConstructor getItem() {
        return boundedAreaConstructor;
    }

    public void setBoundedAreaConstructor(BoundedAreaConstructor boundedAreaConstructor) {
        this.boundedAreaConstructor = boundedAreaConstructor;
    }

    @Override
    public int getPriority() {
        return boundedAreaConstructor == null ? 0 : priority;
    }

    @Override
    public boolean contains(Point imagePoint, ImageAndGeoPointTranslator imageAndGeoPointTranslator,
                            OverlayRenderer<BoundedArea> overlayRenderer) {
        if (boundedAreaConstructor == null) return false;
        Point geoPoint = imageAndGeoPointTranslator.getGeoLocation(imagePoint);
        if (!PolygonExtensions.getBounds(getItem().getAreas()).contains(geoPoint)) return false;
        Component component = overlayRenderer.getOverlayRendererComponent(
                this, imageAndGeoPointTranslator, imagePoint);
        return component.contains(imagePoint);
    }

    @Override
    public boolean containedWithin(Shape geoShape) {
        for (Polygon area : getItem().getAreas()) {
            for (int i = 0; i < area.npoints; i++) {
                if (!geoShape.contains(area.xpoints[i], area.ypoints[i])) return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(OverlayItem o) {
        if (priority != o.getPriority()) return getPriority() - o.getPriority();
        if (getGeoLocationOfCenter().x != o.getGeoLocationOfCenter().x)
            return getGeoLocationOfCenter().x - o.getGeoLocationOfCenter().x;
        if (getGeoLocationOfCenter().y != o.getGeoLocationOfCenter().y)
            return getGeoLocationOfCenter().y - o.getGeoLocationOfCenter().y;
        try {
            return 0;
        }
        catch (ClassCastException cce) {
            return o.getClass().getName().compareTo(getClass().getName());
        }
    }
}
