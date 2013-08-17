package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.DummyMapViewGeneratorFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelModel;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyCouncil;
import uk.co.epii.conservatives.fredericknorth.routes.Council;

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
        MapViewGenerator mapViewGenerator =
                DummyMapViewGeneratorFactory.getDummyInstance(new Rectangle(100, 200, 300, 400));
        ApplicationContext applicationContext = new TestApplicationContext();
        applicationContext.registerDefaultInstance(Council.class, new DummyCouncil());
        applicationContext.registerDefaultInstance(MapViewGenerator.class, mapViewGenerator);
        RouteBuilderMapFrameModel routeBuilderMapFrameModel = new RouteBuilderMapFrameModel(applicationContext);
        MapPanelModel mapViewModel = new RouteBuilderMapPanelModel(routeBuilderMapFrameModel, 0);
        mapViewGenerator.setViewPortSize(new Dimension(150, 100));
        mapViewGenerator.setScale(0.5);
        mapViewGenerator.setGeoCenter(new Point(250, 400));
        mapViewModel.getCurrentMapView();
        mapViewModel.zoomIn(new Point(50, 25), 2d);
        double expectedScale = 1d;
        Point expectedGeoCenter = new Point(225, 425);
        assertEquals(expectedScale, mapViewGenerator.getScale(), 0.00000001);
        assertEquals(expectedGeoCenter, mapViewGenerator.getGeoCenter());
    }

    @Test
    public void dragMapTest() {
        MapViewGenerator mapViewGenerator =
                DummyMapViewGeneratorFactory.getDummyInstance(new Rectangle(100, 200, 300, 400));
        ApplicationContext applicationContext = new TestApplicationContext();
        applicationContext.registerDefaultInstance(Council.class, new DummyCouncil());
        applicationContext.registerDefaultInstance(MapViewGenerator.class, mapViewGenerator);
        RouteBuilderMapFrameModel routeBuilderMapFrameModel = new RouteBuilderMapFrameModel(applicationContext);
        MapPanelModel mapViewModel = new RouteBuilderMapPanelModel(routeBuilderMapFrameModel, 0);
        mapViewGenerator.setViewPortSize(new Dimension(150, 100));
        mapViewGenerator.setScale(0.5);
        mapViewGenerator.setGeoCenter(new Point(250, 400));
        mapViewModel.getCurrentMapView();
        mapViewModel.setDragFrom(new Point(50, 25));
        mapViewModel.moveDraggedFrom(new Point(100, 50));
        Point expectedGeoCenter = new Point(150, 450);
        assertEquals(expectedGeoCenter, mapViewGenerator.getGeoCenter());
    }

}
