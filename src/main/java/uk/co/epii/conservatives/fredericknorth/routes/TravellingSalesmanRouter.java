package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.spencerperceval.tuple.Duple;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 17/09/2014
 * Time: 20:53
 */
public class TravellingSalesmanRouter extends AbstractRouter {
  
  Map<Point, Duple<List<IndivisbleChunk>, Double>> colocatedChunks;
  
  @Override
  public List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize) {
    reset(routing);
    groupByLocation(chunks);
    removeOverLarge(targetSize);
    solveTravellingSalesmanProblem(targetSize);
    return routes;
  }

  private void solveTravellingSalesmanProblem(int targetSize) {
  }

  private void removeOverLarge(int targetSize) {
    for (Point point : new ArrayList<Point>(colocatedChunks.keySet())) {
      Duple<List<IndivisbleChunk>, Double> duple = colocatedChunks.get(point);
      if (duple.getSecond() > targetSize) {
        colocatedChunks.remove(point);
        createRoute(duple.getFirst());
      }
    }
  }

  @Override
  protected void reset(RoutableArea routing) {
    super.reset(routing);
    colocatedChunks = new HashMap<Point, Duple<List<IndivisbleChunk>, Double>>();
  }

  private void groupByLocation(Collection<IndivisbleChunk> chunks) {
    for (IndivisbleChunk chunk : chunks) {
      Duple<List<IndivisbleChunk>, Double> duple = colocatedChunks.get(chunk.getMedian());
      if (duple == null) {
        duple = new Duple<List<IndivisbleChunk>, Double>(new ArrayList<IndivisbleChunk>(), 0d);
        colocatedChunks.put(chunk.getMedian(), duple);
      }
      duple.getFirst().add(chunk);
      duple.setSecond(duple.getSecond() + chunk.size());
    }
  }
}
