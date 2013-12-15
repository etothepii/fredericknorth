package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:52
 */

public class PostcodeDatumImpl implements PostcodeDatum {

    private final String postcode;
    private Point location;
    private int[] councilBandCount;
    private List<Dwelling> dwellings;
    private Point point;

    public PostcodeDatumImpl(String postcode) {
        if (postcode == null) throw new NullPointerException("The postcode is null!");
        this.postcode = postcode;
        councilBandCount = new int[9];
        this.dwellings = new ArrayList<Dwelling>();
    }

    public PostcodeDatumImpl(String postcode, Point location) {
        this(postcode);
        this.location = location;
    }

    @Override
    public String getName() {
        return postcode;
    }

    @Override
    public Point getPoint() {
        return location;
    }

    @Override
    public int[] getCouncilBandCount() {
        return Arrays.copyOf(councilBandCount, councilBandCount.length);
    }

    @Override
    public Iterable<Dwelling> getDwellings() {
        return dwellings;
    }

    @Override
    public int size() {
        return dwellings.size();
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

    public void add(Dwelling dwelling) {
        dwellings.add(dwelling);
        char band = dwelling.getCouncilTaxBand();
        councilBandCount[getArrayIndexForCouncilBand(band)]++;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
