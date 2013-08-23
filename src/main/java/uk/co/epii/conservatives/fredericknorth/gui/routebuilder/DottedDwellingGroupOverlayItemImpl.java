package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractOverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 12:40
 */
class DottedDwellingGroupOverlayItemImpl extends AbstractOverlayItem<DottedDwellingGroup> {

    DottedDwellingGroupOverlayItemImpl(DottedDwellingGroup dottedDwellingGroup, int priority) {
        super(dottedDwellingGroup, priority);
    }

    @Override
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        Point imageLocation = imageAndGeoPointTranslator.getImageLocation(getItem().getDwellingGroup().getPoint());
        return new Point(imageLocation.x - size.width / 2,
                imageLocation.y - size.height / 2);
    }

    @Override
    public Point getGeoLocationOfCenter() {
        return getItem().getDwellingGroup().getPoint();
    }

    @Override
    public boolean containedWithin(Shape geoShape) {
        return geoShape.contains(getGeoLocationOfCenter());
    }

    @Override
    protected int compareToSameGeneric(OverlayItem<DottedDwellingGroup> o) {
        int comparison = getItem().getDwellingGroup().getPostcode().getPostcode().compareTo(
                o.getItem().getDwellingGroup().getPostcode().getPostcode());
        if (comparison != 0) return comparison;
        return getItem().getDwellingGroup().getName().compareTo(
                o.getItem().getDwellingGroup().getName());
    }
}
