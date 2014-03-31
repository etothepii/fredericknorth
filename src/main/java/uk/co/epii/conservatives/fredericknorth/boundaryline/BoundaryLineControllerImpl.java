package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.robertwalpole.DataSet;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 11/08/2013
 * Time: 22:24
 */
public class BoundaryLineControllerImpl implements BoundaryLineController {

    private static final Logger LOG = LoggerFactory.getLogger(BoundaryLineControllerImpl.class);
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(BoundaryLineControllerImpl.class.getName().concat("_sync"));

    private final Map<BoundedAreaType, DataSet> dataSets;
    private final Map<BoundedAreaType, SimpleFeatureCollection> simpleFeatureCollections;
    private final Map<BoundedAreaType, List<LazyLoadingBoundaryLineFeature>> lazyBoundaryLineFeatureLists;

    public BoundaryLineControllerImpl(Map<BoundedAreaType, DataSet> dataSets) {
        this.dataSets = dataSets;
        simpleFeatureCollections = new EnumMap<BoundedAreaType, SimpleFeatureCollection>(BoundedAreaType.class);
        lazyBoundaryLineFeatureLists = new EnumMap<BoundedAreaType, List<LazyLoadingBoundaryLineFeature>>(BoundedAreaType.class);
    }

    @Override
    public SimpleFeatureCollection getAllOSKnownBoundedAreas(BoundedAreaType type) {
        DataSet dataSet = dataSets.get(type);
        if (dataSet == null) {
            return null;
        }
        SimpleFeatureCollection simpleFeatureCollection = simpleFeatureCollections.get(type);
        if (simpleFeatureCollection == null) {
            simpleFeatureCollection = loadSimpleFeatureCollection(type, dataSet);
        }
        return simpleFeatureCollection;
    }

    private SimpleFeatureCollection loadSimpleFeatureCollection(BoundedAreaType type, DataSet dataSet) {
        LOG_SYNC.debug("Awaiting simpleFeatureCollections");
        try {
            synchronized (simpleFeatureCollections) {
                LOG_SYNC.debug("Received simpleFeatureCollections");
                SimpleFeatureCollection simpleFeatureCollection = simpleFeatureCollections.get(type);
                if (simpleFeatureCollection != null) return simpleFeatureCollection;
                try {
                    simpleFeatureCollection = dataSet.getFeatureSource().getFeatures();
                    simpleFeatureCollections.put(type, simpleFeatureCollection);
                    return simpleFeatureCollection;
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released simpleFeatureCollections");
        }
    }

    @Override
    public List<? extends BoundedArea> getAllOSKnownLazyBoundaryLineFeatures(BoundedAreaType type) {
        List<LazyLoadingBoundaryLineFeature> lazyBoundaryLineFeatureList = lazyBoundaryLineFeatureLists.get(type);
        if (lazyBoundaryLineFeatureList == null) {
            lazyBoundaryLineFeatureList = loadLazyBoundaryLineFeatureList(type);
        }
        return lazyBoundaryLineFeatureList;
    }

    private List<LazyLoadingBoundaryLineFeature> loadLazyBoundaryLineFeatureList(BoundedAreaType type) {
        LOG_SYNC.debug("Awaiting lazyBoundaryLineFeatureLists");
        try {
            synchronized (lazyBoundaryLineFeatureLists) {
                LOG_SYNC.debug("Received lazyBoundaryLineFeatureLists");
                List<LazyLoadingBoundaryLineFeature> lazyBoundaryLineFeatureList = lazyBoundaryLineFeatureLists.get(type);
                if (lazyBoundaryLineFeatureList != null) {
                    return lazyBoundaryLineFeatureList;
                }
                SimpleFeatureCollection allOSKnownBoundedAreas = getAllOSKnownBoundedAreas(type);
                lazyBoundaryLineFeatureList = new ArrayList<LazyLoadingBoundaryLineFeature>(allOSKnownBoundedAreas.size());
                SimpleFeatureIterator simpleFeatureIterator = allOSKnownBoundedAreas.features();
                while (simpleFeatureIterator.hasNext()) {
                    lazyBoundaryLineFeatureList.add(new LazyLoadingBoundaryLineFeature(this, simpleFeatureIterator.next(), type));
                }
                lazyBoundaryLineFeatureLists.put(type, lazyBoundaryLineFeatureList);
                return lazyBoundaryLineFeatureList;
            }
        }
        finally {
            LOG_SYNC.debug("Released lazyBoundaryLineFeatureLists");
        }
    }

    @Override
    public List<BoundedArea> getKnownDescendents(BoundedArea parent, BoundedAreaType childType) {
        if (!Arrays.asList(parent.getBoundedAreaType().getAllPossibleDecendentTypes()).contains(childType)) {
            return new ArrayList<BoundedArea>();
        }
        return getFeaturesContainedWithin(childType, parent.getAreas());
    }

    @Override
    public BoundedArea getContainingFeature(BoundedAreaType type, double x, double y) {
        SimpleFeatureIterator simpleFeatures =
                getAllOSKnownBoundedAreas(type).features();
        while (simpleFeatures.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatures.next();
            LOG.debug("Checking: {}", simpleFeature.getAttribute("NAME"));
            BoundingBox boundingBox = simpleFeature.getBounds();
            if (boundingBox.contains(x, y)) {
                BoundaryLineFeature boundaryLineFeature =
                        new BoundaryLineFeature(simpleFeature, type);
                if (PolygonExtensions.contains(boundaryLineFeature.getAreas(), x, y)) {
                    return boundaryLineFeature;
                }
            }
        }
        return null;
    }

    @Override
    public List<BoundedArea> getFeaturesContainedWithin(BoundedAreaType type, Polygon[] polygons) {
        ArrayList<BoundedArea> boundedAreas = new ArrayList<BoundedArea>();
        SimpleFeatureCollection simpleFeaturesCollection =
                getAllOSKnownBoundedAreas(type);
        if (simpleFeaturesCollection == null) {
            LOG.debug("No SimpleFeaturesCollection found for: {}", type);
            return new ArrayList<BoundedArea>();
        }
        SimpleFeatureIterator simpleFeatures = simpleFeaturesCollection.features();
        Rectangle shapeBounds = PolygonExtensions.getBounds(polygons);
        while (simpleFeatures.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatures.next();
            BoundingBox boundingBox = simpleFeature.getBounds();
            Rectangle slightlyShrunkBounds = slightlyShrink(boundingBox);
            if (shapeBounds.contains(slightlyShrunkBounds)) {
                LazyLoadingBoundaryLineFeature lazyLoadingBoundaryLineFeature =
                        new LazyLoadingBoundaryLineFeature(this, simpleFeature, type);
                Point2D.Float centreOfGravity =
                        PolygonExtensions.getCentreOfGravity(lazyLoadingBoundaryLineFeature.getAreas());
                if (PolygonExtensions.contains(lazyLoadingBoundaryLineFeature.getAreas(), centreOfGravity)) {
                    if (PolygonExtensions.contains(polygons, centreOfGravity)) {
                        boundedAreas.add(lazyLoadingBoundaryLineFeature);
                    }
                }
                else {
                    LOG.warn("This {}, {}, has a centre of gravity outside itself",
                            new Object[] {type, lazyLoadingBoundaryLineFeature.getName()});
                    if (PolygonExtensions.contains(polygons, centreOfGravity)) {
                        boundedAreas.add(lazyLoadingBoundaryLineFeature);
                    }
                }
            }
        }
        return boundedAreas;
    }

    private Rectangle slightlyShrink(BoundingBox boundingBox) {
        double x = boundingBox.getMinX();
        double y = boundingBox.getMinY();
        double w = boundingBox.getMaxX() - x;
        double h = boundingBox.getMaxY() - y;
        return new Rectangle((int)(x + w/10), (int)(y + h/10), (int)(w * 4 / 5), (int)(h * 4 / 5));
    }

    @Override
    public BoundedArea getContainingFeature(BoundedAreaType type, Point p) {
        return getContainingFeature(type, p.getX(), p.getY());
    }

}
