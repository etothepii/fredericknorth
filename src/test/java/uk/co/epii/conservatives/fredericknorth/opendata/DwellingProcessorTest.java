package uk.co.epii.conservatives.fredericknorth.opendata;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

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
        DwellingGroupFactoryRegistrar.registerToContext(applicationContext);
        DwellingProcessorRegistrar.registerToContext(
                applicationContext, new NullProgressTracker(), DwellingProcessorTest.class.getResourceAsStream("/smallDwellingSet.txt"));
        dwellingProcessor = applicationContext.getDefaultInstance(DwellingProcessor.class);
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
        String expectedName = "1, 3 & 5 DRAKE HOUSE 118, STEPNEY WAY, LONDON";
        assertEquals(expectedName, result.getName());
    }

}
