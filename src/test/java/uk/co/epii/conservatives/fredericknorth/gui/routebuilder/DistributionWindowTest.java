package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;

import static junit.framework.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 26/09/2014
 * Time: 14:22
 */
public class DistributionWindowTest {

  @Test
  public void displaysBehavesCorrectlyOnNoTest() {
    DistributionModel distributionModel = new DistributionModel();
    final DistributionWindow distributionWindow = new DistributionWindow(null, distributionModel);
    distributionWindow.pack();
    distributionWindow.setLocationRelativeTo(null);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        distributionWindow.no();
      }
    });
    distributionWindow.setVisible(true);
    assertEquals(null, distributionWindow.getDistributionModel());
  }

  @Test
  public void displaysBehavesCorrectlyOnYesTest() {
    DistributionModel distributionModel = new DistributionModel();
    final DistributionWindow distributionWindow = new DistributionWindow(null, distributionModel);
    distributionWindow.pack();
    distributionWindow.setLocationRelativeTo(null);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        distributionWindow.yes();
      }
    });
    distributionWindow.setVisible(true);
    assertEquals(distributionModel, distributionWindow.getDistributionModel());
  }

}
