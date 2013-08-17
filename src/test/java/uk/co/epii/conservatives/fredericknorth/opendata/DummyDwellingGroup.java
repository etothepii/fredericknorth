package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:25
 */
public class DummyDwellingGroup implements DwellingGroup {

    ArrayList<Dwelling> dwellings;
    String name;
    Point point;
    private PostcodeDatum postcode;

    public DummyDwellingGroup(String name, int count, Point point) {
        this.name = name;
        dwellings = new ArrayList<Dwelling>();
        for (int i = 1; i <= count; i++) {
            dwellings.add(new DummyDwelling(i + "", this));
        }
        this.point = point;
    }

    @Override
    public void setDisplayName(String displayName) {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setUniquePart(String uniquePart) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public int size() {
        return dwellings.size();
    }

    @Override
    public PostcodeDatum getPostcode() {
        return postcode;
    }

    public void setPostcode(PostcodeDatum postcode) {
        this.postcode = postcode;
    }

    @Override
    public List<? extends Dwelling> getDwellings() {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public void add(Dwelling dwelling) {
        dwellings.add(dwelling);
    }

    @Override
    public void load(ApplicationContext applicationContext, Element dwellingGroupElt) {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

    @Override
    public String getIdentifierSummary() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUniquePart() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(DwellingGroup o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DummyDwellingGroup that = (DummyDwellingGroup) o;

        if (!name.equals(that.name)) return false;
        if (point != null ? !point.equals(that.point) : that.point != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (point != null ? point.hashCode() : 0);
        return result;
    }
}
