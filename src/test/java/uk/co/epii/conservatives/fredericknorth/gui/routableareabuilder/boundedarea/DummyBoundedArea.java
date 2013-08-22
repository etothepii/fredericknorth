package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.AbstractBoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 01/08/2013
 * Time: 21:35
 */
public class DummyBoundedArea extends AbstractBoundedArea {

    public DummyBoundedArea(BoundedAreaType type, String name, Polygon polygon) {
        super(type, name);
        java.util.List<Point> points = getPoints();
        for (int i = 0; i < polygon.npoints; i++) {
            points.add(new Point(polygon.xpoints[i], polygon.ypoints[i]));
        }
    }
}
