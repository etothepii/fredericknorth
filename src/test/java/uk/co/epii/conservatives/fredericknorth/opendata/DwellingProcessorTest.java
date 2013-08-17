package uk.co.epii.conservatives.fredericknorth.opendata;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * User: James Robinson
 * Date: 22/06/2013
 * Time: 17:26
 */
public class DwellingProcessorTest {

    private final static Logger LOG = Logger.getLogger(DwellingProcessorTest.class);

    private static DwellingProcessor dwellingProcessor;

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestApplicationContext applicationContext = new TestApplicationContext();
        PostcodeDatumFactoryRegistrar.registerToContext(applicationContext);
        PostcodeProcessorRegistrar.registerToContext(
                applicationContext, PostcodeProcessorRegistrar.class.getResourceAsStream("/smallPostcodeSet.txt"), 0);
        DwellingProcessorRegistrar.registerToContext(
                applicationContext, new NullProgressTracker(), DwellingProcessorTest.class.getResourceAsStream("/smallDwellingSet.txt"));
        dwellingProcessor = applicationContext.getDefaultInstance(DwellingProcessor.class);
    }

    @Test
    public void getPostcodeTest()  {
        Collection<String> results = dwellingProcessor.getDwellingGroups();
        Collection<String> expectedPostcodes = new ArrayList<String>();
        expectedPostcodes.add("E14 0DG");
        expectedPostcodes.add("E1 3BE");
        for (String result : results) {
            assertTrue(result + " in expectedPostcodes", expectedPostcodes.contains(result));
        }
        assertEquals(expectedPostcodes.size(), results.size());

    }

    @Test
    public void getDwellingGroupsTest() {
        Collection<? extends DwellingGroup> result = dwellingProcessor.getDwellingGroups("E1 3BE");
        assertEquals(result.size(), 7);
    }

    @Test
    public void getDwellingGroupTest() {
        DwellingGroup result = dwellingProcessor.getDwellingGroup("E1 3BE", "DRAKE HOUSE 118, STEPNEY WAY, LONDON");
        assertEquals(result.size(), 3);
        assertEquals(result.getName(), "DRAKE HOUSE 118, STEPNEY WAY, LONDON");
        assertEquals(result.getPostcode().getPostcode(), "E1 3BE");
    }

    @Test
    public void getMax() {
        String maxPostcode = null;
        int max = 0;
        for (String postcode : dwellingProcessor.getDwellingGroups()) {
            int dwellingCount = dwellingProcessor.getDwellingGroups(postcode).size();
            if (dwellingCount > max) {
                max = dwellingCount;
                maxPostcode = postcode;
            }
        }
        LOG.info(maxPostcode);
    }

}
