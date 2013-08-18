package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.AbstractBoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.extensions.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;

/**
 * User: James Robinson
 * Date: 01/08/2013
 * Time: 21:35
 */
public class DummyBoundedArea extends AbstractBoundedArea {

    public DummyBoundedArea(String name, Polygon polygon) {
        super(null, name);
        java.util.List<Point> points = getPoints();
        for (int i = 0; i < polygon.npoints; i++) {
            points.add(new Point(polygon.xpoints[i], polygon.ypoints[i]));
        }
    }
}
