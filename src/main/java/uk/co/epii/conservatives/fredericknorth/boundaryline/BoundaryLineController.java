package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 28/07/2013
 * Time: 14:46
 */
public interface BoundaryLineController {

    public SimpleFeatureCollection getAllOSKnownBoundedAreas(BoundedAreaType type);
    public List<? extends BoundedArea> getAllOSKnownLazyBoundaryLineFeatures(BoundedAreaType type);
    public List<BoundedArea> getKnownDescendents(BoundedArea parent, BoundedAreaType type);
    public BoundedArea getContainingFeature(BoundedAreaType type, double x, double y);
    public BoundedArea getContainingFeature(BoundedAreaType type, Point p);
    public List<BoundedArea> getFeaturesContainedWithin(BoundedAreaType type, Shape s);
}
