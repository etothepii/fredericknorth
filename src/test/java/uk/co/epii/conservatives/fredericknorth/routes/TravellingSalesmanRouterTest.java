package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Ignore;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaConstructor;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 18/09/2014
 * Time: 02:20
 */
public class TravellingSalesmanRouterTest {

  @Test
  @Ignore
  public void simpleCreateRoutesTest() {
    TravellingSalesmanRouter router = new TravellingSalesmanRouter();
    BoundedAreaConstructor constructor = new BoundedAreaConstructor(null, BoundedAreaType.COUNTY, "A");
    constructor.setCurrent(new Point(0, 0), new BoundedArea[0]);
    constructor.addCurrent();
    constructor.setCurrent(new Point(100, 0), new BoundedArea[0]);
    constructor.addCurrent();
    constructor.setCurrent(new Point(100, 100), new BoundedArea[0]);
    constructor.addCurrent();
    constructor.setCurrent(new Point(0, 100), new BoundedArea[0]);
    constructor.addCurrent();
    DefaultRoutableArea routing = new DefaultRoutableArea(constructor.lockDown(), null);
    List<DummyDwellingGroup> groups = new ArrayList<DummyDwellingGroup>();
    groups.add(new DummyDwellingGroup("B1", 10, new Point(5,5), "A1"));
    groups.add(new DummyDwellingGroup("B2", 9, new Point(5,50), "A2"));
    groups.add(new DummyDwellingGroup("B3", 8, new Point(5,95), "A3"));
    groups.add(new DummyDwellingGroup("B4", 7, new Point(95,5), "A4"));
    groups.add(new DummyDwellingGroup("B5", 6, new Point(95,5), "A5"));
    groups.add(new DummyDwellingGroup("B6", 5, new Point(95,95), "A6"));
    for (DummyDwellingGroup group : groups) {
      group.setCommonName(group.getName());
      routing.addDwellingGroup(group, false);
    }
    Chunker chunker = new Chunker();
    Collection<IndivisbleChunk> indivisbleChunks = chunker.chunk(groups);
    List<Route> routes = router.createRoutes(routing, indivisbleChunks, 30);
    assertEquals(routes.size(), 2);
  }

  @Ignore
  @Test
  public void clusterCorrectlyTest() {
    TravellingSalesmanRouter router = new TravellingSalesmanRouter();
    BoundedAreaConstructor constructor = new BoundedAreaConstructor(null, BoundedAreaType.COUNTY, "A");
    constructor.setCurrent(new Point(0, 0), new BoundedArea[0]);
    constructor.addCurrent();
    constructor.setCurrent(new Point(100, 0), new BoundedArea[0]);
    constructor.addCurrent();
    constructor.setCurrent(new Point(100, 100), new BoundedArea[0]);
    constructor.addCurrent();
    constructor.setCurrent(new Point(0, 100), new BoundedArea[0]);
    constructor.addCurrent();
    DefaultRoutableArea routing = new DefaultRoutableArea(constructor.lockDown(), null);
    List<DummyDwellingGroup> groups = new ArrayList<DummyDwellingGroup>();
    int totalDwelings = 0;
    for (int i = 1; i <= 100; i++) {
      int dwellings = (int)(Math.ceil(Math.random() * 10));
      int x = (int)(Math.ceil(Math.random() * 99));
      int y = (int)(Math.ceil(Math.random() * 99));
      totalDwelings += dwellings;
      groups.add(new DummyDwellingGroup(i + "", dwellings, new Point(x, y), i + ""));
    }
    for (DummyDwellingGroup group : groups) {
      group.setCommonName(group.getName());
      routing.addDwellingGroup(group, false);
    }
    Chunker chunker = new Chunker();
    Collection<IndivisbleChunk> indivisbleChunks = chunker.chunk(groups);
    List<Route> routes = router.createRoutes(routing, indivisbleChunks, totalDwelings / 10);
    final BufferedImage bufferedImage = new BufferedImage(100, (1 + routes.size()) * 101 - 1, BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, 100, (1 + routes.size()) * 101 - 1);
    paintDwellingGroups(g, 0, groups);
    int y = 101;
    for (Route route : routes) {
      paintDwellingGroups(g, y, route.getDwellingGroups());
      y += 101;
    }
    final JDialog dialog = new JDialog(null, "Routes", Dialog.ModalityType.APPLICATION_MODAL);
    dialog.getContentPane().setLayout(new BorderLayout());
    JPanel panel = new JPanel() {
      public void paint(Graphics g) {
        g.drawImage(bufferedImage, 0, 0, dialog);
      }
    };
    panel.setPreferredSize(new Dimension(100, (1 + routes.size()) * 101 - 1));
    JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    dialog.getContentPane().add(scrollPane);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
  }

  private void paintDwellingGroups(Graphics g, int y, Collection<? extends DwellingGroup> route) {
    Map<Point, Integer> blobs = new HashMap<Point, Integer>();
    for (DwellingGroup dwellingGroup : route) {
      Point point = dwellingGroup.getPoint();
      point = new Point(point.x, point.y + y);
      int size = dwellingGroup.size();
      Integer old = blobs.get(point);
      blobs.put(point, size + (old == null ? 0 : old));
    }
    g.setColor(Color.RED);
    for (Map.Entry<Point, Integer> entry : blobs.entrySet()) {
      int r = (int)(3 * Math.sqrt(entry.getValue()) / 2);
      Point p = entry.getKey();
      g.fillOval(p.x - r, p.y - r, r * 2, r * 2);
    }
    g.setColor(Color.BLACK);
    g.drawLine(0, y + 101, 100, y + 101);
  }

}
