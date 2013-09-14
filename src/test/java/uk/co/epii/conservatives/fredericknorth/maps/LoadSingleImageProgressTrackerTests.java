package uk.co.epii.conservatives.fredericknorth.maps;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerJProgressBar;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 01/09/2013
 * Time: 09:08
 */
public class LoadSingleImageProgressTrackerTests {

    @Test
    public void loadImageProgressTest() {
        ProgressTracker progressTracker = new ProgressTrackerJProgressBar(1000);
        LoadSingleImageProgressTracker imageProgressTracker = new LoadSingleImageProgressTracker(progressTracker, 1000);
        imageProgressTracker.imageStarted(null, 0);
        for (int i = 0; i < 85; i++) {
            imageProgressTracker.imageProgress(null, i * 100f / 85);
            assertTrue(Math.abs((int) (1000f / 85 * i) - progressTracker.getValue()) <= 1);
        }
        imageProgressTracker.imageComplete(null);
        assertEquals(0, progressTracker.getValue());
        assertEquals(1, progressTracker.getMaximum());
    }

}
