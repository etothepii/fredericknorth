package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.MapImageObserver;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewChangedTranslationListener;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 16:04
 */
public class DummyMapViewGenerator implements MapViewGenerator {

    private Point geoCenter;
    private Dimension viewPortSize;
    private double scale;
    private MapView mapView;

    @Override
    public Point getGeoCenter() {
        return geoCenter;
    }

    @Override
    public boolean setGeoCenter(Point geoCenter, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        this.geoCenter = geoCenter;
        return true;
    }

    @Override
    public Dimension getViewPortSize() {
        return viewPortSize;
    }

    @Override
    public boolean setViewPortSize(Dimension viewPortSize, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        this.viewPortSize = viewPortSize;
        return true;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public boolean setScale(double scale, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        this.scale = scale;
        return true;
    }

    @Override
    public MapView getView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    @Override
    public boolean scaleToFitRectangle(Rectangle rectangeToFit, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        throw new UnsupportedOperationException("This operation is not supported in the dummy generator");
    }

    @Override
    public boolean setScaleAndCenter(double newScale, Point newGeoCenter, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        setScale(newScale, progressTracker, imageObserver);
        setGeoCenter(newGeoCenter, progressTracker, imageObserver);
        return true;
    }

    @Override
    public void addMapViewChangedListener(MapViewChangedTranslationListener l) {}

    @Override
    public void removeMapViewChangedListener(MapViewChangedTranslationListener l) {}
}
