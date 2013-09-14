package uk.co.epii.conservatives.fredericknorth.maps;

import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 06/07/2013
 * Time: 19:38
 */
public class MapViewTest {

    @Test
    public void getZoomNewGeoCenterTest1() {
        MapView mapView = new MapViewImpl(
                new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB),
                new Point(50, 25), new Dimension(10, 20), 0.5, null, null);
        Point result = mapView.getNewGeoCenter(new Point(6, 7), 0.25);
        Point expected = new Point(48, 19);
        assertEquals(expected, result);
    }

    @Test
    public void getZoomNewGeoCenterTest2() {
        MapView mapView = new MapViewImpl(
                new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB),
                new Point(50, 25), new Dimension(10, 20), 0.5, null, null);
        Point result = mapView.getNewGeoCenter(new Point(6, 7), 2);
        Point expected = new Point(52, 30);
        assertEquals(expected, result);
    }

    @Test
    public void getDragNewGeoCenterTest() {
        MapView mapView = new MapViewImpl(
                new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB),
                new Point(50, 25), new Dimension(10, 20), 0.5, null, null);
        Point result = mapView.getNewGeoCenter(new Point(52, 31), new Point(4, 3));
        Point expected = new Point(54, 17);
        assertEquals(expected, result);
    }

    @Test
    public void checkGeoLocationToImageLocationCalculationTest() {
        MapView mapView = new MapViewImpl(null, new Point(550, -400), new Dimension(300, 400), 0.333333333333333333333, null, null);
        Point geoLocation = new Point(400, -100);
        Point expected = new Point(100, 100);
        Point result = mapView.getImageLocation(geoLocation);
        assertEquals(expected, result);
    }

    @Test
    public void checkGeoLocationToImageLocationCalculationWithAffineTest() {
        MapView mapView = new MapViewImpl(null, new Point(550, -400), new Dimension(300, 400), 0.333333333333333333333, null, null);
        Point2D.Float geoLocation = new Point2D.Float(400, -100);
        Point2D.Float expected = new Point2D.Float(100, 100);
        Point2D.Float result = new Point2D.Float();
        mapView.getGeoToImageTransform().transform(geoLocation, result);
        assertEquals(expected, result);
    }

    @Test
    public void checkImageLocationToGeoLocationCalculationTest() {
        MapView mapView = new MapViewImpl(null, new Point(550, -400), new Dimension(300, 400), 0.333333333333333333333, null, null);
        Point imageLocation = new Point(200, 100);
        Point expected = new Point(700, -100);
        Point result = mapView.getGeoLocation(imageLocation);
        assertEquals(expected, result);
    }

}
