package uk.co.epii.conservatives.fredericknorth.pdf;

import uk.co.epii.conservatives.fredericknorth.routes.Route;

import java.util.UUID;

/**
 * User: James Robinson
 * Date: 14/09/2014
 * Time: 21:59
 */
class RenderedRouteImpl implements RenderedRoute {

  private Route route;
  private UUID uuid;
  private String uniqueUrl;

  RenderedRouteImpl(Route route, UUID uuid, String uniqueUrl) {
    this.route = route;
    this.uuid = uuid;
    this.uniqueUrl = uniqueUrl;
  }

  @Override
  public String getUniqueUrl() {
    return uniqueUrl;
  }

  @Override
  public UUID getUUID() {
    return uuid;
  }

  @Override
  public Route getRoute() {
    return route;
  }
}
