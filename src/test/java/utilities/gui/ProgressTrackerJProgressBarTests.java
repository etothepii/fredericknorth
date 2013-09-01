package utilities.gui;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerJProgressBar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 01/09/2013
 * Time: 08:16
 */
public class ProgressTrackerJProgressBarTests {

    @Test
    public void canIncrementToTwenty() {
        ProgressTracker progressTracker = new ProgressTrackerJProgressBar(20);
        for (int i = 0; i < 19; i++) {
            progressTracker.increment();
            assertEquals(20, progressTracker.getMaximum());
            assertEquals(i + 1, progressTracker.getValue());
        }
        progressTracker.increment();
        assertEquals(1, progressTracker.getMaximum());
        assertEquals(0, progressTracker.getValue());
    }

    @Test
    public void canIncrementToTwentyLotsOfSeventeen() {
        ProgressTracker progressTracker = new ProgressTrackerJProgressBar(20);
        for (int i = 0; i < 19; i++) {
            progressTracker.startSubsection(17);
            for (int j = 0; j < 16; j++) {
                progressTracker.increment();
                assertEquals(340, progressTracker.getMaximum());
                assertEquals(i * 17 + j + 1, progressTracker.getValue());
            }
            progressTracker.increment();
            assertEquals(20, progressTracker.getMaximum());
            assertEquals(i + 1, progressTracker.getValue());
        }
        progressTracker.startSubsection(17);
        for (int j = 0; j < 16; j++) {
            progressTracker.increment();
            assertEquals(340, progressTracker.getMaximum());
            assertEquals(323 + j + 1, progressTracker.getValue());
        }
        progressTracker.increment();
        assertEquals(1, progressTracker.getMaximum());
        assertEquals(0, progressTracker.getValue());
    }

}
