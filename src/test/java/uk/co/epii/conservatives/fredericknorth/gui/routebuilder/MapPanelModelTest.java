package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLoaderRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapType;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.DummyMapViewGeneratorFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelModel;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 16:02
 */
public class MapPanelModelTest {

    @Test
    public void zoomInTest() {
        ApplicationContext applicationContext = new TestApplicationContext();
        OSMapLoaderRegistrar.registerToContext(applicationContext);
        MapViewGenerator mapViewGenerator =
                DummyMapViewGeneratorFactory.getDummyInstance(applicationContext,
                        OSMapType.STREET_VIEW, new Rectangle(100, 200, 300, 400));
        applicationContext.registerDefaultInstance(MapViewGenerator.class, mapViewGenerator);
        RouteBuilderPanelModel routeBuilderPanelModel =
                new RouteBuilderPanelModel(applicationContext, new DummyBoundedAreaSelectionModel(null));
        MapPanelModel mapViewModel = new RouteBuilderMapPanelModel(routeBuilderPanelModel, 0);
        mapViewGenerator.setViewPortSize(new Dimension(150, 100), NullProgressTracker.NULL, null);
        mapViewGenerator.setScale(0.5, NullProgressTracker.NULL, null);
        mapViewGenerator.setGeoCenter(new Point(250, 400), NullProgressTracker.NULL, null);
        mapViewModel.getCurrentMapView();
        mapViewModel.zoomIn(new Point(50, 25), 2d);
        double expectedScale = 1d;
        Point expectedGeoCenter = new Point(225, 425);
        assertEquals(expectedScale, mapViewGenerator.getScale(), 0.00000001);
        assertEquals(expectedGeoCenter, mapViewGenerator.getGeoCenter());
    }

    @Test
    public void dragMapTest() {
        ApplicationContext applicationContext = new TestApplicationContext();
        OSMapLoaderRegistrar.registerToContext(applicationContext);
        MapViewGenerator mapViewGenerator =
                DummyMapViewGeneratorFactory.getDummyInstance(applicationContext,
                        OSMapType.STREET_VIEW, new Rectangle(100, 200, 300, 400));
        applicationContext.registerDefaultInstance(MapViewGenerator.class, mapViewGenerator);
        RouteBuilderPanelModel routeBuilderPanelModel =
                new RouteBuilderPanelModel(applicationContext, new DummyBoundedAreaSelectionModel(null));
        MapPanelModel mapViewModel = new RouteBuilderMapPanelModel(routeBuilderPanelModel, 0);
        mapViewGenerator.setViewPortSize(new Dimension(150, 100), NullProgressTracker.NULL, null);
        mapViewGenerator.setScale(0.5, NullProgressTracker.NULL, null);
        mapViewGenerator.setGeoCenter(new Point(250, 400), NullProgressTracker.NULL, null);
        mapViewModel.getCurrentMapView();
        mapViewModel.setDragFrom(new Point(50, 25));
        mapViewModel.moveDraggedFrom(new Point(100, 50));
        Point expectedGeoCenter = new Point(150, 450);
        assertEquals(expectedGeoCenter, mapViewGenerator.getGeoCenter());
    }

}
