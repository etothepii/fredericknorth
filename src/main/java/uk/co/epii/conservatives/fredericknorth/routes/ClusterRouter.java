package uk.co.epii.conservatives.fredericknorth.routes;

import com.tomgibara.cluster.gvm.dbl.DblClusters;
import com.tomgibara.cluster.gvm.dbl.DblListKeyer;
import com.tomgibara.cluster.gvm.dbl.DblResult;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.utilities.StringExtentions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 17/05/2014
 * Time: 10:26
 */
public class ClusterRouter extends AbstractRouter {

  private DblClusters<List<IndivisbleChunk>> clusters;

  @Override
  public List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize) {
    int routesCount = calculateRoutesCount(chunks, targetSize);
    reset(routing);
    clusters = new DblClusters<List<IndivisbleChunk>>(2, routesCount);
    clusters.setKeyer(new DblListKeyer<IndivisbleChunk>());
    fill(chunks);
    for (DblResult<List<IndivisbleChunk>> proposedRoute : clusters.results()) {
      createRoute(proposedRoute.getKey());
    }
    removeCommonEndings();
    return routes;
  }

  private void fill(Collection<IndivisbleChunk> chunks) {
    for (IndivisbleChunk indivisbleChunk : chunks) {
      Point geoLocation = indivisbleChunk.getMedian();
      double[] doubleGeoLocation = new double[]{geoLocation.getX(), geoLocation.getY()};
      double weight = indivisbleChunk.size();
      ArrayList<IndivisbleChunk> indivisbleChunks = new ArrayList<IndivisbleChunk>();
      indivisbleChunks.add(indivisbleChunk);
      clusters.add(weight, doubleGeoLocation, indivisbleChunks);
    }
  }

  private int calculateRoutesCount(Collection<IndivisbleChunk> chunks, int targetSize) {
    int singleRoutes = 0;
    double count = 0;
    for (IndivisbleChunk chunk : chunks) {
      if (chunk.size() > targetSize) {
        singleRoutes++;
      }
      else {
        count += chunk.size();
      }
    }
    return singleRoutes + (int)Math.ceil(count / targetSize);
  }
}
