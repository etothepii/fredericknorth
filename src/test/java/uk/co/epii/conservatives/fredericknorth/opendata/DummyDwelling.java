package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:27
 */
public class DummyDwelling implements Dwelling {

    private final String identifier;
    private DwellingGroup dwellingGroup;
    private Point point;

    public DummyDwelling(String identifier, DummyDwellingGroup dwellingGroup) {
        this.identifier = identifier;
        this.dwellingGroup = dwellingGroup;
    }

    @Override
    public char getCouncilTaxBand() {
        return 'A';
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
