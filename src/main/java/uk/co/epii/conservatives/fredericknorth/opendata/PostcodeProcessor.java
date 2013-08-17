package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.maps.OSMap;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jrrpl
 * Date: 21/06/2013
 * Time: 19:13
 */
public interface PostcodeProcessor {

    public int getDwellingCount(String postcode);
    public int[] getCouncilBandCount(String postcode);
    public Point getLocation(String postcode);
    public String getAdminWardId(String postcode);
    public OSMap getContainingMap(String postcode);
    public Set<OSMap> getContainingMaps(Collection<String> postcodes);
    public Set<String> getWard(String wardId);
    public Set<String> getWards();

}
