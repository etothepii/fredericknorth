package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:02
 */
public interface MapViewGenerator {

    public Point getGeoCenter();
    public boolean setGeoCenter(Point geoCenter,
                                ProgressTracker progressTracker, MapImageObserver imageObserver);
    public Dimension getViewPortSize();
    public boolean setViewPortSize(Dimension viewPortSize,
                                   ProgressTracker progressTracker, MapImageObserver imageObserver);
    public double getScale();
    public boolean setScale(double scale,
                            ProgressTracker progressTracker, MapImageObserver imageObserver);
    public MapView getView();
    public boolean scaleToFitRectangle(Rectangle rectangeToFit,
                                       ProgressTracker progressTracker, MapImageObserver imageObserver);
    public void updateImage(MapImageObserver mapImageObserver);
    public boolean setScaleAndCenter(double newScale, Point newGeoCenter,
                                     ProgressTracker progressTracker, MapImageObserver imageObserver);

    public void addMapViewChangedListener(MapViewChangedTranslationListener l);
    public void removeMapViewChangedListener(MapViewChangedTranslationListener l);
}
