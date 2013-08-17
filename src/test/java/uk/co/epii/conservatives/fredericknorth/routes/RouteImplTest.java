package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Before;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
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

    private WardImpl wardImpl;
    private RouteImpl routeImpl;
    private DummyDwellingGroup bRoad;
    private DummyDwellingGroup bRoadFlats;
    private DummyDwellingGroup cStreet;


    @Before
    public void setUp() throws Exception {
        bRoad = new DummyDwellingGroup("B Road", 20, new Point(0, 0));
        bRoadFlats = new DummyDwellingGroup("Flat 26, B Road", 20, new Point(0, 0));
        cStreet = new DummyDwellingGroup("C Street", 20, new Point(25, 25));
        ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroups.add(bRoad);
        dwellingGroups.add(bRoadFlats);
        dwellingGroups.add(cStreet);
        wardImpl = new WardImpl("A Ward", dwellingGroups, "A");
        routeImpl = new RouteImpl(wardImpl, "Route 1");
    }

    @Test
    public void addDwellingGroupsTest() {
        ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroups.add(bRoad);
        routeImpl.addDwellingGroups(dwellingGroups);
        assertTrue("B Road Present", routeImpl.getDwellingGroups().contains(bRoad));
        assertEquals("B Road Alone", 1, routeImpl.getDwellingGroups().size());
        assertEquals("Ward Dwellings ", 60, wardImpl.getDwellingCount());
        assertEquals("Route Dwellings ", 20, routeImpl.getDwellingCount());
        assertEquals("Routed Dwellings ", 40, wardImpl.getUnroutedDwellingCount());
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
        assertEquals("Ward Dwellings ", 60,  wardImpl.getDwellingCount());
        assertEquals("Route Dwellings ", 40, routeImpl.getDwellingCount());
        assertEquals("Routed Dwellings ", 20, wardImpl.getUnroutedDwellingCount());
        dwellingGroups.clear();
        dwellingGroups.add(bRoadFlats);
        routeImpl.removeDwellingGroups(dwellingGroups);
        assertTrue("B Road Present", routeImpl.getDwellingGroups().contains(bRoad));
        assertEquals("B Road alone", 1, routeImpl.getDwellingGroups().size());
        assertEquals("Ward Dwellings ", 60, wardImpl.getDwellingCount());
        assertEquals("Route Dwellings ", 20, routeImpl.getDwellingCount());
        assertEquals("Routed Dwellings ", 40, wardImpl.getUnroutedDwellingCount());
    }
}
