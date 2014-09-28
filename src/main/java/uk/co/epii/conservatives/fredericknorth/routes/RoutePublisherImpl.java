package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.pdf.RenderedRoute;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;
import uk.co.epii.politics.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.politics.williamcavendishbentinck.tables.Leaflet;
import uk.co.epii.politics.williamcavendishbentinck.tables.LeafletMap;
import uk.co.epii.politics.williamcavendishbentinck.tables.RouteMember;

import java.util.*;

/**
 * User: James Robinson
 * Date: 28/09/2014
 * Time: 20:31
 */
public class RoutePublisherImpl implements RoutePublisher {

  private DatabaseSession databaseSession;

  public ProgressTracker getProgressTracker() {
    return progressTracker;
  }

  public void setProgressTracker(ProgressTracker progressTracker) {
    this.progressTracker = progressTracker;
  }

  private ProgressTracker progressTracker = new NullProgressTracker();

  public DatabaseSession getDatabaseSession() {
    return databaseSession;
  }

  public void setDatabaseSession(DatabaseSession databaseSession) {
    this.databaseSession = databaseSession;
  }

  @Override
  public void publish(DistributionModel distributionModel, Collection<RenderedRoute> renderedRoutes) {
    if (distributionModel == null) {
      progressTracker.increment();
      return;
    }
    Leaflet leaflet = new Leaflet(UUID.randomUUID().toString(),
            new java.sql.Date(distributionModel.getDistributionStart().getTime()),
            distributionModel.getTitle(),
            distributionModel.getDescription());
    databaseSession.upload(Arrays.asList(leaflet));
    progressTracker.startSubsection(renderedRoutes.size());
    for (RenderedRoute renderedRoute : renderedRoutes) {
      uploadRenderedRoute(databaseSession, leaflet, renderedRoute);
      progressTracker.increment();
    }
  }

  private void uploadRenderedRoute(DatabaseSession databaseSession, Leaflet leaflet, RenderedRoute renderedRoute) {
    uk.co.epii.politics.williamcavendishbentinck.tables.Route route = getRouteFromDatabase(databaseSession, renderedRoute.getRoute());
    LeafletMap leafletMap = new LeafletMap(
            renderedRoute.getUUID().toString(), leaflet.getId(), route.getId(), null, null);
    databaseSession.upload(Arrays.asList(leafletMap));
  }

  private uk.co.epii.politics.williamcavendishbentinck.tables.Route getRouteFromDatabase(DatabaseSession databaseSession, uk.co.epii.conservatives.fredericknorth.routes.Route route) {
    List<uk.co.epii.politics.williamcavendishbentinck.tables.Route> routes = databaseSession.getByUuid(uk.co.epii.politics.williamcavendishbentinck.tables.Route.class, route.getUuid());
    if (routes.size() > 0) {
      return routes.get(0);
    }
    uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea parent =
            getBoundedArea(databaseSession, route.getRoutableArea().getBoundedArea());
    uk.co.epii.politics.williamcavendishbentinck.tables.Route databaseRoute = new uk.co.epii.politics.williamcavendishbentinck.tables.Route(route.getUuid().toString(), route.getName(), parent.getId(), getOwner(), getOwnerGroup(),
            getDeliveredBy(), null);
    databaseSession.upload(Arrays.asList(databaseRoute));
    List<RouteMember> routeMembers = new ArrayList<RouteMember>(route.getDwellingCount());
    for (DwellingGroup dwellingGroup : route.getDwellingGroups()) {
      for (Location location : dwellingGroup.getDwellings()) {
        if (location instanceof DwellingDatabaseImpl) {
          DwellingDatabaseImpl dwelling = (DwellingDatabaseImpl)location;
          routeMembers.add(new RouteMember(
                  UUID.randomUUID().toString(),
                  databaseRoute.getId(),
                  dwelling.getDeliveryPointAddress().getUprn()));
        }
      }
    }
    databaseSession.upload(routeMembers);
    return databaseRoute;
  }

  private Integer getDeliveredBy() {
    return null;
  }

  private uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea getBoundedArea(
          DatabaseSession databaseSession, BoundedArea boundedArea) {
    if (boundedArea == null) {
      return null;
    }
    List<uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea> boundedAreas = databaseSession.getByUuid(
            uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea.class, boundedArea.getUuid());
    if (boundedAreas.size() > 0) {
      return boundedAreas.get(0);
    }
    uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea parent =
            getBoundedArea(databaseSession, boundedArea.getParent());
    uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea databaseBoundedArea =
            new uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea(boundedArea.getUuid().toString(),
                    parent == null ? null : parent.getId(), getOwner(), getOwnerGroup(), boundedArea.getName(), null);
    databaseSession.upload(Arrays.asList(databaseBoundedArea));
    return databaseBoundedArea;
  }

  private Integer getOwnerGroup() {
    return null;
  }

  private Integer getOwner() {
    return null;
  }

}
