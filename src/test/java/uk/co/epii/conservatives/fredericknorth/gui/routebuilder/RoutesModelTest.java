package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.DummyMapViewGeneratorFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyRoutableArea;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 19:10
 */
public class RoutesModelTest {

    @Test
    public void addingRoutesEndUpInAlphabeticalOrder() {
        TestApplicationContext applicationContext = new TestApplicationContext();
        applicationContext.registerDefaultInstance(MapViewGenerator.class,
                DummyMapViewGeneratorFactory.getDummyInstance(new Rectangle(1, 1, 1, 1)));
        DummyRoutableArea dummyRoutableArea = new DummyRoutableArea(null, null, "A Ward", "A");
        RoutesModel routesModel = new RoutesModel(
                new RouteBuilderMapFrameModel(applicationContext));
        routesModel.setSelectedRoutableArea(dummyRoutableArea);
        routesModel.add("Route 1");
        routesModel.add("Route 2");
        routesModel.add("Route 3");
        routesModel.add("Route 4");
        assertEquals("Route 1", routesModel.getElementAt(0).getName());
        assertEquals("Route 2", routesModel.getElementAt(1).getName());
        assertEquals("Route 3", routesModel.getElementAt(2).getName());
        assertEquals("Route 4", routesModel.getElementAt(3).getName());
    }

}
