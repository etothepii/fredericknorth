package uk.co.epii.conservatives.fredericknorth.pdf;

import uk.co.epii.conservatives.fredericknorth.routes.Route;

import java.util.UUID;

/**
 * User: James Robinson
 * Date: 14/09/2014
 * Time: 22:03
 */
public class RenderedRouteFactoryImpl implements RenderedRouteFactory {
  private String urlPrefix;

  public RenderedRouteFactoryImpl(String urlPrefix) {
    this.urlPrefix = urlPrefix;
  }

  public RenderedRoute getRenderedRoute(Route route) {
    UUID uuid = UUID.randomUUID();
    return new RenderedRouteImpl(route, uuid, urlPrefix + uuid.toString());
  }
}
