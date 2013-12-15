package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Before;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyPostcodeDatum;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 16:55
 */
public class RouteImplTest {

    private DefaultRoutableArea defaultRoutableArea;
    private RouteImpl routeImpl;
    private DummyDwellingGroup bRoad;
    private DummyDwellingGroup bRoadFlats;
    private DummyDwellingGroup cStreet;


    @Before
    public void setUp() throws Exception {
        bRoad = new DummyDwellingGroup("B Road", 20, new Point(0, 0));
        bRoad.setPostcode(new DummyPostcodeDatum("A1 1AA"));
        bRoadFlats = new DummyDwellingGroup("Flat 26, B Road", 20, new Point(0, 0));
        bRoadFlats.setPostcode(new DummyPostcodeDatum("A1 1AB"));
        cStreet = new DummyDwellingGroup("C Street", 20, new Point(25, 25));
        cStreet.setPostcode(new DummyPostcodeDatum("A1 1AC"));
        ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroups.add(bRoad);
        dwellingGroups.add(bRoadFlats);
        dwellingGroups.add(cStreet);
        defaultRoutableArea = new DefaultRoutableArea(null, null);
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            defaultRoutableArea.addDwellingGroup(dwellingGroup, false);
        }
        routeImpl = new RouteImpl(defaultRoutableArea, "Route 1");
    }

    @Test
    public void addDwellingGroupsTest() {
        ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroups.add(bRoad);
        routeImpl.addDwellingGroups(dwellingGroups);
        assertTrue("B Road Present", routeImpl.getDwellingGroups().contains(bRoad));
        assertEquals("B Road Alone", 1, routeImpl.getDwellingGroups().size());
        assertEquals("Ward Dwellings ", 60, defaultRoutableArea.getDwellingCount());
        assertEquals("Route Dwellings ", 20, routeImpl.getDwellingCount());
        assertEquals("Routed Dwellings ", 40, defaultRoutableArea.getUnroutedDwellingCount());
    }

    @Test
    public void removeDwellingGroupsTest() {
        ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroups.add(bRoad);
        dwellingGroups.add(bRoadFlats);
        routeImpl.addDwellingGroups(dwellingGroups);
        assertTrue("B Road Present", routeImpl.getDwellingGroups().contains(bRoad));
        assertTrue("B Road Flats Present", routeImpl.getDwellingGroups().contains(bRoadFlats));
        assertEquals("B Roads and Flats are alone", 2, routeImpl.getDwellingGroups().size());
        assertEquals("Ward Dwellings ", 60,  defaultRoutableArea.getDwellingCount());
        assertEquals("Route Dwellings ", 40, routeImpl.getDwellingCount());
        assertEquals("Routed Dwellings ", 20, defaultRoutableArea.getUnroutedDwellingCount());
        dwellingGroups.clear();
        dwellingGroups.add(bRoadFlats);
        routeImpl.removeDwellingGroups(dwellingGroups);
        assertTrue("B Road Present", routeImpl.getDwellingGroups().contains(bRoad));
        assertEquals("B Road alone", 1, routeImpl.getDwellingGroups().size());
        assertEquals("Ward Dwellings ", 60, defaultRoutableArea.getDwellingCount());
        assertEquals("Route Dwellings ", 20, routeImpl.getDwellingCount());
        assertEquals("Routed Dwellings ", 40, defaultRoutableArea.getUnroutedDwellingCount());
    }
}
