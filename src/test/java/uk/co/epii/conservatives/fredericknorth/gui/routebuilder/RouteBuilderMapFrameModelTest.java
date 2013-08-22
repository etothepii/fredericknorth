package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Before;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MouseLocation;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.maps.DummyMapViewGeneratorFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.opendata.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 23:08
 */
public class RouteBuilderMapFrameModelTest {

    private static DummyDwellingGroup bRoad;
    private static DummyDwellingGroup bRoadFlats;
    private static DummyDwellingGroup cStreet;
    private static DummyDwellingGroup eGrove;
    private static DummyDwellingGroup eGroveAppartments;

    private RouteBuilderMapFrameModel routeBuilderMapFrameModel;

    @Before
    public void setUp() throws Exception {
        DummyRoutableArea dummyRoutableArea = new DummyRoutableArea("A RoutableArea", "A");
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
        applicationContext.registerDefaultInstance(MapViewGenerator.class,
                DummyMapViewGeneratorFactory.getDummyInstance(new Rectangle(0, 0, 100, 100)));
        DotFactoryRegistrar.registerToContext(applicationContext);
        routeBuilderMapFrameModel = new RouteBuilderMapFrameModel(applicationContext);
        routeBuilderMapFrameModel.getMapPanelModel().setOverlayRenderer(DottedDwellingGroup.class, new DottedDwellingGroupOverlayRenderer());
        routeBuilderMapFrameModel.getMapPanelModel().setViewportSize(new Dimension(50, 50));
        routeBuilderMapFrameModel.getMapPanelModel().setScale(0.5);
        routeBuilderMapFrameModel.getMapPanelModel().setGeoCenter(new Point(50, 50));
        routeBuilderMapFrameModel.updateOverlays();
        routeBuilderMapFrameModel.getMapPanelModel().getCurrentMapView();
    }

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipListTest1() {
        ((RouteBuilderMapPanelModel) routeBuilderMapFrameModel.getMapPanelModel()).mouseStablized(new Point(4, 4));
        List<OverlayItem> overlays = routeBuilderMapFrameModel.getMapPanelModel().getImmutableOverlayItems();
        assertEquals("overlays", 5, overlays.size());
        Map<OverlayItem, MouseLocation> mouseOverOverlays = routeBuilderMapFrameModel.getMapPanelModel().getImmutableOverlaysMouseOver();
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays.size());
        RoutedAndUnroutedToolTipModel routedAndUnroutedToolTipModel = routeBuilderMapFrameModel.getRoutedAndUnroutedToolTipModel();
        assertTrue("B Road: ", routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", !routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", !routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(eGroveAppartments));
        routeBuilderMapFrameModel.getUnroutedDwellingGroups().getRowCount();
        List<DwellingGroup> selected = routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.SELECTED);
        List<DwellingGroup> unselected = routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
        assertEquals("B Road: ", bRoad, selected.get(0));
        assertEquals("D Flats 26, B Road: ", bRoadFlats, unselected.get(0));
    }

    @Test
    public void correctlyBuildsRoutedAndUnroutedToolTipListTest2() {
        ((RouteBuilderMapPanelModel) routeBuilderMapFrameModel.getMapPanelModel()).mouseStablized(new Point(46, 4));
        List<OverlayItem> overlays = routeBuilderMapFrameModel.getMapPanelModel().getImmutableOverlayItems();
        assertEquals("overlays", 5, overlays.size());
        Map<OverlayItem, MouseLocation> mouseOverOverlays = routeBuilderMapFrameModel.getMapPanelModel().getImmutableOverlaysMouseOver();
        assertEquals("mouseOverOverlays", 2, mouseOverOverlays.size());
        RoutedAndUnroutedToolTipModel routedAndUnroutedToolTipModel = routeBuilderMapFrameModel.getRoutedAndUnroutedToolTipModel();
        assertTrue("B Road: ", !routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(bRoad));
        assertTrue("D Flats 26, B Road: ", !routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(bRoadFlats));
        assertTrue("C Street: ", !routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(cStreet));
        assertTrue("E Grove: ", routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(eGrove));
        assertTrue("Apartment 26, E Grove: ", routedAndUnroutedToolTipModel.getDwellingGroupModel().contains(eGroveAppartments));
        routeBuilderMapFrameModel.getUnroutedDwellingGroups().getRowCount();
        List<DwellingGroup> selected = routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.SELECTED);
        List<DwellingGroup> unselected = routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
        assertEquals("selected: ", 0, selected.size());
        assertEquals("unselected: ", 2, unselected.size());
        assertEquals("Apartment 26, E Grove: ", eGroveAppartments, unselected.get(0));
        assertEquals("E Grove: ", eGrove, unselected.get(1));
    }

    @Test
    public void routedAndUnroutedToolTipListSelectsAllOnDoubleClickTest() {
        final RoutedAndUnroutedToolTipModel routedAndUnroutedToolTipModel = routeBuilderMapFrameModel.getRoutedAndUnroutedToolTipModel();
        routeBuilderMapFrameModel.getMapPanelModel().doubleClicked(new MouseEvent(new JPanel(), 0, 0l, 0, 46, 4, 1, false));
        routeBuilderMapFrameModel.getMapPanelModel().mouseMovedTo(new Point(45, 4));
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<DwellingGroup> selected = routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.SELECTED);
        List<DwellingGroup> unselected = routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.UNSELECTED);
        assertEquals("selected: ", 2, selected.size());
        assertEquals("unselected: ", 0, unselected.size());
        assertEquals("Apartment 26, E Grove: ", eGroveAppartments, selected.get(0));
        assertEquals("E Grove: ", eGrove, selected.get(1));
    }

}
