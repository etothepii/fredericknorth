package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.opendata.AbstractDwellingGroupImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:21
 */
public class DwellingGroupDatabaseImpl extends AbstractDwellingGroupImpl {

    private final List<DwellingDatabaseImpl> dwellings;
    private final Set<String> postcode;
    private final Point medianPoint;

    public DwellingGroupDatabaseImpl(
            Collection<DwellingDatabaseImpl> dwellings,
            String commonName, Point medianPoint) {
        super(commonName);
        postcode = extractPostcode(dwellings);
        this.dwellings = new ArrayList<DwellingDatabaseImpl>(dwellings);
        this.medianPoint = medianPoint;
    }

  private Set<String> extractPostcode(Collection<DwellingDatabaseImpl> dwellings) {
    Set<String> postcodes = new HashSet<String>();
    for (DwellingDatabaseImpl dwelling : dwellings) {
      postcodes.add(dwelling.getDeliveryPointAddress().getPostcode());
    }
    return postcodes;
  }

  @Override
    public int size() {
        return dwellings.size();
    }

    @Override
    public Iterable<? extends Location> getDwellings() {
        return dwellings;
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
  public Collection<String> getPostcodes() {
    return postcode;
  }


  @Override
    public String getKey() {
        return PointExtensions.getLocationString(getPoint()).concat(getCommonName());
    }

    @Override
    public int compareTo(DwellingGroup o) {
        int compareTo = getCommonName().compareTo(o.getCommonName());
        if (compareTo != 0) {
          return compareTo;
        }
        return getName().compareTo(o.getName());
    }

    @Override
    public Point getPoint() {
        return medianPoint;
    }
}
