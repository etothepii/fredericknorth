package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Ignore;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.routes.DistributionModel;

import javax.swing.*;

import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 26/09/2014
 * Time: 14:22
 */
public class DistributionWindowTest {

  @Ignore
  @Test
  public void displaysBehavesCorrectlyOnNoTest() {
    DistributionModel distributionModel = new DistributionModelImpl();
    final DistributionWindow distributionWindow = new DistributionWindow(null, distributionModel);
    distributionWindow.pack();
    distributionWindow.setLocationRelativeTo(null);
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        while (!distributionWindow.isVisible()) {
          try {
            Thread.sleep(10L);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            distributionWindow.no();
          }
        });
      }
    });
    distributionWindow.setVisible(true);
    assertEquals(null, distributionWindow.getDistributionModel());
  }

  @Ignore
  @Test
  public void displaysBehavesCorrectlyOnYesTest() {
    DistributionModel distributionModel = new DistributionModelImpl();
    final DistributionWindow distributionWindow = new DistributionWindow(null, distributionModel);
    distributionWindow.pack();
    distributionWindow.setLocationRelativeTo(null);
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run() {
        while (!distributionWindow.isVisible()) {
          try {
            Thread.sleep(10L);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            distributionWindow.yes();
          }
        });
      }
    });
    distributionWindow.setVisible(true);
    assertEquals(distributionModel, distributionWindow.getDistributionModel());
  }

}
