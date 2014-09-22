package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:27
 */
public class DummyDwelling implements Location {

    private final String identifier;
    private DwellingGroup dwellingGroup;
    private Point point;

    public DummyDwelling(String identifier, DummyDwellingGroup dwellingGroup) {
        this.identifier = identifier;
        this.dwellingGroup = dwellingGroup;
    }

    public DummyDwelling(String identifier, DummyDwellingGroup dwellingGroup, Point point) {
      this(identifier, dwellingGroup);
      this.point = point;
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public Point getPoint() {
        return point;
    }
}
