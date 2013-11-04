package uk.co.epii.conservatives.fredericknorth.routes;

import com.tomgibara.cluster.gvm.dbl.DblClusters;
import com.tomgibara.cluster.gvm.dbl.DblListKeyer;
import com.tomgibara.cluster.gvm.dbl.DblResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 22/08/2013
 * Time: 11:13
 */
public class DefaultRoutableArea implements RoutableArea {

    private final BoundedArea boundedArea;
    private final HashSet<Route> routes;
    private final RoutableArea parent;
    private final HashSet<DefaultRoutableArea> children;
    private final HashSet<DwellingGroup> unroutedDwellingGroups;
    private final HashSet<DwellingGroup> routedDwellingGroups;
    private final HashSet<DwellingGroup> dwellingGroups;

    public DefaultRoutableArea(BoundedArea boundedArea, RoutableArea parent) {
        this.boundedArea = boundedArea;
        routes = new HashSet<Route>();
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
    public Collection<? extends DwellingGroup> getRoutedDwellingGroups() {
        return routedDwellingGroups;
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
        if (!unroutedOnly) {
            for (Route route : routes) {
                removeRoute(route, this);
            }
        }
        for (DefaultRoutableArea child : children) {
            child.autoGenerate(targetSize, unroutedOnly);
        }
        routeUnrouted(targetSize);
    }

    private void routeUnrouted(int targetSize) {
        int routes = calculateRoutesCount(targetSize);
        DblClusters<List<DwellingGroup>> clusters = new DblClusters<List<DwellingGroup>>(2, routes);
        clusters.setKeyer(new DblListKeyer<DwellingGroup>());
        for (DwellingGroup dwellingGroup : unroutedDwellingGroups) {
            Point geoLocation = dwellingGroup.getPoint();
            double[] doubleGeoLocation = new double[] {geoLocation.getX(), geoLocation.getY()};
            double weight = dwellingGroup.size();
            ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
            dwellingGroups.add(dwellingGroup);
            clusters.add(weight, doubleGeoLocation, dwellingGroups);
        }
        for (DblResult<List<DwellingGroup>> proposedRoute : clusters.results()) {
            DwellingGroup largest = null;
            for (DwellingGroup dwellingGroup : proposedRoute.getKey()) {
                if (largest == null || largest.size() < dwellingGroup.size()) {
                    largest = dwellingGroup;
                }
                else if (largest.size() == dwellingGroup.size()) {
                    if (largest.compareTo(dwellingGroup) < 0) {
                        largest = dwellingGroup;
                    }
                }
            }
            Route route = createRoute(largest.getName());
            route.addDwellingGroups(proposedRoute.getKey());
        }
    }


    private int calculateRoutesCount(int targetSize) {
        HashMap<Point, Integer> pointSizes = new HashMap<Point, Integer>(dwellingGroups.size());
        for (DwellingGroup dwellingGroup : unroutedDwellingGroups) {
            int count = 0;
            if (pointSizes.containsKey(dwellingGroup.getPoint())) {
                count += pointSizes.get(dwellingGroup.getPoint());
            }
            count += dwellingGroup.size();
            pointSizes.put(dwellingGroup.getPoint(), count);
        }
        List<Integer> sizes = new ArrayList<Integer>(pointSizes.values());
        Collections.sort(sizes);
        int targetIndex = Collections.binarySearch(sizes, targetSize);
        double size = 0;
        int countTo = targetIndex < 0 ? ~targetIndex : targetIndex;
        for (int i = 0; i < countTo; i++) {
            size += sizes.get(i);
        }
        size += (sizes.size() - countTo) * targetSize;
        return (int)Math.ceil(size / targetSize);
    }

    @Override
    public Route createRoute(String name) {
        RouteImpl route = new RouteImpl(this, name);
        routes.add(route);
        if (parent != null) {
            parent.addRoute(route, this);
        }
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
            if (parent != null) {
                parent.markAsRouted(dwellingGroup, this);
            }
            for (DefaultRoutableArea child : children) {
                child.markAsRouted(dwellingGroup, this);
            }
        }
    }

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup) {
        if (routedDwellingGroups.remove(dwellingGroup)) {
            unroutedDwellingGroups.add(dwellingGroup);
            if (parent != null) {
                parent.markAsUnrouted(dwellingGroup, this);
            }
            for (DefaultRoutableArea child : children) {
                child.markAsUnrouted(dwellingGroup, this);
            }
        }
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup, RoutableArea informant) {
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

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup, RoutableArea informant) {
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
    public String getName() {
        return getBoundedArea().getName();
    }

    @Override
    public void save(File selectedFile) {
        throw new UnsupportedOperationException("This method is not yet supported");
    }

    @Override
    public RoutableArea getParent() {
        return parent;
    }

    @Override
    public int getUnroutedDwellingCount() {
        int count = 0;
        for (DwellingGroup dwellingGroup : getUnroutedDwellingGroups()) {
            count += dwellingGroup.size();
        }
        return count;
    }

    @Override
    public int getDwellingCount() {
        int count = 0;
        for (DwellingGroup dwellingGroup : getDwellingGroups()) {
            count += dwellingGroup.size();
        }
        return count;
    }

    @Override
    public void addRoute(Route route, RoutableArea informant) {
        if (routes.add(route)) {
            if (informant == parent) {
                for (DefaultRoutableArea child : children) {
                    child.addRoute(route, this);
                }
            }
            else if (children.contains(informant)) {
                if (parent != null) {
                    parent.addRoute(route, this);
                }
            }
            else {
                throw new IllegalArgumentException("The DefaultRoutableArea has been informed of a change of " +
                        "routed status by an informant other than its parent or child");
            }
        }
    }

    @Override
    public void removeRoute(Route route, RoutableArea informant) {
        if (routes.remove(route)) {
            if (informant == parent) {
                for (DefaultRoutableArea child : children) {
                    child.removeRoute(route, this);
                }
            }
            else if (children.contains(informant)) {
                if (parent != null) {
                    parent.removeRoute(route, this);
                }
            }
            else {
                throw new IllegalArgumentException("The DefaultRoutableArea has been informed of a change of " +
                        "routed status by an informant other than its parent or child");
            }
        }
    }

    @Override
    public void load(File selectedFile) {
        throw new UnsupportedOperationException("This method is not yet supported");
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

    public void addDwellingGroup(DwellingGroup dwellingGroup, boolean routed) {
        dwellingGroups.add(dwellingGroup);
        if (routed) {
            routedDwellingGroups.add(dwellingGroup);
        }
        else {
            unroutedDwellingGroups.add(dwellingGroup);
        }
    }

    public void addChild(DefaultRoutableArea childRoutableArea) {
        children.add(childRoutableArea);
    }
}
