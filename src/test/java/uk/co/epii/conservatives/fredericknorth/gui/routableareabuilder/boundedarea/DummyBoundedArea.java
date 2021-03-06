package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.AbstractBoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 01/08/2013
 * Time: 21:35
 */
public class DummyBoundedArea extends AbstractBoundedArea {

    public DummyBoundedArea(BoundedArea parent, BoundedAreaType type, String name, Polygon polygon) {
        super(parent, type, name);
        List<List<Point>> list = getPoints();
        List<Point> points = new ArrayList<Point>();
        list.add(points);
        for (int i = 0; i < polygon.npoints; i++) {
            points.add(new Point(polygon.xpoints[i], polygon.ypoints[i]));
        }
    }
}
