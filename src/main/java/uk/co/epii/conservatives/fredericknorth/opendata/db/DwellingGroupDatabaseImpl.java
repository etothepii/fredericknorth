package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;

import java.awt.*;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:21
 */
public class DwellingGroupDatabaseImpl implements DwellingGroup {

    private final Map<DwellingDatabaseImpl, Dwelling> dwellings;
    private String commonName;
    private Point medianPoint;

    public DwellingGroupDatabaseImpl(
            Map<DwellingDatabaseImpl, Dwelling> dwellings,
            String commonName, Point medianPoint) {
        this.dwellings = dwellings;
        this.commonName = commonName;
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
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public int compareTo(DwellingGroup o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public String getName() {
        return commonName;
    }

    @Override
    public Point getPoint() {
        return medianPoint;
    }
}
