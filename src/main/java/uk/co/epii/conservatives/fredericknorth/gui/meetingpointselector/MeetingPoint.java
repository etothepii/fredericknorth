package uk.co.epii.conservatives.fredericknorth.gui.meetingpointselector;

import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 00:17
 */
class MeetingPoint implements Location {

    private String name;
    private Point point;

    public MeetingPoint() {}

    public MeetingPoint(String name, Point point) {
        this.name = name;
        this.point = point;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
