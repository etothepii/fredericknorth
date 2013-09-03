package uk.co.epii.conservatives.fredericknorth.maps;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 26/08/2013
 * Time: 16:49
 */
public class MapImageImplTest {

    @Test
    public void checkGeoLocationToImageLocationCalculationTest1() {
        MapImageImpl mapImage = new MapImageImpl(
                new BufferedImage(300, 400, BufferedImage.TYPE_INT_ARGB),
                new Rectangle(100, -200, 300, 400),
                OSMapType.STREET_VIEW, 0.333333333333333333333);
        Point geoLocation = new Point(400, -100);
        Point expected = new Point(100, 100);
        Point result = mapImage.getImageLocation(geoLocation);
        assertEquals(expected, result);
    }

    @Test
    public void checkGeoLocationToImageLocationCalculationTest2() {
        MapImageImpl mapImage = new MapImageImpl(
                new BufferedImage(300, 400, BufferedImage.TYPE_INT_ARGB),
                new Rectangle(100, -200, 300, 400),
                OSMapType.STREET_VIEW, 0.333333333333333333333);
        Point geoLocation = new Point(400, -100);
        Point expected = new Point(100, 100);
        Point result = mapImage.getImageLocation(geoLocation);
        assertEquals(expected, result);
    }

    @Test
    public void checkImageLocationToGeoLocationCalculationTest1() {
        MapImageImpl mapImage = new MapImageImpl(
                new BufferedImage(300, 400, BufferedImage.TYPE_INT_ARGB),
                new Rectangle(100, -200, 300, 400),
                OSMapType.STREET_VIEW, 0.333333333333333333333);
        Point expected = new Point(400, -100);
        Point imageLocation = new Point(100, 100);
        Point result = mapImage.getGeoLocation(imageLocation);
        assertEquals(expected, result);
    }

    @Test
    public void checkImageLocationToGeoLocationCalculationTest2() {
        MapImageImpl mapImage = new MapImageImpl(
                new BufferedImage(300, 400, BufferedImage.TYPE_INT_ARGB),
                new Rectangle(100, -200, 300, 400),
                OSMapType.STREET_VIEW, 0.333333333333333333333);
        Point expected = new Point(400, -100);
        Point imageLocation = new Point(100, 100);
        Point result = mapImage.getGeoLocation(imageLocation);
        assertEquals(expected, result);
    }
}
