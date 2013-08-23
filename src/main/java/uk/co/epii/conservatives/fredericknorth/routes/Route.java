package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.util.Collection;
import java.util.Set;

/**
 * User: James Robinson
 * Date: 20/06/13
 * Time: 23:51
 */
public interface Route {

    public String getName();
    public void setName(String name);
    public Set<DwellingGroup> getDwellingGroups();
    public Element toXml(Document document);
    public RoutableArea getRoutableArea();
    public int getDwellingCount();
    public void addDwellingGroups(Collection<? extends DwellingGroup> dwellingGroups);
    public void removeDwellingGroups(Collection<? extends DwellingGroup> dwellingGroups);
    public void load(ApplicationContext applicationContext, Element routeElt);
    public String getAssociation();
    public void setAssociation(String association);
    public String getFullyQualifiedName();
}
