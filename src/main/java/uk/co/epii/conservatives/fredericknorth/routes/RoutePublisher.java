package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.conservatives.fredericknorth.pdf.RenderedRoute;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.util.Collection;

/**
 * User: James Robinson
 * Date: 28/09/2014
 * Time: 20:29
 */
public interface RoutePublisher {

  public void publish(DistributionModel distributionModel, Collection<RenderedRoute> renderedRoutes);
  public void setProgressTracker(ProgressTracker progressTracker);

}
