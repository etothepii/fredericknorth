package uk.co.epii.conservatives.fredericknorth.opendata;

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
        OSMap result = osMapLocator.getMap(E140DG);
        assertEquals("tq38se", result.getMapName());
    }

    @Test
    public void checkCanGetBottomLeftTest1() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(osMapLocator.create("tq", 38, "se"));
        Point expected = new Point(535000, 180000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest2() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(osMapLocator.create("tq", 38, "ne"));
        Point expected = new Point(535000, 185000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest3() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(osMapLocator.create("tq", 38, "sw"));
        Point expected = new Point(530000, 180000);
        assertEquals(expected, result);
    }

    @Test
    public void checkCanGetBottomLeftTest4() {
        Point result = osMapLocator.getBottomLeftMapCoordinate(osMapLocator.create("tq", 38, "nw"));
        Point expected = new Point(530000, 185000);
        assertEquals(expected, result);
    }

    @Test
    public void getMapsTest1() {
        List<OSMap> osMaps = osMapLocator.getMaps(new Rectangle(532486, 175936, 5000, 5000));
        Set<String> osMapNames = new HashSet<String>();
        osMapNames.add("tq37nw");
        osMapNames.add("tq37ne");
        osMapNames.add("tq38sw");
        osMapNames.add("tq38se");
        for (OSMap map : osMaps) {
            assertTrue(map.getMapName(), osMapNames.contains(map.getMapName()));
        }
    }

    @Test
    public void getMapsTest2() {
        List<OSMap> osMaps = osMapLocator.getMaps(new Rectangle(532486, 175936, 0, 0));
        Set<String> osMapNames = new HashSet<String>();
        osMapNames.add("tq37nw");
        for (OSMap map : osMaps) {
            assertTrue(map.getMapName(), osMapNames.contains(map.getMapName()));
        }
    }

    @Test
    public void getMapsTest3() {
        List<OSMap> osMaps = osMapLocator.getMaps(new Rectangle(532486, 175936, 5000, 0));
        Set<String> osMapNames = new HashSet<String>();
        osMapNames.add("tq37nw");
        osMapNames.add("tq37ne");
        for (OSMap map : osMaps) {
            assertTrue(map.getMapName(), osMapNames.contains(map.getMapName()));
        }
    }

}
