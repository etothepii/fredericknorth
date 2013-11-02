package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:49
 * To change this template use File | Settings | File Templates.
 */
class DwellingImpl implements Dwelling {

    private String identifier;
    private DwellingGroup dwellingGroup;
    private char councilTaxBand;
    private Point point;

    DwellingImpl(String identifier, char councilTaxBand, DwellingGroup dwellingGroup) {
        this.identifier = identifier;
        this.dwellingGroup = dwellingGroup;
        this.councilTaxBand = councilTaxBand;
    }

    @Override
    public char getCouncilTaxBand() {
        return councilTaxBand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DwellingImpl dwelling = (DwellingImpl) o;

        if (!dwellingGroup.equals(dwelling.dwellingGroup)) return false;
        if (!identifier.equals(dwelling.identifier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + dwellingGroup.hashCode();
        return result;
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public Point getPoint() {
        return point == null ? dwellingGroup.getPoint() : point;
    }
}
