package uk.co.epii.conservatives.fredericknorth.routes;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.boundaryline.DefaultBoundedArea;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.DistributionModelImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.pdf.RenderedRoute;
import uk.co.epii.conservatives.fredericknorth.pdf.RenderedRouteFactoryImpl;
import uk.co.epii.conservatives.fredericknorth.utilities.DefaultApplicationContext;
import uk.co.epii.politics.williamcavendishbentinck.DatabaseSession;

import java.awt.*;
import java.util.Date;

/**
 * User: James Robinson
 * Date: 28/09/2014
 * Time: 22:05
 */
public class RoutePublisherImplTest {

  @Ignore
  @Test
  public void publishSimpleRoute() {
    ApplicationContext springContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    DefaultApplicationContext applicationContext =
            new DefaultApplicationContext(DefaultApplicationContext.DEFAULT_CONFIG_LOCATION);
    DatabaseSession databaseSession = (DatabaseSession)springContext.getBean("databaseSession");
    RoutePublisherImpl routePublisher = new RoutePublisherImpl();
    DistributionModel distributionModel = new DistributionModelImpl();
    distributionModel.setDescription("Description");
    distributionModel.setTitle("Title");
    distributionModel.setDistributionStart(new Date());
    RenderedRouteFactoryImpl renderedRouteFactory = new RenderedRouteFactoryImpl("http://epii.co.uk/pl/");
    DefaultBoundedArea council = new DefaultBoundedArea(null, BoundedAreaType.UNITARY_DISTRICT, "Test Council");
    DefaultBoundedArea ward = new DefaultBoundedArea(council, BoundedAreaType.UNITARY_DISTRICT_WARD, "Test Ward");
    DefaultBoundedArea pollingDistrict = new DefaultBoundedArea(ward, BoundedAreaType.POLLING_DISTRICT, "Test Polling District");
    DefaultBoundedArea neighbourhood = new DefaultBoundedArea(pollingDistrict, BoundedAreaType.NEIGHBOURHOOD, "Test Neighbourhood");
    DefaultRoutableArea routableCouncil = new DefaultRoutableArea(council, null);
    DefaultRoutableArea routableWard = new DefaultRoutableArea(ward, routableCouncil);
    DefaultRoutableArea routablePollingDistrict = new DefaultRoutableArea(pollingDistrict, routableWard);
    DefaultRoutableArea routableNeighbourhood = new DefaultRoutableArea(neighbourhood, routablePollingDistrict);
    RouteImpl route = new RouteImpl(routableNeighbourhood, "Test Route 1");
    route.addDwellingGroups(Arrays.asList(new DwellingGroup[] {
            new DummyDwellingGroup("Test Dwelling Group 1", 5, new Point(0, 0)),
            new DummyDwellingGroup("Test Dwelling Group 2", 5, new Point(10, 0)),
            new DummyDwellingGroup("Test Dwelling Group 3", 5, new Point(0, 10))
    }));
    RouteImpl route2 = new RouteImpl(routableNeighbourhood, "Test Route 2");
    route2.addDwellingGroups(Arrays.asList(new DwellingGroup[] {
            new DummyDwellingGroup("Test Dwelling Group 4", 5, new Point(10, 10)),
            new DummyDwellingGroup("Test Dwelling Group 5", 5, new Point(20, 0)),
            new DummyDwellingGroup("Test Dwelling Group 6", 5, new Point(0, 20))
    }));
    routePublisher.setDatabaseSession(databaseSession);
    routePublisher.publish(distributionModel, Arrays.asList(new RenderedRoute[] {
            renderedRouteFactory.getRenderedRoute(route),
            renderedRouteFactory.getRenderedRoute(route2)
    }));
  }

}
