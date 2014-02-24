package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

    public int size();
    public Iterable<? extends Dwelling> getDwellings();
    public Element toXml(Document document);
    public String getKey();
    public String getCommonName();
    public PostcodeDatum getPostcode();
}
