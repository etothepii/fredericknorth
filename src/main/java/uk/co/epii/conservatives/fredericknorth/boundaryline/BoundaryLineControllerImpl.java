package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;

import java.awt.*;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 11/08/2013
 * Time: 22:24
 */
public class BoundaryLineControllerImpl implements BoundaryLineController {

    private final Map<BoundedAreaType, DataSet> dataSets;
    private final Map<BoundedAreaType, SimpleFeatureCollection> simpleFeatureCollections;

    public BoundaryLineControllerImpl(Map<BoundedAreaType, DataSet> dataSets) {
        this.dataSets = dataSets;
        simpleFeatureCollections = new EnumMap<BoundedAreaType, SimpleFeatureCollection>(BoundedAreaType.class);
    }

    @Override
    public SimpleFeatureCollection getAllOSKnownBoundedAreas(BoundedAreaType type) {
        DataSet dataSet = dataSets.get(type);
        if (dataSet == null) {
            return null;
        }
        SimpleFeatureCollection simpleFeatureCollection = simpleFeatureCollections.get(type);
        if (simpleFeatureCollection == null) {
            try {
                simpleFeatureCollection = dataSet.getFeatureSource().getFeatures();
                simpleFeatureCollections.put(type, simpleFeatureCollection);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return simpleFeatureCollection;
    }

    @Override
    public List<BoundedArea> getKnownChildren(BoundedArea parent, BoundedAreaType type) {
        throw new UnsupportedOperationException("This feature is not currently supported");
    }

    @Override
    public BoundedArea getContainingFeature(BoundedAreaType type, double x, double y) {
        SimpleFeatureIterator constituencies =
                getAllOSKnownBoundedAreas(BoundedAreaType.PARLIAMENTARY_CONSTITUENCY).features();
        while (constituencies.hasNext()) {
            SimpleFeature constituency = constituencies.next();
            BoundingBox boundingBox = constituency.getBounds();
            if (boundingBox.contains(x, y)) {
                BoundaryLineFeature boundaryLineFeature =
                        new BoundaryLineFeature(constituency, BoundedAreaType.PARLIAMENTARY_CONSTITUENCY);
                if (boundaryLineFeature.getArea().contains(x, y)) {
                    return boundaryLineFeature;
                }
            }
        }
        return null;
    }

    @Override
    public BoundedArea getContainingFeature(BoundedAreaType type, Point p) {
        return getContainingFeature(type, p.getX(), p.getY());
    }

}
