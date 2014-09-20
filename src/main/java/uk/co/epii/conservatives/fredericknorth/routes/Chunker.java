package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 20/09/2014
 * Time: 15:31
 */
class Chunker {

  private List<Partition> partitions = new ArrayList<Partition>();
  private Set<DwellingGroup>[] dwellingGroups;

  public Collection<IndivisbleChunk> chunk(Collection<? extends DwellingGroup> dwellingGroups) {
    partition(dwellingGroups);
    this.dwellingGroups = createArray(partitions.size());
    outer: for (DwellingGroup dwellingGroup : dwellingGroups) {
      for (int i = 0; i < partitions.size(); i++) {
        if (partitions.get(i).contains(dwellingGroup)) {
          this.dwellingGroups[i].add(dwellingGroup);
          continue outer;
        }
      }
    }
    List<IndivisbleChunk> indivisbleChunks = new ArrayList<IndivisbleChunk>();
    for (int i = 0; i < partitions.size(); i++) {
      IndivisbleChunk indivisbleChunk = new IndivisbleChunk();
      for (DwellingGroup dwellingGroup : this.dwellingGroups[i]) {
        indivisbleChunk.add(dwellingGroup);
      }
      indivisbleChunks.add(indivisbleChunk);
    }
    return indivisbleChunks;
  }

  private Set<DwellingGroup>[] createArray(int size) {
    Set<DwellingGroup>[] dwellingGroups = new Set[size];
    for (int i = 0; i < size; i++) {
      dwellingGroups[i] = new HashSet<DwellingGroup>();
    }
    return dwellingGroups;
  }

  private void partition(Collection<? extends DwellingGroup> dwellingGroups) {
    for (DwellingGroup dwellingGroup : dwellingGroups) {
      List<Integer> foundIndecies = getPartitionsFoundInIndecies(dwellingGroup);
      List<Partition> matchedPartitions = new ArrayList<Partition>(foundIndecies.size());
      for (int foundIndex : foundIndecies) {
        matchedPartitions.add(partitions.remove(foundIndex));
      }
      if (matchedPartitions.size() == 0) {
        partitions.add(new Partition(dwellingGroup));
      }
      else if (matchedPartitions.size() == 1) {
        partitions.add(matchedPartitions.get(0));
      }
      else {
        partitions.add(Partition.join(matchedPartitions));
      }
    }
  }

  private List<Integer> getPartitionsFoundInIndecies(DwellingGroup dwellingGroup) {
    List<Integer> partitionIndecies = new ArrayList<Integer>(partitions.size());
    for (int i = partitions.size() - 1; i >= 0; i--) {
      if (partitions.get(i).contains(dwellingGroup)) {
        partitionIndecies.add(i);
      }
    }
    return partitionIndecies;
  }
}
