package uk.co.epii.conservatives.fredericknorth.geometry;

import uk.co.epii.conservatives.fredericknorth.geometry.PolygonSifter;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 23/02/2014
 * Time: 02:05
 */
public class BruteForcePolygonSifterImpl implements PolygonSifter {

    private final Polygon[] polygons;

    public BruteForcePolygonSifterImpl(Polygon[] polygons) {
        this.polygons = polygons;
    }

    @Override
    public boolean contains(Point p) {
        return PolygonExtensions.contains(polygons, p);
    }
}
