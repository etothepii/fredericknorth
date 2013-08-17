package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactory;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.*;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 13:04
 */
public class DottedDwellingGroupOverlayItemImplTest {

    @Test
    public void compareTwoBuildingsOnChapelHouseTest() {
        TestApplicationContext testApplicationContext = new TestApplicationContext();
        PostcodeDatumFactoryRegistrar.registerToContext(testApplicationContext);
        PostcodeProcessorRegistrar.registerToContext(testApplicationContext,
                DottedDwellingGroupOverlayItemImplTest.class.getResourceAsStream("/chapelHouseStreetPostcodes.txt"), 0);
        DwellingProcessorRegistrar.registerToContext(testApplicationContext, new NullProgressTracker(),
                DottedDwellingGroupOverlayItemImplTest.class.getResourceAsStream("/chapelHouseStreetDwellings.txt"));
        DotFactoryRegistrar.registerToContext(testApplicationContext);
        DotFactory dotFactory = testApplicationContext.getDefaultInstance(DotFactory.class);
        DwellingProcessor dwellingProcessor = testApplicationContext.getDefaultInstance(DwellingProcessor.class);
        List<DwellingGroup> dwellingGroupList = new ArrayList<DwellingGroup>(dwellingProcessor.getDwellingGroups("E14 3AS"));
        DottedDwellingGroupOverlayItemImpl[] impls = new DottedDwellingGroupOverlayItemImpl[2];
        for (int i = 0; i < 2; i++) {
            DwellingGroup dwellingGroup = dwellingGroupList.get(i);
            impls[i] = new DottedDwellingGroupOverlayItemImpl(
                    new DottedDwellingGroup(
                            dwellingGroup, dotFactory.getStandardDot(Color.BLUE)), 0);
        }
        assertTrue(!impls[0].equals(impls[1]));
        assertTrue(impls[0].compareTo(impls[1]) != 0);
    }

}
