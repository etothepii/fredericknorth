package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User: James Robinson
 * Date: 28/07/2013
 * Time: 15:05
 */
public class DefaultBoundedArea extends AbstractBoundedArea {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBoundedArea.class);

    public DefaultBoundedArea(BoundedAreaType type, String name) {
        super(type, name);
    }

    public DefaultBoundedArea(BoundedArea boundedArea) {
        super(boundedArea.getBoundedAreaType(), boundedArea.getName());
        for (Polygon polygon : boundedArea.getAreas()) {
            List<Point> points = new ArrayList<Point>();
            getPoints().add(points);
            for (int i = 0; i < polygon.npoints; i++)  {
                points.add(new Point(polygon.xpoints[i], polygon.ypoints[i]));
            }
        }
        for (BoundedArea child : boundedArea.getChildren()) {
            addChild(child);
        }
        for (Polygon enclave : boundedArea.getEnclaves()) {
            List<Point> enclavePoints = new ArrayList<Point>(enclave.npoints);
            for (int i = 0; i < enclave.npoints; i++) {
                enclavePoints.add(new Point(enclave.xpoints[i], enclave.ypoints[i]));
            }
            getEnclavePoints().add(enclavePoints);
        }
    }

    public static DefaultBoundedArea load(Element boundedArea) {
        if (!boundedArea.getTagName().equals("BoundedArea"))
            throw new IllegalArgumentException("The element provided is not a BoundedArea");

        String name = boundedArea.getElementsByTagName("Name").item(0).getTextContent();
        BoundedAreaType boundedAreaType = BoundedAreaType.valueOf(
                boundedArea.getElementsByTagName("Type").item(0).getTextContent());
        DefaultBoundedArea defaultBoundedArea = new DefaultBoundedArea(boundedAreaType, name);
        NodeList uuidList = boundedArea.getElementsByTagName("UUID");
        if (uuidList.getLength() == 0) {
          LOG.warn("Loaded BoundedArea without a uuid");
        }
        else {
          String uuidStr = uuidList.item(0).getTextContent();
          defaultBoundedArea.setUuid(UUID.fromString(uuidStr));
        }
        Element areaElt = (Element)boundedArea.getElementsByTagName("Area").item(0);
        Element allPointsElt = (Element)areaElt.getElementsByTagName("AllPoints").item(0);
        defaultBoundedArea.getPoints().addAll(getAllPointsFromNode(allPointsElt == null ? areaElt : allPointsElt));
        Element enclavesElt = (Element)areaElt.getElementsByTagName("Enclaves").item(0);
        if (enclavesElt != null) {
            NodeList enclavesList = enclavesElt.getElementsByTagName("Enclave");
            for (int i = 0; i < enclavesList.getLength(); i++) {
                Element enclave = (Element)enclavesList.item(i);
                defaultBoundedArea.getEnclavePoints().add(getPointsFromNode(enclave));
            }
        }
        Element children = (Element)boundedArea.getElementsByTagName("Children").item(0);
        if (children != null) {
            NodeList childrenList = children.getElementsByTagName("BoundedArea");
            for (int i = 0; i < childrenList.getLength(); i++) {
                Element child = (Element)childrenList.item(i);
                if (child.getParentNode() == children) {
                    DefaultBoundedArea loaded = load(child);
                    if (loaded.getBoundedAreaType() == boundedAreaType.getChildType()) {
                        defaultBoundedArea.getChildrenList().add(loaded);
                    }
                }
            }
        }
        return defaultBoundedArea;
    }

    private static List<List<Point>> getAllPointsFromNode(Element allPointsElt) {
        List<List<Point>> allPoints = new ArrayList<List<Point>>();
        NodeList pointsList = allPointsElt.getElementsByTagName("Points");
        for (int i = 0; i < pointsList.getLength(); i++) {
            Element points = (Element)pointsList.item(i);
            allPoints.add(getPointsFromNode(points));
        }
        return allPoints;
    }

    private static List<Point> getPointsFromNode(Element points) {
        NodeList nodeList = points.getElementsByTagName("Point");
        List<Point> list = new ArrayList<Point>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element pointElt = (Element)nodeList.item(i);
            int x = (int)Float.parseFloat(pointElt.getElementsByTagName("X").item(0).getTextContent());
            int y = (int)Float.parseFloat(pointElt.getElementsByTagName("Y").item(0).getTextContent());
            list.add(new Point(x, y));
        }
        return list;
    }
}
