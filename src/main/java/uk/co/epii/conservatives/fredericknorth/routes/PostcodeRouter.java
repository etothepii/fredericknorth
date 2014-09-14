package uk.co.epii.conservatives.fredericknorth.routes;

import com.tomgibara.cluster.gvm.dbl.DblResult;

import java.util.*;

/**
 * User: James Robinson
 * Date: 17/05/2014
 * Time: 13:55
 */
public class PostcodeRouter extends AbstractRouter {

  private List<List<IndivisbleChunk>> grouped;
  private List<IndivisbleChunk> activeGroup;
  private String lastPostcode = null;
  private int activeGroupSize = 0;
  private int targetSize;

  @Override
  public List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize) {
    reset(routing);
    this.targetSize = targetSize;
    List<IndivisbleChunk> sorted = new ArrayList<IndivisbleChunk>(chunks);
    Collections.sort(sorted, new Comparator<IndivisbleChunk>() {
      @Override
      public int compare(IndivisbleChunk o1, IndivisbleChunk o2) {
        return o1.getPostcode().compareTo(o2.getPostcode());
      }
    });
    group(sorted);

    for (List<IndivisbleChunk> proposedRoute : grouped) {
      createRoute(proposedRoute);
    }
    removeCommonEndings();
    return routes;
  }

  private void group(List<IndivisbleChunk> sorted) {
    grouped = new ArrayList<List<IndivisbleChunk>>();
    resetActive();
    for (IndivisbleChunk chunk : sorted) {
      if (!shouldAdd(chunk)) {
        addActiveGroup();
      }
      addToActive(chunk);
    }
    addActiveGroup();
    subgroup(grouped);
  }

  private void subgroup(List<List<IndivisbleChunk>> grouped) {
    this.grouped = new ArrayList<List<IndivisbleChunk>>();
    for (List<IndivisbleChunk> hardGroup : grouped) {

    }
  }

  private void resetActive() {
    activeGroup = new ArrayList<IndivisbleChunk>();
    activeGroupSize = 0;
  }

  private void addActiveGroup() {
    if (activeGroupSize == 0) return;
    grouped.add(activeGroup);
    resetActive();
  }

  private boolean shouldAdd(IndivisbleChunk chunk) {
    try {
      if (lastPostcode == null) {
        return true;
      }
      if (lastPostcode.length() != chunk.getPostcode().length()) {
        return false;
      }
      return chunk.getPostcode().startsWith(lastPostcode.substring(0, lastPostcode.length() - 1));
    }
    finally {
      lastPostcode = chunk.getPostcode();
    }
  }


  private void addToActive(IndivisbleChunk chunk) {
    activeGroupSize += chunk.size();
    activeGroup.add(chunk);
  }

}
