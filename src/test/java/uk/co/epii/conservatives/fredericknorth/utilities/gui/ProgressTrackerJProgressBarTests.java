package uk.co.epii.conservatives.fredericknorth.utilities.gui;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

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
  public void incrementsWithChildrenOfSize1() {
    ProgressTrackerJProgressBar progressBar = new ProgressTrackerJProgressBar(10);
    progressBar.startSubsection(1);
    progressBar.increment();
    assertEquals(10, progressBar.getMaximum());
    assertEquals(1, progressBar.getValue());
  }

  @Test
  public void incrementsWithChildrenOfVaryingSizes() {
    int[][] values = new int[][] {new int[] {2,3,0,1,6},new int[] {0,9,4},new int[] {7,1,0,3}, new int[] {},new int[] {0}};
    ProgressTrackerJProgressBar progressBar = new ProgressTrackerJProgressBar(values.length);
    for (int[] value : values) {
      progressBar.startSubsection(value.length + 1);
      for (int v : value) {
        progressBar.startSubsection(v + 1);
        for (int j = 0; j < v; j++) {
          progressBar.increment();
        }
        progressBar.increment();
      }
      progressBar.increment();
    }
    assertEquals(1, progressBar.getMaximum());
    assertEquals(0, progressBar.getValue());
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
