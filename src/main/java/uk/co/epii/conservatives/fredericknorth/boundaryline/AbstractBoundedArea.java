package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 17:31
 */
public class AbstractBoundedArea implements BoundedArea, Iterable<BoundedArea> {

    private String name;
    private final BoundedAreaType type;
    protected final List<Point> _points;
    protected final List<List<Point>> _enclavePoints;
    protected final List<BoundedArea> _childrenList;

    protected AbstractBoundedArea(BoundedAreaType type, String name) {
        this.type = type;
        this.name = name;
        _points = new ArrayList<Point>();
        _enclavePoints = new ArrayList<List<Point>>();
        _childrenList = new ArrayList<BoundedArea>();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public BoundedArea[] getChildren() {
        List<BoundedArea> children = getChildrenList();
        return children.toArray(new BoundedArea[children.size()]);
    }

    @Override
    public BoundedAreaType getBoundedAreaType() {
        return type;
    }

    @Override
    public Polygon getArea() {
        return getPolygon(getPoints());
    }

    @Override
    public Polygon[] getEnclaves() {
        List<List<Point>> enclavePoints = getEnclavePoints();
        Polygon[] enclaves = new Polygon[enclavePoints.size()];
        for (int i = 0; i < enclaves.length; i++) {
            enclaves[i] = getPolygon(enclavePoints.get(i));
        }
        return enclaves;
    }

    private static Polygon getPolygon(List<Point> points) {
        int[] x = new int[points.size()];
        int[] y = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            x[i] = points.get(i).x;
            y[i] = points.get(i).y;
        }
        return new Polygon(x, y, points.size());
    }

    @Override
    public Element toXml(Document document) {
        Element boundedAreaElt = document.createElement("BoundedArea");
        Element typeElt = document.createElement("Type");
        typeElt.setTextContent(getBoundedAreaType().name());
        boundedAreaElt.appendChild(typeElt);
        Element nameElt = document.createElement("Name");
        nameElt.setTextContent(getName());
        boundedAreaElt.appendChild(nameElt);
        Element areaElt = document.createElement("Area");
        boundedAreaElt.appendChild(areaElt);
        Element pointsElt = document.createElement("Points");
        areaElt.appendChild(pointsElt);
        for (Point point : getPoints()) {
            Element pointElt = document.createElement("Point");
            pointsElt.appendChild(pointElt);
            Element xElt = document.createElement("X");
            xElt.setTextContent(point.getX() + "");
            pointElt.appendChild(xElt);
            Element yElt = document.createElement("Y");
            yElt.setTextContent(point.getY() + "");
            pointElt.appendChild(yElt);
        }
        Element enclavesElt = document.createElement("Enclaves");
        boundedAreaElt.appendChild(enclavesElt);
        for (List<Point> enclave : getEnclavePoints()) {
            Element enclaveElt = document.createElement("Enclave");
            enclaveElt.appendChild(enclaveElt);
            for (Point point : enclave) {
                Element pointElt = document.createElement("Point");
                enclaveElt.appendChild(pointElt);
                Element xElt = document.createElement("X");
                xElt.setTextContent(point.getX() + "");
                pointElt.appendChild(xElt);
                Element yElt = document.createElement("Y");
                yElt.setTextContent(point.getY() + "");
                pointElt.appendChild(yElt);
            }
        }
        Element childrenElt = document.createElement("Children");
        boundedAreaElt.appendChild(childrenElt);
        for (BoundedArea child : getChildrenList()) {
            childrenElt.appendChild(child.toXml(document));
        }
        return boundedAreaElt;
    }

    @Override
    public Iterator<BoundedArea> iterator() {
        return getChildrenList().iterator();
    }

    @Override
    public void addChild(BoundedArea boundedAreas) {
        getChildrenList().add(boundedAreas);
    }

    @Override
    public void save(XMLSerializer xmlSerializer, File file) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        Element xml = toXml(document);
        document.appendChild(xml);
        String toWrite = xmlSerializer.toString(document);
        try {
            FileUtils.writeStringToFile(file, toWrite);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NearestPoint getNearestGeoPoint(Point2D.Float point) {
        return PolygonExtensions.getNearestPoint(getArea(), point);
    }

    protected List<Point> getPoints() {
        return _points;
    }

    protected List<BoundedArea> getChildrenList() {
        return _childrenList;
    }

    protected List<List<Point>> getEnclavePoints() {
        return _enclavePoints;
    }
}
