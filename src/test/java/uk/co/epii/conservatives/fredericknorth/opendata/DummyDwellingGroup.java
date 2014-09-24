package uk.co.epii.conservatives.fredericknorth.opendata;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:25
 */
public class DummyDwellingGroup implements DwellingGroup {

    ArrayList<Location> dwellings;
    String name;
    String commonName;
    Point point;
    String identifierSummary;
    String postcode;

    public DummyDwellingGroup(String name, int count, Point point) {
        this.name = name;
        this.commonName = name;
        dwellings = new ArrayList<Location>();
        for (int i = 1; i <= count; i++) {
            dwellings.add(new DummyDwelling(i + "", this, point));
        }
        this.point = point;
    }

    public DummyDwellingGroup(String name, int count, Point point, String postcode) {
      this(name, count, point);
      this.postcode = postcode;
    }

    @Override
    public String getName() {
        return name;
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
    public List<? extends Location> getDwellings() {
      return dwellings;
    }

    @Override
    public Element toXml(Document document) {
        throw new UnsupportedOperationException("This method is not supported in this Dummy instance");
    }

  @Override
  public Collection<String> getPostcodes() {
    return Arrays.asList(new String[] {postcode});
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  @Override
    public String getKey() {
        return PointExtensions.getLocationString(getPoint()).concat(getCommonName());
    }

    @Override
    public String getCommonName() {
        return commonName;
    }

    @Override
    public String getIdentifierSummary() {
        return identifierSummary;
    }

    @Override
    public int compareTo(DwellingGroup o) {
        return name.compareTo(o.getName());
    }

    public void setIdentifierSummary(String identifierSummary) {
        this.identifierSummary = identifierSummary;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
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
