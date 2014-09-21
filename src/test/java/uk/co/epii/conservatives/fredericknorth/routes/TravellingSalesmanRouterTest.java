package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaConstructor;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 18/09/2014
 * Time: 02:20
 */
public class TravellingSalesmanRouterTest {

  @Test
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
    groups.add(new DummyDwellingGroup("B1", 10, new Point(5,5)));
    groups.add(new DummyDwellingGroup("B2", 9, new Point(5,50)));
    groups.add(new DummyDwellingGroup("B3", 8, new Point(5,95)));
    groups.add(new DummyDwellingGroup("B4", 7, new Point(95,5)));
    groups.add(new DummyDwellingGroup("B5", 6, new Point(95,5)));
    groups.add(new DummyDwellingGroup("B6", 5, new Point(95,95)));
    for (DummyDwellingGroup group : groups) {
      group.setCommonName(group.getName());
      routing.addDwellingGroup(group, false);
    }
    List<Route> routes = router.createRoutes(routing, routing.getUnroutedIndivisbleChunks(), 30);
    assertEquals(routes.size(), 2);
  }

}
