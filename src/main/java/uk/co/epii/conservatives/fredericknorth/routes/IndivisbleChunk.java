package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.Dwelling;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndivisbleChunk {

  private final List<DwellingGroup> dwellingGroups;
  private int count;
  private List<Point> points;

  public IndivisbleChunk() {
    this.dwellingGroups = new ArrayList<DwellingGroup>();
    points = new ArrayList<Point>();
  }

  public void add(DwellingGroup dwellingGroup) {
    dwellingGroups.add(dwellingGroup);
    count += dwellingGroup.size();
    for (Dwelling dwelling : dwellingGroup.getDwellings()) {
      Point p = dwelling.getPoint();
      if (p == null) {
        p = dwellingGroup.getPoint();
      }
      points.add(p);
    }
  }

  public Point getMedian() {
    return PointExtensions.getMedian(points);
  }

  public double size() {
    return count;
  }

  public Collection<DwellingGroup> getDwellingGroups() {
    return dwellingGroups;
  }
}