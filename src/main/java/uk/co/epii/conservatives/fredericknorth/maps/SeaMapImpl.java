package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 26/08/2013
 * Time: 10:00
 */

class SeaMapImpl implements OSMap {

    private final Point bottomLeft;
    private final OSMapType osMapType;

    public SeaMapImpl(OSMapType osMapType, Point bottomLeft) {
        this.osMapType = osMapType;
        this.bottomLeft = bottomLeft;
    }

    public Point getBottomLeftMapCoordinate() {
        return bottomLeft;
    }

    @Override
    public OSMapType getOSMapType() {
        return osMapType;
    }

    @Override
    public String getLargeSquare() {
        return "CC";
    }

    @Override
    public Integer getSquare() {
        return 0;
    }

    @Override
    public String getQuadrant() {
        return "cc";
    }

    @Override
    public Integer getSquareHundredth() {
        return 0;
    }

    @Override
    public Integer getQuadrantHundredth() {
        return 0;
    }

    @Override
    public String getMapName() {
        return "SEA";
    }

    @Override
    public String toString() {
        return "SeaMapImpl{" +
                "bottomLeft=" + bottomLeft +
                ", osMapType=" + osMapType +
                '}';
    }
}
