package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.awt.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 20/06/13
 * Time: 23:46
 */
public interface DwellingGroup extends Location, Comparable<DwellingGroup> {

    public void setDisplayName(String displayName);
    public String getDisplayName();
    public String getName();
    public void setUniquePart(String uniquePart);
    public void setPoint(Point point);
    public int size();
    public PostcodeDatum getPostcode();
    public List<? extends Dwelling> getDwellings();
    public void add(Dwelling dwelling);
    public void load(ApplicationContext applicationContext, Element dwellingGroupElt);
    public String getIdentifierSummary();
    public String getUniquePart();
}
