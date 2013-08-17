package uk.co.epii.conservatives.fredericknorth.opendata;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.OSMap;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLocatorRegistrar;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 19:23
 */
public class PostcodeProcessorTest {

    private static PostcodeProcessor postcodeProcessor;
    private static DwellingProcessor dwellingProcessor;

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestApplicationContext applicationContext = new TestApplicationContext();
        PostcodeDatumFactoryRegistrar.registerToContext(applicationContext);
        OSMapLocatorRegistrar.registerToContext(applicationContext);
        PostcodeProcessorRegistrar.registerToContext(
                applicationContext, PostcodeProcessorRegistrar.class.getResourceAsStream("/smallPostcodeSet.txt"), 0);
        DwellingProcessorRegistrar.registerToContext(
                applicationContext, new NullProgressTracker(), DwellingProcessorTest.class.getResourceAsStream("/smallDwellingSet.txt"));
        dwellingProcessor = applicationContext.getDefaultInstance(DwellingProcessor.class);
        postcodeProcessor = applicationContext.getDefaultInstance(PostcodeProcessor.class);
    }

    @Test
    public void getDwellingCountTest() {
        int result = postcodeProcessor.getDwellingCount("E14 0DG");
        int expected = 55;
        assertEquals(expected, result);
    }

    @Test
    public void getCouncilBandCountTest() {
        int[] result = postcodeProcessor.getCouncilBandCount("E14 0DG");
        int[] expected = new int[] {7, 16, 32, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected, result);
    }

    @Test
    public void getLocationTest() {
        Point result = postcodeProcessor.getLocation("E14 0DG");
        Point expected = new Point(537486, 180936);
        assertEquals(expected, result);
    }

    @Test
    public void getAdminWardTest() {
        String result = postcodeProcessor.getAdminWardId("E14 0DG");
        String expected = "E05000580";
        assertEquals(expected, result);
    }

    @Test
    public void getContaingMapTest() {
        OSMap result = postcodeProcessor.getContainingMap("E14 0DG");
        String expected = "tq38se";
        assertEquals(expected, result.getMapName());
    }

    @Test
    public void getContainingMapsTest() {
        Set<OSMap> results = postcodeProcessor.getContainingMaps(dwellingProcessor.getDwellingGroups());
        Set<String> expected = new HashSet<String>();
        expected.add("tq38se");
        for (OSMap result : results) {
            assertTrue(result.getMapName(), expected.contains(result.getMapName()));
        }
        assertEquals(expected.size(), results.size());
    }

    @Test
    public void getWardsTest() {
        Set<String> result = postcodeProcessor.getWards();
        Set<String> expected = new HashSet<String>();
        expected.add("E05000589");
        expected.add("E05000580");
        for (String s : result) {
            System.out.println(s);
        }
        for (String expectedValue : expected) {
            assertTrue(expectedValue + " not in result", result.contains(expectedValue));
        }
        for (String resultValue : result) {
            assertTrue(resultValue + " not in expected", expected.contains(resultValue));
        }
    }

    @Test
    public void getLimehouseWardTest() {
        Set<String> result = postcodeProcessor.getWard("E05000580");
        Set<String> expected = new HashSet<String>();
        expected.add("E14 0DG");
        for (String s : result) {
            System.out.println(s);
        }
        for (String expectedValue : expected) {
            assertTrue(expectedValue + " not in result", result.contains(expectedValue));
        }
        for (String resultValue : result) {
            assertTrue(resultValue + " not in expected", expected.contains(resultValue));
        }
    }
}
