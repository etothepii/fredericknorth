package uk.co.epii.conservatives.fredericknorth.boundaryline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 10:57
 */
public class BoundaryLineFeature extends AbstractBoundedArea {

    private SimpleFeature boundaryLineFeature;

    public BoundaryLineFeature(SimpleFeature boundaryLineFeature, BoundedAreaType boundedAreaType) {
        super(boundedAreaType, "");
        this.boundaryLineFeature = boundaryLineFeature;
        MultiPolygon multiPolygon = (MultiPolygon)this.boundaryLineFeature.getAttribute("the_geom");
        setName((String)this.boundaryLineFeature.getAttribute("NAME"));
        Coordinate[] coordinates = multiPolygon.getBoundary().getCoordinates();
        List<Point> points = getPoints();
        for (int i = 0; i < coordinates.length; i++) {
            points.add(new Point((int)coordinates[i].x, (int)coordinates[i].y));
        }
    }
}
