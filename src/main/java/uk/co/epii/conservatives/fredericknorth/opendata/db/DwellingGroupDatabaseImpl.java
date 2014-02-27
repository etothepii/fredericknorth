package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.AbstractDwellingGroupImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;

import java.awt.*;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:21
 */
public class DwellingGroupDatabaseImpl extends AbstractDwellingGroupImpl {

    private final Map<DwellingDatabaseImpl, Dwelling> dwellings;
    private final PostcodeDatumDatabaseImpl postcode;
    private Point medianPoint;

    public DwellingGroupDatabaseImpl(PostcodeDatumDatabaseImpl postcode,
            Map<DwellingDatabaseImpl, Dwelling> dwellings,
            String commonName, Point medianPoint) {
        super(commonName);
        this.postcode = postcode;
        this.dwellings = dwellings;
        this.medianPoint = medianPoint;
    }

    @Override
    public int size() {
        return dwellings.size();
    }

    @Override
    public Iterable<? extends uk.co.epii.conservatives.fredericknorth.opendata.Dwelling> getDwellings() {
        return dwellings.keySet();
    }

    @Override
    public Element toXml(Document document) {
        Element dwellingGroup = document.createElement("DwellingGroup");
        Element name = document.createElement("Key");
        name.setTextContent(getKey());
        dwellingGroup.appendChild(name);
        return dwellingGroup;
    }


    @Override
    public String getKey() {
        return postcode.getName().concat(PointExtensions.getLocationString(getPoint())).concat(getCommonName());
    }

    @Override
    public PostcodeDatum getPostcode() {
        return postcode;
    }

    @Override
    public int compareTo(DwellingGroup o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public Point getPoint() {
        return medianPoint;
    }
}
