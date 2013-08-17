package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:02
 */
public interface MapViewGenerator {

    public void loadUniverse(Rectangle rectangle);
    public void loadUniverse(Rectangle rectangle, ProgressTracker progressTracker);
    public Point getGeoCenter();
    public boolean setGeoCenter(Point geoCenter);
    public Dimension getViewPortSize();
    public boolean setViewPortSize(Dimension viewPortSize);
    public double getScale();
    public boolean setScale(double scale);
    public MapView getView();
    public boolean scaleToFitRectangle(Rectangle rectangeToFit);
    public Rectangle getUniverse();
}
