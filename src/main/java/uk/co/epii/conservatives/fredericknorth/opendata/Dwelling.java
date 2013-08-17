package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 20/06/13
 * Time: 23:46
 */
public interface Dwelling extends Location {
    public String getIdentifier();
    public DwellingGroup getDwellingGroup();
    public char getCouncilTaxBand();
    public void setDwellingGroup(DwellingGroup dwellingGroup);
    public void setPoint(Point point);
}
