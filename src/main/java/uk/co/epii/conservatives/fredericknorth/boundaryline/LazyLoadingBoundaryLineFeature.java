package uk.co.epii.conservatives.fredericknorth.boundaryline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 17/08/2013
 * Time: 19:49
 */
public class LazyLoadingBoundaryLineFeature extends AbstractBoundedArea {

    private static final Logger LOG_SYNC = LoggerFactory.getLogger(
            LazyLoadingBoundaryLineFeature.class.getName().concat("_sync"));

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
        LOG_SYNC.debug("Awaiting super.getPoints()");
        try {
            synchronized (super.getPoints()) {
                LOG_SYNC.debug("Received super.getPoints()");
                if (loadedPoints) return;
                MultiPolygon multiPolygon = (MultiPolygon)this.boundaryLineFeature.getAttribute("the_geom");
                for (int n = 0; n < multiPolygon.getNumGeometries(); n++) {
                    Coordinate[] coordinates = multiPolygon.getGeometryN(n).getCoordinates();
                    List<Point> points = new ArrayList<Point>();
                    for (int i = 0; i < coordinates.length; i++) {
                        points.add(new Point((int)coordinates[i].x, (int)coordinates[i].y));
                    }
                    super.getPoints().add(points);
                }
                loadedPoints = true;
            }
        }
        finally {
            LOG_SYNC.debug("Released super.getPoints()");
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
        LOG_SYNC.debug("Awaiting super.getChildrenList()");
        try {
            synchronized (super.getChildrenList()) {
                LOG_SYNC.debug("Received super.getChildrenList()");
                if (loadedChildren) return;
                List<BoundedArea> children = super.getChildrenList();
                children.addAll(boundaryLineController.getKnownDescendents(this, getBoundedAreaType().getChildType()));
            }
        }
        finally {
            LOG_SYNC.debug("Released super.getChildrenList()");
        }
    }

    @Override
    protected List<List<Point>> getPoints() {
        if (!loadedPoints) {
            loadPoints();
        }
        return super.getPoints();
    }

}
