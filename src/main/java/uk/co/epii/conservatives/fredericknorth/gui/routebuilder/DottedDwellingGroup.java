package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.Dot;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 18:55
 */
class DottedDwellingGroup {

    private final DwellingGroup dwellingGroup;
    private final Dot dot;

    public DottedDwellingGroup(DwellingGroup dwellingGroup, Dot dot) {
        this.dwellingGroup = dwellingGroup;
        this.dot = dot;
    }

    public DwellingGroup getDwellingGroup() {
        return dwellingGroup;
    }

    public Dot getDot() {
        return dot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DottedDwellingGroup that = (DottedDwellingGroup) o;

        if (!dwellingGroup.equals(that.dwellingGroup)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dwellingGroup.hashCode();
    }
}
