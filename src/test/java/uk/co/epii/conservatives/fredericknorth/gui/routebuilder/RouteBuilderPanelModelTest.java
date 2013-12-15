package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.DummyMouseEvent;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.DummyBoundedArea;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.routes.DefaultRoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerImpl;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.opendata.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:08
 */
public class RouteBuilderPanelModelTest {

    private static DummyDwellingGroup bRoad;
    private static DummyDwellingGroup bRoadFlats;
    private static DummyDwellingGroup cStreet;
    private static DummyDwellingGroup eGrove;
    private static DummyDwellingGroup eGroveAppartments;
    private static DummyDwellingGroup fRoad;

    private RouteBuilderPanelModel routeBuilderPanelModel;
    private RoutableArea routableArea;

    @Before
    public void setUp() throws Exception {
        BoundedArea neighbourhood = new DummyBoundedArea(BoundedAreaType.NEIGHBOURHOOD,
                "A Bounded Area", new Polygon(new int[] {0, 0, 100, 100}, new int[] {0, 100, 100, 0}, 4));
        routableArea = new DefaultRoutableArea(neighbourhood, null);
        bRoad = new DummyDwellingGroup("B Road", 25, new Point(10, 90));
        bRoad.setPostcode(new DummyPostcodeDatum("A1 1AA"));
        routableArea.addDwellingGroup(bRoad, false);
        bRoadFlats = new DummyDwellingGroup("D Flats 26, B Road", 10, new Point(9, 89));
        bRoadFlats.setPostcode(new DummyPostcodeDatum("A1 1AA"));
        routableArea.addDwellingGroup(bRoadFlats, false);
        cStreet = new DummyDwellingGroup("C Street", 25, new Point(50, 10));
        cStreet.setPostcode(new DummyPostcodeDatum("A1 1AB"));
        routableArea.addDwellingGroup(cStreet, false);
        eGrove = new DummyDwellingGroup("E Grove", 25, new Point(90, 90));
        eGrove.setPostcode(new DummyPostcodeDatum("A1 1AC"));
        routableArea.addDwellingGroup(eGrove, false);
        eGroveAppartments = new DummyDwellingGroup("Apartment 26, E Grove", 25, new Point(91, 91));
        eGroveAppartments.setPostcode(new DummyPostcodeDatum("A1 1AC"));
        routableArea.addDwellingGroup(eGroveAppartments, false);
        ApplicationContext applicationContext = new TestApplicationContext();
        applicationContext.registerDefaultInstance(XMLSerializer.class, new XMLSerializerImpl());
        applicationContext.registerDefaultInstance(OSMapLoader.class, new DummyOSMapLoader());
        OSMapLocatorRegistrar.registerToContext(applicationContext);
        applicationContext.registerDefaultInstance(MapViewGenerator.class,
                DummyMapViewGeneratorFactory.getDummyInstance(applicationContext,
                        OSMapType.STREET_VIEW, new Rectangle(0, 0, 100, 100)));
        DotFactoryRegistrar.registerToContext(applicationContext);
        HashMap<BoundedArea, RoutableArea> routableAreas = new HashMap<BoundedArea, RoutableArea>();
        routableAreas.put(routableArea.getBoundedArea(), routableArea);
        routeBuilderPanelModel = new RouteBuilderPanelModel(applicationContext,
                new DummyBoundedAreaSelectionModel(routableArea.getBoundedArea()), routableAreas);
        routeBuilderPanelModel.getMapPanelModel().setMapImageObserver(new MapImageObserver() {
            @Override
            public void imageUpdated(MapImage mapImage, Rectangle update, boolean completed) {
                if (completed) {
                    routeBuilderPanelModel.enable();
                }
            }
        });
        routeBuilderPanelModel.setSelectedBoundedArea(neighbourhood);
        while (!routeBuilderPanelModel.isEnabled()) {
            Thread.sleep(100l);
        }
    }

    private void initiateAndDraw() throws InterruptedException, InvocationTargetException {
        routeBuilderPanelModel.setSelectedBoundedArea(routableArea.getBoundedArea());
        routeBuilderPanelModel.getMapPanelModel().setOverlayRenderer(DwellingGroup.class, new DottedDwellingGroupOverlayRenderer());
        routeBuilderPanelModel.getMapPanelModel().setViewportSize(new Dimension(50, 50));
        routeBuilderPanelModel.getMapPanelModel().setScale(0.5);
        routeBuilderPanelModel.getMapPanelModel().setGeoCenter(new Point(50, 50));
        routeBuilderPanelModel.updateOverlays();
        routeBuilderPanelModel.getMapPanelModel().getCurrentMapView();
        final MapPanel mapPanel = new MapPanel(routeBuilderPanelModel.getMapPanelModel(), 1.2d);
        mapPanel.setSize(50, 50);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
                mapPanel.paint(image.getGraphics());
            }
        });
    }

    public void initRoutes() {
        routeBuilderPanelModel.getRoutesModel().add("Route 1");
        Route route = routeBuilderPanelModel.getRoutesModel().getElementAt(0);
        route.addDwellingGroups(Arrays.asList(new Object[] {bRoad, cStreet}));
    }

    private void tidyInvokeAndWait(Runnable r) {
        try {
            SwingUtilities.invokeAndWait(r);
        }
        catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        catch (InvocationTargetException ite) {
            throw new RuntimeException(ite);
        }
    }

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipFromSelectionArea() throws InvocationTargetException, InterruptedException {
        initRoutes();
        initiateAndDraw();
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        final List<OverlayItem>[] overlays = new List[1];
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final Map<OverlayItem, MouseLocation>[] mouseOverOverlays = new Map[1];
        tidyInvokeAndWait(new Runnable() {
                @Override
                public void run() {
                routeBuilderPanelModel.getMapPanelModel().doubleClicked(new DummyMouseEvent(new Point(-10, -10)));
                overlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlayItems();
                mouseOverOverlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
            }
        });
        assertEquals("overlays", 5, overlays[0].size());
        assertEquals("mouseOverOverlays", 0, mouseOverOverlays[0].size());
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                ((RouteBuilderMapPanelModel) routeBuilderPanelModel.getMapPanelModel()).mouseMovedTo(new Point(20, 20));
                mouseOverOverlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
            }
        });
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays[0].size());
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
            }
        });
        assertTrue("B Road: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGroveAppartments));
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routeBuilderPanelModel.getUnroutedDwellingGroups().getRowCount();
                selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
            }
        });
        assertEquals("B Road: ", bRoad, selected[0].get(0));
        assertEquals("D Flats 26, B Road: ", bRoadFlats, unselected[0].get(0));
    }

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipListTest1() throws InvocationTargetException, InterruptedException {
        initRoutes();
        initiateAndDraw();
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        final List<OverlayItem>[] overlays = new List[1];
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final Map<OverlayItem, MouseLocation>[] mouseOverOverlays = new Map[1];
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                ((RouteBuilderMapPanelModel) routeBuilderPanelModel.getMapPanelModel()).mouseStablized(new Point(4, 4));
                overlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlayItems();
            }
        });
        assertEquals("overlays", 5, overlays[0].size());
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                mouseOverOverlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
            }
        });
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays[0].size());
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
            }
        });
        assertTrue("B Road: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGroveAppartments));
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routeBuilderPanelModel.getUnroutedDwellingGroups().getRowCount();
                selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
            }
        });
        assertEquals("B Road: ", bRoad, selected[0].get(0));
        assertEquals("D Flats 26, B Road: ", bRoadFlats, unselected[0].get(0));
    }

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipListTest2() throws InvocationTargetException, InterruptedException {
        initiateAndDraw();
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        final List<OverlayItem>[] overlays = new List[1];
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final Map<OverlayItem, MouseLocation>[] mouseOverOverlays = new Map[1];
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                ((RouteBuilderMapPanelModel) routeBuilderPanelModel.getMapPanelModel()).mouseStablized(new Point(46, 4));
                overlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlayItems();
            }
        });
        assertEquals("overlays", 5, overlays[0].size());
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                mouseOverOverlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
            }
        });
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays[0].size());
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
            }
        });
        assertTrue("B Road: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGroveAppartments));
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routeBuilderPanelModel.getUnroutedDwellingGroups().getRowCount();
                selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
            }
        });
        assertEquals("selected: ", 0, selected[0].size());
        assertEquals("unselected: ", 2, unselected[0].size());
        assertEquals("Apartment 26, E Grove: ", eGroveAppartments, unselected[0].get(0));
        assertEquals("E Grove: ", eGrove, unselected[0].get(1));
    }

    @Test
    public void routedAndUnroutedToolTipListSelectsAllOnDoubleClickTest() throws InvocationTargetException, InterruptedException {
        initiateAndDraw();
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
                routeBuilderPanelModel.getMapPanelModel().doubleClicked(new MouseEvent(new JPanel(), 0, 0l, 0, 46, 4, 1, false));
                routeBuilderPanelModel.getMapPanelModel().mouseMovedTo(new Point(45, 4));
            }
        });
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        tidyInvokeAndWait(new Runnable() {
            @Override
            public void run() {
                selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
            }
        });
        assertEquals("selected: ", 2, selected[0].size());
        assertEquals("unselected: ", 0, unselected[0].size());
        assertEquals("Apartment 26, E Grove: ", eGroveAppartments, selected[0].get(0));
        assertEquals("E Grove: ", eGrove, selected[0].get(1));
    }

    @Test
    public void loadTest() throws InvocationTargetException, InterruptedException {
        initiateAndDraw();
        try {
            routeBuilderPanelModel.load(new File(RouteBuilderPanelModelTest.class.getResource(
                    "/uk/co/epii/conservatives/fredericknorth/gui/routebuilder/TestRouteableArea.xml").toURI()));
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        while (!routeBuilderPanelModel.isEnabled()) {
            try {Thread.sleep(100L);} catch (InterruptedException ie) {}
        }
        assertEquals(1, routeBuilderPanelModel.getRoutesModel().getSize());
        Route route = routeBuilderPanelModel.getRoutesModel().getElementAt(0);
        assertEquals(3, route.getDwellingGroups().size());
        HashSet<String> dwellingGroupNames = new HashSet<String>();
        for (DwellingGroup dwellingGroup : route.getDwellingGroups()) {
            dwellingGroupNames.add(dwellingGroup.getName());
        }
        assertTrue(dwellingGroupNames.contains("B Road"));
        assertTrue(dwellingGroupNames.contains("Apartment 26, E Grove"));
        assertTrue(dwellingGroupNames.contains("C Street"));
    }

}
