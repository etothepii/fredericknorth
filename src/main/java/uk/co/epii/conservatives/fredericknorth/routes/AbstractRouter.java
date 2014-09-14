package uk.co.epii.conservatives.fredericknorth.routes;

import com.tomgibara.cluster.gvm.dbl.DblResult;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.utilities.StringExtentions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 17/05/2014
 * Time: 15:14
 */
public abstract class AbstractRouter implements Router {

  protected List<Route> routes;
  protected List<String> routesNames;
  protected RoutableArea routing;

  protected void reset(RoutableArea routableArea) {
    routes = new ArrayList<Route>();
    routesNames = new ArrayList<String>();
    this.routing = routableArea;
  }

  protected void createRoute(Collection<IndivisbleChunk> proposedRoute) {
    DwellingGroup largest = null;
    for (IndivisbleChunk indivisbleChunk : proposedRoute) {
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
    for (IndivisbleChunk indivisbleChunk : proposedRoute) {
      route.addDwellingGroups(indivisbleChunk.getDwellingGroups());
    }
    routes.add(route);
  }

  protected void removeCommonEndings() {
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
}
