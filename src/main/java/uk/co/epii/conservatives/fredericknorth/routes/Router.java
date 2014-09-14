package uk.co.epii.conservatives.fredericknorth.routes;

import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 17/05/2014
 * Time: 10:21
 */
public interface Router {
  List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize);
}
