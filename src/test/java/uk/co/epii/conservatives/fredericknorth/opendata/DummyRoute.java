package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.routes.Ward;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:40
 */
public class DummyRoute implements Route {

    private String name;
    private HashSet<DwellingGroup> dwellingGroupHashSet;
    private final Ward ward;
    private int dwellingCount;
    private String association;

    public DummyRoute(String name, Ward ward) {
        this.name = name;
        this.ward = ward;
        dwellingGroupHashSet = new HashSet<DwellingGroup>();
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
    public Ward getWard() {
        return ward;
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
}
