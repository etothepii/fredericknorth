package uk.co.epii.conservatives.fredericknorth.pdf;

import uk.co.epii.conservatives.fredericknorth.maps.WeightedPoint;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 14/07/2013
 * Time: 19:54
 */
class RouteMapGrouping implements WeightedPoint {

    private final List<DwellingGroup> dwellingGroupList;
    private final Point geoLocation;
    private String commonName;
    private int count;

    public RouteMapGrouping(Point geoLocation) {
        this.geoLocation = geoLocation;
        this.dwellingGroupList = new ArrayList<DwellingGroup>();
        count = 0;
    }

    public List<? extends DwellingGroup> getDwellingGroupList() {
        return dwellingGroupList;
    }

    public Point getGeoLocation() {
        return geoLocation;
    }

    public void addDwellingGroup(DwellingGroup dwellingGroup) {
        if (!dwellingGroup.getPoint().equals(geoLocation)) {
            throw new IllegalArgumentException("The supplied Dwelling does not share a location with this grouping");
        }
        if (dwellingGroupList.isEmpty()) {
            commonName = dwellingGroup.getName();
        }
        else if (!dwellingGroup.getName().equals(commonName)) {
            commonName = null;
        }
        dwellingGroupList.add(dwellingGroup);
        count += dwellingGroup.size();
    }

    public boolean hasCommonName() {
        if (count == 0) {
            throw new IllegalStateException(
                    "One can not request the common name of a Route Map Dwelling Group when it has no dwellings");
        }
        return commonName != null;
    }

    public int size() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteMapGrouping that = (RouteMapGrouping) o;

        if (!geoLocation.equals(that.geoLocation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return geoLocation.hashCode();
    }

    public String getCommonName() {
        return commonName;
    }

    @Override
    public double getX() {
        return geoLocation.getX();
    }

    @Override
    public double getY() {
        return geoLocation.getY();
    }

    @Override
    public Point getPoint() {
        return geoLocation;
    }

    @Override
    public double getWeight() {
        return count;
    }
}
