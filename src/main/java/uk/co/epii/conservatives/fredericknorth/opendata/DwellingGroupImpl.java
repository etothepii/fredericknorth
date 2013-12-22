package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:51
 */
public class DwellingGroupImpl extends AbstractDwellingGroupImpl {

    private final List<Dwelling> dwellings;
    private PostcodeDatumImpl postcode;
    private String displayName;
    private String uniquePart;
    private Point point;
    private String identifierSummary;

    public DwellingGroupImpl(String name, String displayName, PostcodeDatumImpl postcode) {
        super(name, displayName);
        this.postcode = postcode;
        dwellings = new ArrayList<Dwelling>();
    }

    public void setUniquePart(String uniquePart) {
        this.uniquePart = uniquePart;
    }

    @Override
    public Point getPoint() {
        return point == null ? postcode.getPoint() : point;
    }

    @Override
    public int size() {
        return dwellings.size();
    }

    public void add(Dwelling dwelling) {
        dwellings.add(dwelling);
        if (postcode != null) {
            postcode.add(dwelling);
        }
    }

    void load(ApplicationContext applicationContext, Element dwellingGroupElt) {
        if (!dwellingGroupElt.getTagName().equals("DwellingGroup")) throw new IllegalArgumentException("You have not provided a Route node");
        String postcode = dwellingGroupElt.getElementsByTagName("Postcode").item(0).getTextContent();
        if (!this.postcode.getName().equals(postcode)) {
            throw new RuntimeException("This is not the DwellingGroup for this node as the Postcodes differ");
        }
        String name = dwellingGroupElt.getElementsByTagName("Name").item(0).getTextContent();
        if (!this.getName().equals(name)) {
            throw new RuntimeException("This is not the DwellingGroup for this node as the names differ");
        }
    }

    public Element toXml(Document document) {
        Element dwellingGroupElt = document.createElement("DwellingGroup");
        Element dwellingGroupName = document.createElement("Key");
        dwellingGroupElt.appendChild(dwellingGroupName);
        dwellingGroupName.setTextContent(getKey());
        return dwellingGroupElt;
    }

    @Override
    public String getKey() {
        return postcode.getName().concat(PointExtensions.getLocationString(getPoint()));
    }

    @Override
    public List<? extends Dwelling> getDwellings() {
        return dwellings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DwellingGroupImpl that = (DwellingGroupImpl) o;
        if (!getName().equals(that.getName())) return false;
        if (!postcode.equals(that.postcode)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + postcode.hashCode();
        return result;
    }

    @Override
    public int compareTo(DwellingGroup o) {
        return getName().compareTo(o.getName());
    }
}
