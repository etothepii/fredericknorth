package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 19/07/2013
 * Time: 18:55
 */
public interface RoutableArea {

    public BoundedArea getBoundedArea();
    public Collection<? extends Route> getRoutes();
    public Collection<? extends DwellingGroup> getRoutedDwellingGroups();
    public Collection<? extends DwellingGroup> getUnroutedDwellingGroups();
    public Collection<? extends DwellingGroup> getDwellingGroups();
    public Element toXml(Document document);
    public void autoGenerate(int targetSize, boolean unroutedOnly);
    public Route createRoute(String name);
    public int getRouteCount();
    public void removeAll();
    public void markAsRouted(DwellingGroup dwellingGroup);
    public void markAsUnrouted(DwellingGroup dwellingGroup);
    public void markAsRouted(DwellingGroup dwellingGroup, RoutableArea routableArea);
    public void markAsUnrouted(DwellingGroup dwellingGroup, RoutableArea routableArea);
    public String getName();
    public void load(Element element);
    public void save(File selectedFile);
    public RoutableArea getParent();
    public int getUnroutedDwellingCount();
    public int getDwellingCount();
    public void addRoute(Route route, RoutableArea informant);
    public void removeRoute(Route route, RoutableArea informant);
    public void addDwellingGroup(DwellingGroup dwellingGroup, boolean routed);
    public void setRouter(Router router);
}
