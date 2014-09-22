package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.routes.Route;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:40
 */
public class DummyRoute implements Route {

    private String name;
    private HashSet<DwellingGroup> dwellingGroupHashSet;
    private final RoutableArea routableArea;
    private int dwellingCount;
    private String association;

    public DummyRoute(String name, RoutableArea routableArea) {
        this.name = name;
        this.routableArea = routableArea;
        dwellingGroupHashSet = new HashSet<DwellingGroup>();
    }

  @Override
  public UUID getUuid() {
    return UUID.fromString("00000");
  }

  @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<DwellingGroup> getDwellingGroups() {
        return dwellingGroupHashSet;
    }

    @Override
    public Element toXml(Document document) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public RoutableArea getRoutableArea() {
        return routableArea;
    }

    @Override
    public int getDwellingCount() {
        return dwellingCount;
    }

    public void setDwellingCount(int dwellingCount) {
        this.dwellingCount = dwellingCount;
    }

    @Override
    public void addDwellingGroups(Collection<? extends DwellingGroup> dwellingGroups) {
        dwellingGroupHashSet.addAll(dwellingGroups);
    }

    public void addDwellingGroup(DwellingGroup dwellingGroup) {
        dwellingGroupHashSet.add(dwellingGroup);
    }

    @Override
    public void removeDwellingGroups(Collection<? extends DwellingGroup> dwellingGroups) {
        dwellingGroupHashSet.removeAll(dwellingGroups);
    }

    @Override
    public void load(ApplicationContext applicationContext, Element routeElt) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public String getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(String association) {
        this.association = association;
    }

    @Override
    public String getFullyQualifiedName() {
        StringBuilder stringBuilder = new StringBuilder(255);
        stringBuilder.append(getName());
        RoutableArea parent = routableArea;
        do {
            stringBuilder.insert(0, " - ");
            stringBuilder.insert(0, parent.getName());
        } while ((parent = parent.getParent()) != null);
        return stringBuilder.toString();
    }
}
