package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 13/07/2013
 * Time: 12:35
 */
public class DummyPostcodeDatum implements PostcodeDatum {

    private final String postcode;

    public DummyPostcodeDatum(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public Point getPoint() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public String getWardCode() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public int[] getCouncilBandCount() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public int getDwellingCount() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public void addHouse(char councilTaxBand) {
    }

    @Override
    public void setWardCode(String wardCode) {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public void setPoint(Point location) {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }
}
