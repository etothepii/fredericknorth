package uk.co.epii.conservatives.fredericknorth.routes;

import com.tomgibara.cluster.gvm.dbl.DblClusters;
import com.tomgibara.cluster.gvm.dbl.DblListKeyer;
import com.tomgibara.cluster.gvm.dbl.DblResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.AbstractBoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:55
 */
class WardImpl extends AbstractBoundedArea implements Ward {

    private final Set<? extends DwellingGroup> dwellingGroups;
    private final Set<DwellingGroup> unroutedDwellingGroups;
    private final List<RoutableArea> children;
    private final HashMap<String, RouteImpl> routes;
    private final String id;
    private final List<Polygon> enclaves;
    private String name;
    private Polygon area;

    WardImpl(String id, String name) {
        this(name, new ArrayList<DwellingGroup>(), id);
    }

    WardImpl(String name, Collection<? extends DwellingGroup> dwellingGroups, String id) {
        super(BoundedAreaType.UNITARY_DISTRICT_WARD, name);
        this.dwellingGroups = new HashSet<DwellingGroup>(dwellingGroups);
        unroutedDwellingGroups = new HashSet<DwellingGroup>(dwellingGroups);
        this.routes = new HashMap<String, RouteImpl>();
        this.id = id;
        this.name = name;
        children = new ArrayList<RoutableArea>(32);
        enclaves = new ArrayList<Polygon>(32);
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
    public BoundedAreaType getBoundedAreaType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<? extends Route> getRoutes() {
        return new ArrayList<Route>(routes.values());
    }

    @Override
    public List<? extends DwellingGroup> getDwellingGroups() {
        return new ArrayList<DwellingGroup>(dwellingGroups);
    }

    @Override
    public Polygon getArea() {
        return area;
    }

    @Override
    public Polygon[] getEnclaves() {
        return new Polygon[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<? extends DwellingGroup> getUnroutedDwellingGroups() {
        return new ArrayList<DwellingGroup>(unroutedDwellingGroups);
    }

    @Override
    public void load(ApplicationContext applicationContext, Element wardElt) {
        if (!wardElt.getTagName().equals("Ward")) throw new IllegalArgumentException("The element provided is not a Ward Tag");
        String wardId = wardElt.getElementsByTagName("Id").item(0).getTextContent();
        if (!this.id.equals(wardId)) {
            throw new RuntimeException("The provided Element is not this Ward");
        }
        Element routesElt = (Element)wardElt.getElementsByTagName("Routes").item(0);
        NodeList routesNodeList = routesElt.getElementsByTagName("Route");
        routes.clear();
        for (int i = 0; i < routesNodeList.getLength(); i++) {
            Element routeElt = (Element)routesNodeList.item(i);
            String routeName = routeElt.getElementsByTagName("Name").item(0).getTextContent();
            Route route = createRoute(routeName);
            route.load(applicationContext, routeElt);
        }
    }

    @Override
    public Element toXml(Document document) {
        Element wardElt = document.createElement("Ward");
        Element idElt = document.createElement("Id");
        idElt.setTextContent(id);
        wardElt.appendChild(idElt);
        Element routesElt = document.createElement("Routes");
        wardElt.appendChild(routesElt);
        for (RouteImpl route : routes.values()) {
            routesElt.appendChild(route.toXml(document));
        }
        return wardElt;
    }

    @Override
    public List<? extends Route> proposeRoutes(int targetSize, boolean unroutedOnly) {
        return proposeRouteImpls(targetSize, unroutedOnly);
    }

    private List<RouteImpl> proposeRouteImpls(int targetSize, boolean unroutedOnly) {
        int routes = calculateRoutesCount(targetSize, unroutedOnly);
        DblClusters<List<DwellingGroup>> clusters = new DblClusters<List<DwellingGroup>>(2, routes);
        clusters.setKeyer(new DblListKeyer<DwellingGroup>());
        for (DwellingGroup dwellingGroup : unroutedOnly ? unroutedDwellingGroups : dwellingGroups) {
            Point geoLocaion = dwellingGroup.getPostcode().getPoint();
            double[] doubleGeoLocation = new double[] {geoLocaion.getX(), geoLocaion.getY()};
            double weight = dwellingGroup.size();
            ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
            dwellingGroups.add(dwellingGroup);
            clusters.add(weight, doubleGeoLocation, dwellingGroups);
        }
        ArrayList<RouteImpl> results = new ArrayList<RouteImpl>(routes);
        for (DblResult<List<DwellingGroup>> proposedRoute : clusters.results()) {
            DwellingGroup largest = null;
            for (DwellingGroup dwellingGroup : proposedRoute.getKey()) {
                if (largest == null  || largest.size() < dwellingGroup.size()) {
                    largest = dwellingGroup;
                }
                else if (largest.size() == dwellingGroup.size()) {
                    if (largest.getUniquePart().compareTo(dwellingGroup.getUniquePart()) < 0) {
                        largest = dwellingGroup;
                    }
                }
            }
            RouteImpl route = new RouteImpl(this, largest.getUniquePart());
            route.addDwellingGroups(proposedRoute.getKey());
            results.add(route);
        }
        return results;
    }

    @Override
    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        if (unroutedOnly) {
            addAllRoutes(proposeRouteImpls(targetSize, true));
        }
        else {
            setRoutes(proposeRouteImpls(targetSize, false));
        }
    }

    void addRoute(RouteImpl route) {
        if (routes.containsKey(route.getName())) {
            int count = 2;
            while (routes.containsKey(route.getName() + " " + count)) {
                count++;
            }
            route.setName(route.getName() + " " + count);
        }
        routes.put(route.getName(), route);
    }

    void addAllRoutes(Collection<? extends RouteImpl> routes) {
        for (RouteImpl route : routes) {
            addRoute(route);
        }
    }

    void setRoutes(Collection<RouteImpl> routes) {
        routes.clear();
        addAllRoutes(routes);
    }

    private int calculateRoutesCount(int targetSize, boolean unroutedOnly) {
        HashMap<Point, Integer> pointSizes = new HashMap<Point, Integer>(dwellingGroups.size());
        for (DwellingGroup dwellingGroup : unroutedOnly ? unroutedDwellingGroups : dwellingGroups) {
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

    public Route createRoute(String name) {
        RouteImpl route = routes.get(name);
        if (route == null) {
            route = new RouteImpl(this, name);
            routes.put(name, route);
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
    }

    @Override
    public int getDwellingCount() {
        int count = 0;
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            count += dwellingGroup.size();
        }
        return count;
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup) {
        if (!unroutedDwellingGroups.remove(dwellingGroup)) {
            throw new IllegalArgumentException("The provided dwelling group is not currently unrouted");
        }
    }

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup) {
        if (!dwellingGroups.contains(dwellingGroup)) {
            throw new IllegalArgumentException("The provided dwelling group is not in this ward");
        }
        if (!unroutedDwellingGroups.add(dwellingGroup)) {
            throw new IllegalArgumentException("The provided dwelling group was already unrouted");
        }
    }

    @Override
    public int getUnroutedDwellingCount() {
        int count = 0;
        for (DwellingGroup dwellingGroup : unroutedDwellingGroups) {
            count += dwellingGroup.size();
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WardImpl ward = (WardImpl) o;

        if (id != null ? !id.equals(ward.id) : ward.id != null) return false;
        if (!name.equals(ward.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }
}
