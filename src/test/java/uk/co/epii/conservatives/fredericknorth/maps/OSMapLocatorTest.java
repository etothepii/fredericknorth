package uk.co.epii.conservatives.fredericknorth.maps;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.OSMap;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLocator;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLocatorRegistrar;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 18:38
 */
public class OSMapLocatorTest {

    private static Point E140DG = new Point(537486, 180936);
    private static TestApplicationContext applicationContext;
    static {
        applicationContext = new TestApplicationContext();
        OSMapLocatorRegistrar.registerToContext(applicationContext);
    }
    OSMapLocator osMapLocator = applicationContext.getDefaultInstance(OSMapLocator.class);


    @Test
    public void checkCanFindE140DGMap() {
        OSMap result = osMapLocator.getMap(OSMapType.STREET_VIEW, E140DG);
        assertEquals("tq38se41", result.getMapName());
    }

    @Test
    public void checkCanGetBottomLeftTest1() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.STREET_VIEW, "tq", 38, "se", null, 43));
        Point expected = new Point(537000, 181500);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest2() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.STREET_VIEW, "tq", 38, "ne", null, 62));
        Point expected = new Point(538000, 186000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest3() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.STREET_VIEW, "tq", 38, "sw", null, 21));
        Point expected = new Point(531000, 180500);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest4() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.STREET_VIEW, "tq", 38, "nw", null, 0));
        Point expected = new Point(530000, 185000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest5() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.VECTOR_MAP, "tq", 38, null, 7, null));
        Point expected = new Point(530000, 187000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest6() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.RASTER, "tq", 38, null, null, null));
        Point expected = new Point(530000, 180000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest7() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(new OSMapImpl(OSMapType.MINI, "tq", null, null, null, null));
        Point expected = new Point(500000, 100000);
        assertEquals(expected, result);
    }

    @Test
    public void getMapsTest1() {
        Set<OSMap> osMaps = osMapLocator.getMaps(OSMapType.RASTER, new Rectangle(532486, 175936, 5000, 5000));
        Set<String> osMapNames = new HashSet<String>();
        osMapNames.add("tq37");
        osMapNames.add("tq38");
        for (OSMap map : osMaps) {
            assertTrue(map.getMapName(), osMapNames.contains(map.getMapName()));
        }
        assertEquals(osMapNames.size(), osMaps.size());
    }

    @Test
    public void getMapsTest2() {
        Set<OSMap> osMaps = osMapLocator.getMaps(OSMapType.STREET_VIEW, new Rectangle(532486, 175936, 0, 0));
        Set<String> osMapNames = new HashSet<String>();
        osMapNames.add("tq37nw41");
        for (OSMap map : osMaps) {
            assertTrue(map.getMapName(), osMapNames.contains(map.getMapName()));
        }
        assertEquals(osMapNames.size(), osMaps.size());
    }

    @Test
    public void getMapsTest3() {
        Set<OSMap> osMaps = osMapLocator.getMaps(OSMapType.VECTOR_MAP, new Rectangle(532486, 175936, 5000, 0));
        Set<String> osMapNames = new HashSet<String>();
        osMapNames.add("tq3725");
        osMapNames.add("tq3735");
        osMapNames.add("tq3745");
        osMapNames.add("tq3755");
        osMapNames.add("tq3765");
        osMapNames.add("tq3775");
        for (OSMap map : osMaps) {
            assertTrue(map.getMapName(), osMapNames.contains(map.getMapName()));
        }
        assertEquals(osMapNames.size(), osMaps.size());
    }

}
