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
public class ClusterRouter implements Router {

  private List<Route> routes;
  private List<String> routesNames;
  private DblClusters<List<IndivisbleChunk>> clusters;
  private RoutableArea routing;

  @Override
  public List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize) {
    int routesCount = calculateRoutesCount(chunks, targetSize);
    this.routing = routing;
    routes = new ArrayList<Route>(routesCount);
    routesNames = new ArrayList<String>(routesCount);
    clusters = new DblClusters<List<IndivisbleChunk>>(2, routesCount);
    clusters.setKeyer(new DblListKeyer<IndivisbleChunk>());
    fill(chunks);
    for (DblResult<List<IndivisbleChunk>> proposedRoute : clusters.results()) {
      createRoute(proposedRoute);
    }
    removeCommonEndings();
    return routes;
  }

  private void createRoute(DblResult<List<IndivisbleChunk>> proposedRoute) {
    DwellingGroup largest = null;
    for (IndivisbleChunk indivisbleChunk : proposedRoute.getKey()) {
      for (DwellingGroup dwellingGroup : indivisbleChunk.getDwellingGroups()) {
        if (largest == null || largest.size() < dwellingGroup.size()) {
          largest = dwellingGroup;
        } else if (largest.size() == dwellingGroup.size()) {
          if (largest.compareTo(dwellingGroup) < 0) {
            largest = dwellingGroup;
          }
        }
      }
    }
    String proposedName = largest.getCommonName();
    routesNames.add(proposedName);
    int attempt = 1;
    while (alreadyExists(proposedName, attempt)) {
      attempt++;
    }
    String routeName = attempt == 1 ? largest.getCommonName() : largest.getCommonName() + " " + attempt;
    Route route = new RouteImpl(routing, routeName);
    for (IndivisbleChunk indivisbleChunk : proposedRoute.getKey()) {
      route.addDwellingGroups(indivisbleChunk.getDwellingGroups());
    }
    routes.add(route);
  }

  private void removeCommonEndings() {
    if (routes.isEmpty()) return;
    String commonEnding = StringExtentions.getCommonEnding(routesNames);
    int commaAt = commonEnding.indexOf(',');
    commonEnding = commaAt == -1 ? "" : commonEnding.substring(commaAt);
    if (commonEnding.length() == 0) {
      return;
    }
    for (Route route : routes) {
      String truncatedName = route.getName().substring(0, route.getName().lastIndexOf(commonEnding));
      route.setName(truncatedName);
    }
  }

  private boolean alreadyExists(String proposedName, int attempt) {
    String toTry = attempt == 1 ? proposedName : proposedName + " " + attempt;
    for (Route route : routes) {
      if (route.getName().equals(toTry)) {
        return true;
      }
    }
    return false;
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
