package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import uk.co.epii.conservatives.fredericknorth.maps.Location;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 01:17
 */
public class MeetingPoint implements Location {

    private String name;
    private Point point;

    public MeetingPoint() {
    }

    public MeetingPoint(String name, Point point) {
        this.name = name;
        this.point = point;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Element toXml(Document document) {
        Element elt = document.createElement("MeetingPoint");
        Element nameElt = document.createElement("Name");
        Element pointElt = document.createElement("Point");
        Element xElt = document.createElement("X");
        Element yElt = document.createElement("Y");
        elt.appendChild(nameElt);
        elt.appendChild(pointElt);
        pointElt.appendChild(xElt);
        pointElt.appendChild(yElt);
        nameElt.setTextContent(name);
        xElt.setTextContent(point.getX() + "");
        yElt.setTextContent(point.getY() + "");
        return elt;
    }

    public static MeetingPoint parse(Element element) {
        String name = element.getElementsByTagName("Name").item(0).getTextContent();
        Element pointElt = (Element)element.getElementsByTagName("Point").item(0);
        double x = Double.parseDouble(pointElt.getElementsByTagName("X").item(0).getTextContent());
        double y = Double.parseDouble(pointElt.getElementsByTagName("Y").item(0).getTextContent());
        return new MeetingPoint(name, new Point((int)x, (int)y));
    }
}
