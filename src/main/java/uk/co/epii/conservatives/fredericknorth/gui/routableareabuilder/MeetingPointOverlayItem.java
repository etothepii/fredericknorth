package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractOverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 01:38
 */
public class MeetingPointOverlayItem extends AbstractOverlayItem<MeetingPoint> {

    public MeetingPointOverlayItem(MeetingPoint meetingPoint) {
        super(meetingPoint, 1000);
    }

    @Override
    public Point getGeoLocationOfCenter() {
        return getItem().getPoint();
    }

    @Override
    public Point getTopLeft(Dimension size, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        Point location = imageAndGeoPointTranslator.getImageLocation(getGeoLocationOfCenter());
        return new Point(location.x - size.width / 2, location.y - size.height / 2);
    }

    @Override
    protected int compareToSameGeneric(OverlayItem<MeetingPoint> o) {
        return o.getItem().getName().compareTo(getItem().getName());
    }
}
