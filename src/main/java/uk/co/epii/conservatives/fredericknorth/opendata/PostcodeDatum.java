package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:21
 */
public interface PostcodeDatum extends Location {

    public Iterable<Dwelling> getDwellings();
    public int size();
    public int[] getCouncilBandCount();

}
