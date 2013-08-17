package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;

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
    public void loadUniverse(Rectangle rectangle) {
        loadUniverse(rectangle, null);
    }

    @Override
    public void loadUniverse(Rectangle rectangle, ProgressTracker progressTracker) {
        throw new UnsupportedOperationException("This operation is not supported in the dummy generator");
    }

    @Override
    public Point getGeoCenter() {
        return geoCenter;
    }

    @Override
    public boolean setGeoCenter(Point geoCenter) {
        this.geoCenter = geoCenter;
        return true;
    }

    @Override
    public Dimension getViewPortSize() {
        return viewPortSize;
    }

    @Override
    public boolean setViewPortSize(Dimension viewPortSize) {
        this.viewPortSize = viewPortSize;
        return true;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public boolean setScale(double scale) {
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
    public boolean scaleToFitRectangle(Rectangle rectangeToFit) {
        throw new UnsupportedOperationException("This operation is not supported in the dummy generator");
    }

    @Override
    public Rectangle getUniverse() {
        throw new UnsupportedOperationException("This operation is not supported in the dummy generator");
    }
}
