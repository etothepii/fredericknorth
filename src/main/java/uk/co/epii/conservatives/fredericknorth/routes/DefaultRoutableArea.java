package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: James Robinson
 * Date: 22/08/2013
 * Time: 11:13
 */
public class DefaultRoutableArea implements RoutableArea {

    private final BoundedArea boundedArea;
    private final HashSet<RouteImpl> routes;
    private final DefaultRoutableArea parent;
    private final HashSet<DefaultRoutableArea> children;
    private final HashSet<DwellingGroup> unroutedDwellingGroups;
    private final HashSet<DwellingGroup> routedDwellingGroups;
    private final HashSet<DwellingGroup> dwellingGroups;

    public DefaultRoutableArea(BoundedArea boundedArea, DefaultRoutableArea parent) {
        this.boundedArea = boundedArea;
        routes = new HashSet<RouteImpl>();
        this.parent = parent;
        children = new HashSet<DefaultRoutableArea>();
        routedDwellingGroups = new HashSet<DwellingGroup>();
        unroutedDwellingGroups = new HashSet<DwellingGroup>();
        dwellingGroups = new HashSet<DwellingGroup>();
    }

    @Override
    public BoundedArea getBoundedArea() {
        return boundedArea;
    }

    @Override
    public Collection<? extends Route> getRoutes() {
        return routes;
    }

    @Override
    public Collection<? extends DwellingGroup> getUnroutedDwellingGroups() {
        return unroutedDwellingGroups;
    }

    @Override
    public Collection<? extends DwellingGroup> getDwellingGroups() {
        return dwellingGroups;
    }

    @Override
    public Element toXml(Document document) {
        throw new UnsupportedOperationException("This method has not yet been implemented");
    }

    @Override
    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        throw new UnsupportedOperationException("This method has not yet been implemented");
    }

    @Override
    public Route createRoute(String name) {
        RouteImpl route = new RouteImpl(this, name);
        routes.add(route);
        return route;
    }

    @Override
    public int getRouteCount() {
        return routes.size();
    }

    @Override
    public void removeAll() {
        routes.clear();
        unroutedDwellingGroups.clear();
        unroutedDwellingGroups.addAll(dwellingGroups);
        routedDwellingGroups.clear();
        for (DefaultRoutableArea childRoutableArea : children) {
            childRoutableArea.removeAll();
        }
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup) {
        if (unroutedDwellingGroups.remove(dwellingGroup)) {
            routedDwellingGroups.add(dwellingGroup);
        }
    }

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup) {
        if (routedDwellingGroups.remove(dwellingGroup)) {
            unroutedDwellingGroups.add(dwellingGroup);
        }
    }

    private void markAsRouted(DwellingGroup dwellingGroup, RoutableArea informant) {
        if (unroutedDwellingGroups.remove(dwellingGroup)) {
            routedDwellingGroups.add(dwellingGroup);
            if (informant == parent) {
                for (DefaultRoutableArea child : children) {
                    child.markAsRouted(dwellingGroup, this);
                }
            }
            else if (children.contains(informant)) {
                if (parent != null) {
                    parent.markAsRouted(dwellingGroup, this);
                }
            }
            else {
                throw new IllegalArgumentException("The DefaultRoutableArea has been informed of a change of " +
                        "routed status by an informant other than its parent or child");
            }
        }
    }

    private void markAsUnrouted(DwellingGroup dwellingGroup, RoutableArea informant) {
        if (routedDwellingGroups.remove(dwellingGroup)) {
            unroutedDwellingGroups.add(dwellingGroup);
            if (informant == parent) {
                for (DefaultRoutableArea child : children) {
                    child.markAsUnrouted(dwellingGroup, this);
                }
            }
            else if (children.contains(informant)) {
                if (parent != null) {
                    parent.markAsUnrouted(dwellingGroup, this);
                }
            }
            else {
                throw new IllegalArgumentException("The DefaultRoutableArea has been informed of a change of " +
                        "routed status by an informant other than its parent or child");
            }
        }
    }

    @Override
    public void load(ApplicationContext applicationContext, Element wardElt) {
        throw new UnsupportedOperationException("This method has not yet been implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultRoutableArea that = (DefaultRoutableArea) o;

        if (boundedArea != null ? !boundedArea.equals(that.boundedArea) : that.boundedArea != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return boundedArea != null ? boundedArea.hashCode() : 0;
    }
}
