package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 17:29
 */
public interface BoundedArea {

    public String getName();
    public void setName(String name);
    public BoundedArea[] getChildren();
    public BoundedAreaType getBoundedAreaType();
    public Polygon getArea();
    public Polygon[] getEnclaves();
    public Element toXml(Document document);
    public void addChild(BoundedArea boundedAreas);
    public void save(XMLSerializer xmlSerializer, File selectedFile);
    public NearestPoint getNearestGeoPoint(Point2D.Float point);

}
