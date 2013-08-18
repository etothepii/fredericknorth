package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 24/06/2013
 * Time: 08:51
 */
class OSMapImpl implements OSMap {

    private final OSMapType osMapType;
    private final String quadrant;
    private final String largeSquare;
    private final Integer square;
    private final Integer squareHundredth;
    private final Integer quadrantHundredth;

    OSMapImpl(OSMapType osMapType, String largeSquare, Integer square, String quadrant,
              Integer squareHundredth, Integer quadrantHundredth) {
        this.osMapType = osMapType;
        this.quadrant = quadrant;
        this.largeSquare = largeSquare;
        this.square = square;
        this.squareHundredth = squareHundredth;
        this.quadrantHundredth = quadrantHundredth;
    }

    @Override
    public OSMapType getOSMapType() {
        return osMapType;
    }

    @Override
    public String getLargeSquare() {
        return largeSquare;
    }

    @Override
    public Integer getSquare() {
        return square;
    }

    @Override
    public String getQuadrant() {
        return quadrant;
    }


    @Override
    public Integer getSquareHundredth() {
        return squareHundredth;
    }

    @Override
    public Integer getQuadrantHundredth() {
        return quadrantHundredth;
    }

    @Override
    public String getMapName() {
        StringBuilder stringBuilder = new StringBuilder(16);
        stringBuilder.append(largeSquare);
        if (square != null) stringBuilder.append(String.format("%02d", square));
        if (quadrant != null) stringBuilder.append(quadrant);
        if (quadrantHundredth != null) stringBuilder.append(String.format("%02d", quadrantHundredth));
        if (squareHundredth != null) stringBuilder.append(String.format("%02d", squareHundredth));
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OSMapImpl osMap = (OSMapImpl) o;

        if (!largeSquare.equals(osMap.largeSquare)) return false;
        if (osMapType != osMap.osMapType) return false;
        if (quadrant != null ? !quadrant.equals(osMap.quadrant) : osMap.quadrant != null) return false;
        if (quadrantHundredth != null ? !quadrantHundredth.equals(osMap.quadrantHundredth) : osMap.quadrantHundredth != null)
            return false;
        if (square != null ? !square.equals(osMap.square) : osMap.square != null) return false;
        if (squareHundredth != null ? !squareHundredth.equals(osMap.squareHundredth) : osMap.squareHundredth != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = osMapType.hashCode();
        result = 31 * result + (quadrant != null ? quadrant.hashCode() : 0);
        result = 31 * result + largeSquare.hashCode();
        result = 31 * result + (square != null ? square.hashCode() : 0);
        result = 31 * result + (squareHundredth != null ? squareHundredth.hashCode() : 0);
        result = 31 * result + (quadrantHundredth != null ? quadrantHundredth.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OSMapImpl{" +
                "osMapType=" + osMapType +
                ", quadrant='" + quadrant + '\'' +
                ", largeSquare='" + largeSquare + '\'' +
                ", square=" + square +
                ", squareHundredth=" + squareHundredth +
                ", quadrantHundredth=" + quadrantHundredth +
                '}';
    }
}
