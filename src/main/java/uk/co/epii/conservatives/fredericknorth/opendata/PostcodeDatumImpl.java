package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;
import java.util.Arrays;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:52
 */

class PostcodeDatumImpl implements PostcodeDatum {

    private final String postcode;
    private String wardCode;
    private Point location;
    private int[] councilBandCount;
    private int dwellingCount;

    PostcodeDatumImpl(String postcode) {
        if (postcode == null) throw new NullPointerException("The postcode is null!");
        this.postcode = postcode;
        councilBandCount = new int[9];
    }

    PostcodeDatumImpl(String postcode, String wardCode, Point location) {
        this(postcode);
        this.wardCode = wardCode;
        this.location = location;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    @Override
    public Point getPoint() {
        return location;
    }

    @Override
    public String getWardCode() {
        return wardCode;
    }

    @Override
    public int[] getCouncilBandCount() {
        return Arrays.copyOf(councilBandCount, councilBandCount.length);
    }

    @Override
    public int getDwellingCount() {
        return dwellingCount;
    }

    @Override
    public void setPoint(Point location) {
        this.location = location;
    }

    @Override
    public void addHouse(char band) {
        councilBandCount[getArrayIndexForCouncilBand(band)]++;
        dwellingCount++;
    }

    private int getArrayIndexForCouncilBand(char band) {
        int councilTaxBandIndex = band - 65;
        if (Math.abs(councilTaxBandIndex - 4) > 4) throw new IllegalArgumentException("Only bands A through I are supported: " + band);
        return councilTaxBandIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostcodeDatumImpl that = (PostcodeDatumImpl) o;

        if (!postcode.equals(that.postcode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return postcode.hashCode();
    }
}
