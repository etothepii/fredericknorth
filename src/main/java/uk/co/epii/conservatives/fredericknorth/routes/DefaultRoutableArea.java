package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    private final HashMap<String, RoutableArea> children;
    private final HashMap<String, DwellingGroup> unroutedDwellingGroups;
    private final HashMap<String, DwellingGroup> routedDwellingGroups;
    private final HashMap<String, DwellingGroup> dwellingGroups;
  private Router router;

  public DefaultRoutableArea(BoundedArea boundedArea, RoutableArea parent) {
    this.boundedArea = boundedArea;
    routes = new HashSet<Route>();
    this.parent = parent;
    children = new HashMap<String, RoutableArea>();
    routedDwellingGroups = new HashMap<String, DwellingGroup>();
    unroutedDwellingGroups = new HashMap<String, DwellingGroup>();
    dwellingGroups = new HashMap<String, DwellingGroup>();
    router = new TravellingSalesmanRouter();
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
        return unroutedDwellingGroups.values();
    }

    @Override
    public Collection<? extends DwellingGroup> getRoutedDwellingGroups() {
        return routedDwellingGroups.values();
    }

    @Override
    public Collection<? extends DwellingGroup> getDwellingGroups() {
        return dwellingGroups.values();
    }

    @Override
    public Element toXml(Document document) {
        Element routableAreaElement = document.createElement("RoutableArea");
        Element boundedAreaElement = document.createElement("BoundedArea");
        routableAreaElement.appendChild(boundedAreaElement);
        Element name = document.createElement("Name");
        boundedAreaElement.appendChild(name);
        name.setTextContent(boundedArea.getName());
        Element type = document.createElement("Type");
        boundedAreaElement.appendChild(type);
        type.setTextContent(boundedArea.getBoundedAreaType().toString());
        Element routesElt = document.createElement("Routes");
        boundedAreaElement.appendChild(routesElt);
        List<Route> localRoutes = new ArrayList<Route>(routes);
        for (RoutableArea child : children.values()) {
            for (Route route : child.getRoutes()) {
                localRoutes.remove(route);
            }
        }
        for (Route route : localRoutes) {
            Element routeElt = route.toXml(document);
            routesElt.appendChild(routeElt);

        }
        List<RoutableArea> orderedChildren = new ArrayList<RoutableArea>(this.children.values());
        Collections.sort(orderedChildren, new Comparator<RoutableArea>() {
            @Override
            public int compare(RoutableArea a, RoutableArea b) {
                return a.getName().compareTo(b.getName());
            }
        });
        Element children = document.createElement("Children");
        routableAreaElement.appendChild(children);
        for (RoutableArea child : orderedChildren) {
            children.appendChild(child.toXml(document));
        }
        return routableAreaElement;
    }

    @Override
    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        if (!unroutedOnly) {
            for (Route route : routes) {
                removeRoute(route, this);
            }
        }
        for (RoutableArea child : children.values()) {
            child.autoGenerate(targetSize, unroutedOnly);
        }
        routeUnrouted(targetSize);
    }

  private void routeUnrouted(int targetSize) {
    for (Route route : router.createRoutes(this, getUnroutedIndivisbleChunks(), targetSize)) {
      addRoute(route, this);
    }
  }

  @Override
  public Route createRoute(String name) {
    Route route = new RouteImpl(this, name);
    addRoute(route, this);
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
        unroutedDwellingGroups.putAll(dwellingGroups);
        routedDwellingGroups.clear();
        for (RoutableArea childRoutableArea : children.values()) {
            childRoutableArea.removeAll();
        }
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup) {
        String key = dwellingGroup.getKey();
        if (unroutedDwellingGroups.containsKey(key)) {
            unroutedDwellingGroups.remove(dwellingGroup.getKey());
            routedDwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
            if (parent != null) {
                parent.markAsRouted(dwellingGroup, this);
            }
            for (RoutableArea child : children.values()) {
                child.markAsRouted(dwellingGroup, this);
            }
        }
    }

    @Override
    public void markAsUnrouted(DwellingGroup dwellingGroup) {
        if (routedDwellingGroups.containsKey(dwellingGroup.getKey())) {
            routedDwellingGroups.remove(dwellingGroup.getKey());
            unroutedDwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
            if (parent != null) {
                parent.markAsUnrouted(dwellingGroup, this);
            }
            for (RoutableArea child : children.values()) {
                child.markAsUnrouted(dwellingGroup, this);
            }
        }
    }

    @Override
    public void markAsRouted(DwellingGroup dwellingGroup, RoutableArea informant) {
        if (unroutedDwellingGroups.containsKey(dwellingGroup.getKey())) {
            unroutedDwellingGroups.remove(dwellingGroup.getKey());
            routedDwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
            if (informant == parent) {
                for (RoutableArea child : children.values()) {
                    child.markAsRouted(dwellingGroup, this);
                }
            }
            else if (children.containsKey(informant.getName())) {
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
        if (routedDwellingGroups.containsKey(dwellingGroup.getKey())) {
            routedDwellingGroups.remove(dwellingGroup.getKey());
            unroutedDwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
            if (informant == parent) {
                for (RoutableArea child : children.values()) {
                    child.markAsUnrouted(dwellingGroup, this);
                }
            }
            else if (children.containsKey(informant.getName())) {
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
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        Element routableAreasElt = toXml(document);
        document.appendChild(routableAreasElt);
        try {
          FileWriter fileWriter = new FileWriter(selectedFile);
          PrintWriter printWriter = new PrintWriter(fileWriter);
          printWriter.print(new XMLSerializerImpl().toString(document));
          printWriter.flush();
          printWriter.close();
          fileWriter.close();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
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
                children: for (RoutableArea child : children.values()) {
                  for (DwellingGroup dwellingGroup : route.getDwellingGroups()) {
                    if (!child.getDwellingGroups().contains(dwellingGroup)) {
                      continue children;
                    }
                  }
                  child.addRoute(route, this);
                }
            }
            else if (informant == this || children.containsKey(informant.getName())) {
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
                for (RoutableArea child : children.values()) {
                    child.removeRoute(route, this);
                }
            }
            else if (children.containsKey(informant.getName())) {
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
    public void load(Element element) {
        if (!boundedArea.getName().equals(extractName(element))) {
            throw new IllegalArgumentException("The element provided does not represent with this RoutableArea");
        }
        Element boundedArea = (Element)element.getElementsByTagName("BoundedArea").item(0);
        NodeList routeTags = ((Element)boundedArea.getElementsByTagName("Routes").item(0)).getElementsByTagName("Route");
        for (int i = 0; i < routeTags.getLength(); i++) {
            loadRoute((Element)routeTags.item(i));
        }
        NodeList childTags = ((Element)element.getElementsByTagName("Children").item(0)).getElementsByTagName("RoutableArea");
        for (int i = 0; i < childTags.getLength(); i++) {
            loadRoutableArea((Element) childTags.item(i));
        }
    }

    private void loadRoutableArea(Element item) {
        String name = extractName(item);
        RoutableArea routableArea = children.get(name);
        if (routableArea != null) {
            routableArea.load(item);
        }
    }

    private void loadRoute(Element routeElt) {
        String routeName = routeElt.getElementsByTagName("Name").item(0).getTextContent();
        Route route = createRoute(routeName);
        NodeList dwellingGroups = ((Element)routeElt.getElementsByTagName("DwellingGroups").item(0)).getElementsByTagName("DwellingGroup");
        for (int i = 0; i < dwellingGroups.getLength(); i++) {
            Element dwellingGroup = (Element) dwellingGroups.item(i);
            String key = dwellingGroup.getElementsByTagName("Key").item(0).getTextContent();
            route.addDwellingGroups(Arrays.asList(unroutedDwellingGroups.get(key)));
        }
    }

    private String extractName(Element element) {
        if (!element.getTagName().equals("RoutableArea")) {
            throw new IllegalArgumentException("The tag provided is not for a RoutableArea");
        }
        Element boundedArea = (Element)element.getElementsByTagName("BoundedArea").item(0);
        return boundedArea.getElementsByTagName("Name").item(0).getTextContent();
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

    @Override
    public void addDwellingGroup(DwellingGroup dwellingGroup, boolean routed) {
        if (parent != null) {
            parent.addDwellingGroup(dwellingGroup, routed);
        }
        dwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
        if (routed) {
            routedDwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
        }
        else {
            unroutedDwellingGroups.put(dwellingGroup.getKey(), dwellingGroup);
        }
    }

  @Override
  public void setRouter(Router router) {
    this.router = router;
  }

  public void addChild(RoutableArea childRoutableArea) {
        children.put(childRoutableArea.getName(), childRoutableArea);
    }

    public Collection<IndivisbleChunk> getUnroutedIndivisbleChunks() {
        HashMap<String, IndivisbleChunk> indivisbleChunks = new HashMap<String, IndivisbleChunk>();
        for (DwellingGroup dwellingGroup : unroutedDwellingGroups.values()) {
            IndivisbleChunk indivisbleChunk = indivisbleChunks.get(dwellingGroup.getPostcode().getName());
            if (indivisbleChunk == null) {
                indivisbleChunk = new IndivisbleChunk();
                indivisbleChunks.put(dwellingGroup.getPostcode().getName(), indivisbleChunk);
            }
            indivisbleChunk.add(dwellingGroup);
        }
        return indivisbleChunks.values();
    }

}
