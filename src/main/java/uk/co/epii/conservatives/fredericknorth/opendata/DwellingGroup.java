package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.util.Collection;

/**
 * User: James Robinson
 * Date: 20/06/13
 * Time: 23:46
 */
public interface DwellingGroup extends Location, Comparable<DwellingGroup> {

    public int size();
    public Iterable<? extends Location> getDwellings();
    public Element toXml(Document document);
    public Collection<String> getPostcodes();
    public String getKey();
    public String getCommonName();
    public String getIdentifierSummary();
}
