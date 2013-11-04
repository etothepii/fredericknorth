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
    public String getName() {
        return postcode;
    }

    @Override
    public Point getPoint() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public Iterable<Dwelling> getDwellings() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public int[] getCouncilBandCount() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }
}
