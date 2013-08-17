package uk.co.epii.conservatives.fredericknorth.maps;

/**
 * User: James Robinson
 * Date: 24/06/2013
 * Time: 08:51
 */
class OSMapImpl implements OSMap {

    private String quadrant;
    private String largeSquare;
    private int square;

    OSMapImpl(String largeSquare, int square, String quadrant) {
        this.quadrant = quadrant;
        this.largeSquare = largeSquare;
        this.square = square;
    }

    @Override
    public String getLargeSquare() {
        return largeSquare;
    }

    @Override
    public int getSquare() {
        return square;
    }

    @Override
    public String getQuadrant() {
        return quadrant;
    }

    @Override
    public String getMapName() {
        StringBuilder stringBuilder = new StringBuilder(6);
        stringBuilder.append(largeSquare);
        stringBuilder.append(square);
        stringBuilder.append(quadrant);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OSMapImpl osMap = (OSMapImpl) o;

        if (square != osMap.square) return false;
        if (!largeSquare.equals(osMap.largeSquare)) return false;
        if (quadrant.equals(osMap.quadrant)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = quadrant.hashCode();
        result = 31 * result + largeSquare.hashCode();
        result = 31 * result + square;
        return result;
    }

    @Override
    public String toString() {
        return "OSMapImpl{" +
                "quadrant='" + quadrant + '\'' +
                ", largeSquare='" + largeSquare + '\'' +
                ", square=" + square +
                '}';
    }
}
