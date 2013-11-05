package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractOverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 12:40
 */
class DottedDwellingGroupOverlayItemImpl extends AbstractOverlayItem<DwellingGroup> {

    DottedDwellingGroupOverlayItemImpl(DwellingGroup dottedDwellingGroup, int priority) {
        super(dottedDwellingGroup, priority);
    }

    @Override
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        Point imageLocation = imageAndGeoPointTranslator.getImageLocation(getItem().getPoint());
        return new Point(imageLocation.x - size.width / 2,
                imageLocation.y - size.height / 2);
    }

    @Override
    public Point getGeoLocationOfCenter() {
        return getItem().getPoint();
    }

    @Override
    protected int compareToSameGeneric(OverlayItem<DwellingGroup> o) {
        return getItem().compareTo(o.getItem());
    }
}
