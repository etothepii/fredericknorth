package uk.co.epii.conservatives.fredericknorth.boundaryline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 17/08/2013
 * Time: 19:49
 */
public class LazyLoadingBoundaryLineFeature extends AbstractBoundedArea {

    private final SimpleFeature boundaryLineFeature;
    private final BoundaryLineController boundaryLineController;
    private volatile boolean loadedPoints = false;
    private volatile boolean loadedChildren = false;

    LazyLoadingBoundaryLineFeature(BoundaryLineController boundaryLineController,
                                   SimpleFeature boundaryLineFeature, BoundedAreaType boundedAreaType) {
        super(boundedAreaType, "");
        this.boundaryLineController = boundaryLineController;
        this.boundaryLineFeature = boundaryLineFeature;
        setName((String)this.boundaryLineFeature.getAttribute("NAME"));
    }

    @Override
    protected List<List<Point>> getEnclavePoints() {
        if (!loadedPoints) {
            loadPoints();
        }
        return super.getEnclavePoints();
    }

    private void loadPoints() {
        synchronized (super.getPoints()) {
            if (loadedPoints) return;
            MultiPolygon multiPolygon = (MultiPolygon)this.boundaryLineFeature.getAttribute("the_geom");
            Coordinate[] coordinates = multiPolygon.getBoundary().getCoordinates();
            List<Point> points = super.getPoints();
            for (int i = 0; i < coordinates.length; i++) {
                points.add(new Point((int)coordinates[i].x, (int)coordinates[i].y));
            }
            loadedPoints = true;
        }
    }

    @Override
    protected List<BoundedArea> getChildrenList() {
        if (!loadedChildren) {
            loadChildren();
        }
        return super.getChildrenList();
    }

    private void loadChildren() {
        synchronized (super.getChildrenList()) {
            if (loadedChildren) return;
            List<BoundedArea> children = super.getChildrenList();
            children.addAll(boundaryLineController.getKnownDescendents(this, getBoundedAreaType().getChildType()));
        }
    }

    @Override
    protected List<Point> getPoints() {
        if (!loadedPoints) {
            loadPoints();
        }
        return super.getPoints();
    }

}
