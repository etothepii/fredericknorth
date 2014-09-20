package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: James Robinson
 * Date: 20/09/2014
 * Time: 22:01
 */

class Partition {

  private final Set<String> postcodes;
  private final Set<Point> points;

  private Partition() {
    postcodes = new HashSet<String>();
    points = new HashSet<Point>();
  }

  public Partition(DwellingGroup dwellingGroup) {
    this();
    postcodes.addAll(dwellingGroup.getPostcodes());
    for (Location dwelling : dwellingGroup.getDwellings()) {
      points.add(dwelling.getPoint());
    }
  }

  public boolean contains(DwellingGroup dwellingGroup) {
    for (String postcode : dwellingGroup.getPostcodes()) {
      if (postcodes.contains(postcode)) {
        return true;
      }
    }
    for (Location dwelling : dwellingGroup.getDwellings()) {
      if (points.contains(dwelling.getPoint())) {
        return true;
      }
    }
    return false;
  }

  public static Partition join(Iterable<Partition> toJoin) {
    Partition joined = new Partition();
    for (Partition partition : toJoin) {
      joined.postcodes.addAll(partition.postcodes);
      joined.points.addAll(partition.points);
    }
    return joined;
  }
}
