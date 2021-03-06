package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:54
 */
class RouteImpl implements Route {

    private UUID uuid;
    private Set<DwellingGroup> dwellingGroups;
    private String name;
    private RoutableArea routableArea;
    private String association = null;

    public RouteImpl(RoutableArea routableArea, String name) {
        this.name = name;
        this.routableArea = routableArea;
        dwellingGroups = new HashSet<DwellingGroup>();
    }

  public UUID getUuid() {
    if (uuid == null) {
      uuid = UUID.randomUUID();
    }
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
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
    public int getDwellingCount() {
        int count = 0;
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            count += dwellingGroup.size();
        }
        return count;
    }

    @Override
    public void addDwellingGroups(Collection<? extends DwellingGroup> dwellingGroups) {
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            if (!this.dwellingGroups.contains(dwellingGroup)) {
                routableArea.markAsRouted(dwellingGroup);
                this.dwellingGroups.add(dwellingGroup);
            }
        }
    }

    @Override
    public void removeDwellingGroups(Collection<? extends DwellingGroup> dwellingGroups) {
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            this.dwellingGroups.remove(dwellingGroup);
            routableArea.markAsUnrouted(dwellingGroup);
        }
    }

    @Override
    public void load(ApplicationContext applicationContext, Element routeElt) {
        if (!routeElt.getTagName().equals("Route")) throw new IllegalArgumentException("You have not provided a Route node");
        String routeName = routeElt.getElementsByTagName("Name").item(0).getTextContent();
        if (!name.equals(routeName)) {
            throw new RuntimeException("This is not the Route for this node as the names differ");
        }
        DwellingGroupFactory dwellingGroupFactory = applicationContext.getDefaultInstance(DwellingGroupFactory.class);
        NodeList associationList = routeElt.getElementsByTagName("Association");
        if (associationList.getLength() == 0) {
            setAssociation(null);
        }
        else {
            setAssociation(associationList.item(0).getTextContent());
        }
        Element dwellingGroupsElt = (Element)routeElt.getElementsByTagName("DwellingGroups").item(0);
        NodeList dwellingGroupNodeList = dwellingGroupsElt.getElementsByTagName("DwellingGroup");
        List<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>(dwellingGroupNodeList.getLength());
        for (int i = 0; i < dwellingGroupNodeList.getLength(); i++) {
            Element dwellingGroupElt = (Element)dwellingGroupNodeList.item(i);
            String dwellingGroupKey = dwellingGroupElt.getElementsByTagName("Key").item(0).getTextContent();
            String x = dwellingGroupElt.getElementsByTagName("X").item(0).getTextContent();
            String y = dwellingGroupElt.getElementsByTagName("Y").item(0).getTextContent();
            Point point = new Point(Integer.parseInt(x), Integer.parseInt(y));
            DwellingGroup dwellingGroup =
                    dwellingGroupFactory.load(point, dwellingGroupKey);
            dwellingGroups.add(dwellingGroup);
        }
        addDwellingGroups(dwellingGroups);
    }

    @Override
    public String getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(String association) {
        this.association = association;
    }

    @Override
    public String getFullyQualifiedName() {
        StringBuilder stringBuilder = new StringBuilder(255);
        stringBuilder.append(getName());
        RoutableArea parent = routableArea;
        do {
            stringBuilder.insert(0, " - ");
            stringBuilder.insert(0, parent.getName());
        } while ((parent = parent.getParent()) != null);
        return stringBuilder.toString();
    }

    @Override
    public Set<DwellingGroup> getDwellingGroups() {
        return dwellingGroups;
    }

    @Override
    public Element toXml(Document document) {
        Element route = document.createElement("Route");
        Element uuidElt = document.createElement("UUID");
        uuidElt.setTextContent(getUuid().toString());
        route.appendChild(uuidElt);
        Element nameElt = document.createElement("Name");
        nameElt.setTextContent(name);
        route.appendChild(nameElt);
        if (association != null) {
            Element association = document.createElement("Association");
            association.setTextContent(this.association);
            route.appendChild(association);
        }
        Element dwellingGroupsElt = document.createElement("DwellingGroups");
        route.appendChild(dwellingGroupsElt);
        List<DwellingGroup> orderedDwellingGroups = new ArrayList(dwellingGroups);
        Collections.sort(orderedDwellingGroups, new Comparator<DwellingGroup>() {
            @Override
            public int compare(DwellingGroup o1, DwellingGroup o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        for (DwellingGroup dwellingGroup : orderedDwellingGroups) {
            dwellingGroupsElt.appendChild(dwellingGroup.toXml(document));
        }
        return route;
    }

    @Override
    public RoutableArea getRoutableArea() {
        return routableArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteImpl route = (RouteImpl) o;

        if (!name.equals(route.name)) return false;
        if (!routableArea.equals(route.routableArea)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + routableArea.hashCode();
        return result;
    }
}
