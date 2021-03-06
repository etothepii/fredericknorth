package uk.co.epii.conservatives.fredericknorth.boundaryline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
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

    public BoundaryLineFeature(BoundedArea parent, SimpleFeature boundaryLineFeature, BoundedAreaType boundedAreaType) {
        super(parent, boundedAreaType, "");
        this.boundaryLineFeature = boundaryLineFeature;
        MultiPolygon multiPolygon = (MultiPolygon)this.boundaryLineFeature.getAttribute("the_geom");
        setName((String)this.boundaryLineFeature.getAttribute("NAME"));
        List<List<Point>> allPoints = getPoints();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Geometry geometry = multiPolygon.getGeometryN(i);
            List<Point> points = new ArrayList<Point>(geometry.getNumPoints());
            allPoints.add(points);
            Coordinate[] coordinates = geometry.getCoordinates();
            for (int j = 0; j < coordinates.length; j++) {
                points.add(new Point((int)coordinates[j].x, (int)coordinates[j].y));
            }
        }
    }
}
