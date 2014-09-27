package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.routes.Router;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:21
 */
public class DummyRoutableArea implements RoutableArea {

    private String name;
    private final String id;
    private final List<DummyDwellingGroup> dwellingGroups;
    private final HashMap<String, DummyRoute> routes;
    private Polygon[] enclaves;
    private RoutableArea[] children;
    private Polygon area;
    private BoundedArea boundedArea;
    private RoutableArea parent;
  private Router router;

  public DummyRoutableArea(BoundedArea boundedArea, RoutableArea parent, String name, String id) {
        this.boundedArea = boundedArea;
        this.parent = parent;
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
    public void load(Element element) {
        throw new UnsupportedOperationException("This operation is not supported as this is a dummy");
    }

    @Override
    public void save(File selectedFile) {
        throw new UnsupportedOperationException("This operation is not supported as this is a dummy");
    }

    @Override
    public RoutableArea getParent() {
        return parent;
    }

    @Override
    public BoundedArea getBoundedArea() {
        return boundedArea;
    }

    @Override
    public List<DummyRoute> getRoutes() {
        return new ArrayList<DummyRoute>(routes.values());
    }

    @Override
    public Collection<? extends DwellingGroup> getRoutedDwellingGroups() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public List<? extends DwellingGroup> getDwellingGroups() {
        return dwellingGroups;
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
    public void addRoute(Route route, RoutableArea informant) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void removeRoute(Route route, RoutableArea informant) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void addDwellingGroup(DwellingGroup dwellingGroup, boolean routed) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

  @Override
  public void setRouter(Router router) {
    this.router = router;
  }

  @Override
    public void markAsRouted(DwellingGroup dwellingGroup) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup, RoutableArea routableArea) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup, RoutableArea routableArea) {
        throw new UnsupportedOperationException("This operation is not supported in the Dummy");
    }

    @Override
    public Collection<? extends DwellingGroup> getUnroutedDwellingGroups() {
        HashSet<DwellingGroup> dwellingGroups = new HashSet<DwellingGroup>(this.dwellingGroups);
        for (DummyRoute dummyRoute : routes.values()) {
            dwellingGroups.removeAll(dummyRoute.getDwellingGroups());
        }
        return dwellingGroups;
    }

    @Override
    public void autoGenerate(ProgressTracker progressTracker, int targetSize, boolean unroutedOnly) {
    }

}
