package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.extensions.PolygonExtensions;

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
        synchronized (simpleFeatureCollections) {
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

    @Override
    public List<? extends BoundedArea> getAllOSKnownLazyBoundaryLineFeatures(BoundedAreaType type) {
        List<LazyLoadingBoundaryLineFeature> lazyBoundaryLineFeatureList = lazyBoundaryLineFeatureLists.get(type);
        if (lazyBoundaryLineFeatureList == null) {
            lazyBoundaryLineFeatureList = loadLazyBoundaryLineFeatureList(type);
        }
        return lazyBoundaryLineFeatureList;
    }

    private List<LazyLoadingBoundaryLineFeature> loadLazyBoundaryLineFeatureList(BoundedAreaType type) {
        synchronized (lazyBoundaryLineFeatureLists) {
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

    @Override
    public List<BoundedArea> getKnownDescendents(BoundedArea parent, BoundedAreaType childType) {
        if (!Arrays.asList(parent.getBoundedAreaType().getAllPossibleDecendentTypes()).contains(childType)) {
            return new ArrayList<BoundedArea>();
        }
        return getFeaturesContainedWithin(childType, parent.getArea());
    }

    @Override
    public BoundedArea getContainingFeature(BoundedAreaType type, double x, double y) {
        SimpleFeatureIterator simpleFeatures =
                getAllOSKnownBoundedAreas(type).features();
        while (simpleFeatures.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatures.next();
            BoundingBox boundingBox = simpleFeature.getBounds();
            if (boundingBox.contains(x, y)) {
                BoundaryLineFeature boundaryLineFeature =
                        new BoundaryLineFeature(simpleFeature, type);
                if (boundaryLineFeature.getArea().contains(x, y)) {
                    return boundaryLineFeature;
                }
            }
        }
        return null;
    }

    @Override
    public List<BoundedArea> getFeaturesContainedWithin(BoundedAreaType type, Shape s) {
        ArrayList<BoundedArea> boundedAreas = new ArrayList<BoundedArea>();
        SimpleFeatureCollection simpleFeaturesCollection =
                getAllOSKnownBoundedAreas(type);
        if (simpleFeaturesCollection == null) {
            LOG.debug("No SimpleFeaturesCollection found for: {}", type);
            return new ArrayList<BoundedArea>();
        }
        SimpleFeatureIterator simpleFeatures = simpleFeaturesCollection.features();
        Rectangle shapeBounds = s.getBounds();
        while (simpleFeatures.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatures.next();
            BoundingBox boundingBox = simpleFeature.getBounds();
            Rectangle slightlyShrunkBounds = slightlyShrink(boundingBox);
            if (shapeBounds.contains(slightlyShrunkBounds)) {
                LazyLoadingBoundaryLineFeature lazyLoadingBoundaryLineFeature =
                        new LazyLoadingBoundaryLineFeature(this, simpleFeature, type);
                Point2D.Float centreOfGravity =
                        PolygonExtensions.getCentreOfGravity(lazyLoadingBoundaryLineFeature.getArea());
                if (lazyLoadingBoundaryLineFeature.getArea().contains(centreOfGravity)) {
                    if (s.contains(centreOfGravity)) {
                        boundedAreas.add(lazyLoadingBoundaryLineFeature);
                    }
                }
                else {
                    LOG.warn("This {}, {}, has a centre of gravity outside itself",
                            new Object[] {type, lazyLoadingBoundaryLineFeature.getName()});
                    if (s.contains(centreOfGravity)) {
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
