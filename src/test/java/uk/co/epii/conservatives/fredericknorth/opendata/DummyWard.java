package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.extensions.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.routes.Ward;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:21
 */
public class DummyWard implements Ward {

    private String name;
    private final String id;
    private final List<DummyDwellingGroup> dwellingGroups;
    private final HashMap<String, DummyRoute> routes;
    private Polygon[] enclaves;
    private RoutableArea[] children;
    private Polygon area;

    public DummyWard(String name, String id) {
        this.name = name;
        this.id = id;
        dwellingGroups = new ArrayList<DummyDwellingGroup>();
        routes = new HashMap<String, DummyRoute>();
        enclaves = new Polygon[0];
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
    public String getId() {
        return id;
    }

    @Override
    public List<DummyRoute> getRoutes() {
        return new ArrayList<DummyRoute>(routes.values());
    }

    @Override
    public List<? extends DwellingGroup> getDwellingGroups() {
        return dwellingGroups;
    }

    @Override
    public RoutableArea[] getChildren() {
        return children;
    }

    @Override
    public BoundedAreaType getBoundedAreaType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Polygon getArea() {
        return area;
    }

    public void setEnclaves(Polygon[] enclaves) {
        this.enclaves = enclaves;
    }

    public void setChildren(RoutableArea[] children) {
        this.children = children;
    }

    public void setArea(Polygon area) {
        this.area = area;
    }

    @Override
    public Polygon[] getEnclaves() {
        return enclaves;
    }

    public void addDwellingGroup(DummyDwellingGroup dummyDwellingGroup) {
        dwellingGroups.add(dummyDwellingGroup);
    }

    @Override
    public DummyRoute createRoute(String name) {
        routes.put(name, new DummyRoute(name, this));
        return routes.get(name);
    }

    @Override
    public int getRouteCount() {
        return routes.size();
    }

    public void addRoute(DummyRoute dummyRoute) {
        routes.put(dummyRoute.getName(), dummyRoute);
    }

    @Override
    public Element toXml(Document document) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void addChild(BoundedArea boundedAreas) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void save(XMLSerializer xmlSerializer, File selectedFile) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public NearestPoint getNearestGeoPoint(Point2D.Float point) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAll() {}

    @Override
    public int getUnroutedDwellingCount() {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public int getDwellingCount() {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup) {}

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup) {}

    @Override
    public Collection<? extends DwellingGroup> getUnroutedDwellingGroups() {
        HashSet<DwellingGroup> dwellingGroups = new HashSet<DwellingGroup>(this.dwellingGroups);
        for (DummyRoute dummyRoute : routes.values()) {
            dwellingGroups.removeAll(dummyRoute.getDwellingGroups());
        }
        return dwellingGroups;
    }

    @Override
    public void load(ApplicationContext applicationContext, Element wardElt) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public List<? extends Route> proposeRoutes(int targetSize, boolean unroutedOnly) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void autoGenerate(int targetSize, boolean unroutedOnly) {
    }

}
