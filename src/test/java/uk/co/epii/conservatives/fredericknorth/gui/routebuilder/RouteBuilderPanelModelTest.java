package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Before;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.DummyBoundedArea;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLoaderRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapType;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.DummyMapViewGeneratorFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.opendata.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RouteBuilderPanelModel routeBuilderPanelModel;

    @Before
    public void setUp() throws Exception {
        BoundedArea neighbourhood = new DummyBoundedArea(BoundedAreaType.NEIGHBOURHOOD,
                "A Bounded Area", new Polygon(new int[] {0, 0, 100, 100}, new int[] {0, 100, 100, 0}, 4));
        DummyRoutableArea dummyRoutableArea = new DummyRoutableArea(neighbourhood, null, "A RoutableArea", "A");
        bRoad = new DummyDwellingGroup("B Road", 25, new Point(10, 90));
        bRoad.setPostcode(new DummyPostcodeDatum("A1 1AA"));
        dummyRoutableArea.addDwellingGroup(bRoad);
        bRoadFlats = new DummyDwellingGroup("D Flats 26, B Road", 10, new Point(10, 90));
        bRoadFlats.setPostcode(new DummyPostcodeDatum("A1 1AA"));
        dummyRoutableArea.addDwellingGroup(bRoadFlats);
        cStreet = new DummyDwellingGroup("C Street", 25, new Point(50, 10));
        cStreet.setPostcode(new DummyPostcodeDatum("A1 1AB"));
        dummyRoutableArea.addDwellingGroup(cStreet);
        eGrove = new DummyDwellingGroup("E Grove", 25, new Point(90, 90));
        eGrove.setPostcode(new DummyPostcodeDatum("A1 1AC"));
        dummyRoutableArea.addDwellingGroup(eGrove);
        eGroveAppartments = new DummyDwellingGroup("Apartment 26, E Grove", 25, new Point(90, 90));
        eGroveAppartments.setPostcode(new DummyPostcodeDatum("A1 1AC"));
        dummyRoutableArea.addDwellingGroup(eGroveAppartments);
        DummyRoute dummyRoute = new DummyRoute("Route 1", dummyRoutableArea);
        dummyRoute.addDwellingGroup(bRoad);
        dummyRoute.addDwellingGroup(cStreet);
        dummyRoutableArea.addRoute(dummyRoute);
        ApplicationContext applicationContext = new TestApplicationContext();
        OSMapLoaderRegistrar.registerToContext(applicationContext);
        applicationContext.registerDefaultInstance(MapViewGenerator.class,
                DummyMapViewGeneratorFactory.getDummyInstance(OSMapType.STREET_VIEW, new Rectangle(0, 0, 100, 100)));
        DotFactoryRegistrar.registerToContext(applicationContext);
        HashMap<BoundedArea, RoutableArea> routableAreas = new HashMap<BoundedArea, RoutableArea>();
        routableAreas.put(dummyRoutableArea.getBoundedArea(), dummyRoutableArea);
        routeBuilderPanelModel = new RouteBuilderPanelModel(applicationContext, new DummyBoundedAreaSelectionModel(dummyRoutableArea.getBoundedArea()), routableAreas);
        routeBuilderPanelModel.setSelectedBoundedArea(dummyRoutableArea.getBoundedArea());
        routeBuilderPanelModel.getMapPanelModel().setOverlayRenderer(DottedDwellingGroup.class, new DottedDwellingGroupOverlayRenderer());
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

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipListTest1() {
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        final List<OverlayItem>[] overlays = new List[1];
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final Map<OverlayItem, MouseLocation>[] mouseOverOverlays = new Map[1];
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    ((RouteBuilderMapPanelModel) routeBuilderPanelModel.getMapPanelModel()).mouseStablized(new Point(4, 4));
                    overlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlayItems();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        assertEquals("overlays", 5, overlays[0].size());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    mouseOverOverlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays[0].size());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
                }
            });
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertTrue("B Road: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGroveAppartments));
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    routeBuilderPanelModel.getUnroutedDwellingGroups().getRowCount();
                    selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                    unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertEquals("B Road: ", bRoad, selected[0].get(0));
        assertEquals("D Flats 26, B Road: ", bRoadFlats, unselected[0].get(0));
    }

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipListTest2() {
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        final List<OverlayItem>[] overlays = new List[1];
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final Map<OverlayItem, MouseLocation>[] mouseOverOverlays = new Map[1];
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    ((RouteBuilderMapPanelModel) routeBuilderPanelModel.getMapPanelModel()).mouseStablized(new Point(46, 4));
                    overlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlayItems();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertEquals("overlays", 5, overlays[0].size());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    mouseOverOverlays[0] = routeBuilderPanelModel.getMapPanelModel().getImmutableOverlaysMouseOver();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays[0].size());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertTrue("B Road: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", routedAndUnroutedToolTipModel[0].getDwellingGroupModel().contains(eGroveAppartments));
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    routeBuilderPanelModel.getUnroutedDwellingGroups().getRowCount();
                    selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                    unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertEquals("selected: ", 0, selected[0].size());
        assertEquals("unselected: ", 2, unselected[0].size());
        assertEquals("Apartment 26, E Grove: ", eGroveAppartments, unselected[0].get(0));
        assertEquals("E Grove: ", eGrove, unselected[0].get(1));
    }

    @Test
    public void routedAndUnroutedToolTipListSelectsAllOnDoubleClickTest() {
        final List<DwellingGroup>[] selected = new List[1];
        final List<DwellingGroup>[] unselected = new List[1];
        final RoutedAndUnroutedToolTipModel[] routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel[1];
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    routedAndUnroutedToolTipModel[0] = routeBuilderPanelModel.getRoutedAndUnroutedToolTipModel();
                    routeBuilderPanelModel.getMapPanelModel().doubleClicked(new MouseEvent(new JPanel(), 0, 0l, 0, 46, 4, 1, false));
                    routeBuilderPanelModel.getMapPanelModel().mouseMovedTo(new Point(45, 4));
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    selected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.SELECTED);
                    unselected[0] = routedAndUnroutedToolTipModel[0].getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        assertEquals("selected: ", 2, selected[0].size());
        assertEquals("unselected: ", 0, unselected[0].size());
        assertEquals("Apartment 26, E Grove: ", eGroveAppartments, selected[0].get(0));
        assertEquals("E Grove: ", eGrove, selected[0].get(1));
    }

}
