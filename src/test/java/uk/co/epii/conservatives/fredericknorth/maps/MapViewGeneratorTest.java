package uk.co.epii.conservatives.fredericknorth.maps;


import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 11:08
 */
public class MapViewGeneratorTest {

    @Test
    public void scaleToFitRectangleTest() {
        ApplicationContext applicationContext = new TestApplicationContext();
        OSMapLocatorRegistrar.registerToContext(applicationContext);
        MapImage mapImage = new MapImageImpl(
                new BufferedImage(100, 150, BufferedImage.TYPE_INT_ARGB),
                new Rectangle(20, 180, 100, 150), OSMapType.STREET_VIEW, 1d);
        EnumMap<OSMapType, MapImage> mapCache = new EnumMap<OSMapType, MapImage>(OSMapType.class);
        mapCache.put(OSMapType.MINI, mapImage);
        MapViewGenerator mapViewGenerator = new MapViewGeneratorImpl(applicationContext, mapCache, null, null);
        mapViewGenerator.setViewPortSize(new Dimension(20, 30), NullProgressTracker.NULL, null);
        mapViewGenerator.scaleToFitRectangle(new Rectangle(40, -100, 30, 20), NullProgressTracker.NULL, null);
        assertEquals("Scale", mapViewGenerator.getScale(), 0.6666666666666, 0.000001);
        assertEquals("Center", mapViewGenerator.getGeoCenter(), new Point(55, -90));
    }

}
