package uk.co.epii.conservatives.fredericknorth.pdf;

import uk.co.epii.conservatives.fredericknorth.routes.Route;

import java.util.UUID;

/**
 * User: James Robinson
 * Date: 14/09/2014
 * Time: 21:39
 */
public interface RenderedRoute {
  public String getUniqueUrl();
  public UUID getUUID();
  public Route getRoute();
}
