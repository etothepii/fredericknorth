package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:16
 */
public interface Ward extends RoutableArea {
    public String getId();
    public Route createRoute(String name);
    public int getRouteCount();
    public Element toXml(Document document);
    public void removeAll();
    public int getUnroutedDwellingCount();
    public int getDwellingCount();
    public void markAsRouted(DwellingGroup dwellingGroup);
    public void markAsUnrouted(DwellingGroup dwellingGroup);
    public void load(ApplicationContext applicationContext, Element wardElt);
}
