package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.AbstractBoundedArea;

import java.awt.*;

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
