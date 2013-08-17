package uk.co.epii.conservatives.fredericknorth.routes;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.ResourceHelper;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.maps.LocationFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupTestFactory;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessorRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeProcessorRegistrar;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerRegistrar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 01:20
 */
public class CouncilTest {

    @Test
    public void simpleCouncilToXmlTest() throws Exception {
        CouncilImpl council = new CouncilImpl(new ArrayList<Location>(), new HashMap<String, WardImpl>(), null, 0, null, null);
        WardImpl ward = new WardImpl("E05000583", null);
        council.addWard(ward);
        Route route = ward.createRoute("Route 2");
        route.getDwellingGroups().add(DwellingGroupTestFactory.getInstance(
                "CHAPEL HOUSE STREET, LONDON", "CHAPEL HOUSE STREET, LONDON", "E14 3AS"));
        route.getDwellingGroups().add(DwellingGroupTestFactory.getInstance(
                "THERMOPYLAE GATE, LONDON", "THERMOPYLAE GATE, LONDON", "E14 3AX"));
        route.getDwellingGroups().add(DwellingGroupTestFactory.getInstance(
                "MACQUARIE WAY, LONDON", "MACQUARIE WAY, LONDON", "E14 3AU"));
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element xml = council.toXml(document);
        document.appendChild(xml);
        String result = ResourceHelper.toString(document);
        String expected = ResourceHelper.readResource(RouteTest.class.getResource("/simpleCouncil.xml"));
        assertEquals(expected, result);
    }

    @Test
    public void loadMillwallRoutesTest() {
        TestApplicationContext applicationContext = new TestApplicationContext();
        PostcodeDatumFactoryRegistrar.registerToContext(applicationContext);
        DwellingProcessorRegistrar.registerToContext(applicationContext, new NullProgressTracker(),
                CouncilTest.class.getResourceAsStream("/millwallDwellingsSubset.txt"));
        PostcodeProcessorRegistrar.registerToContext(applicationContext, CouncilTest.class.getResourceAsStream("/millwallPostcodeSubset.txt"), 0);
        XMLSerializerRegistrar.registerToContext(applicationContext);
        LocationFactoryRegistrar.registerToContext(applicationContext);
        CouncilRegistrar.registerToContext(applicationContext,
                CouncilTest.class.getResource("/millwallWard.txt"),
                CouncilTest.class.getResource("/millwallMeetingPointsSubset.csv"));
        String tempFontLocation = System.getProperty("java.io.tmpdir") + "millwallRoutesSubset.xml";
        File file = new File(tempFontLocation);
        try {
            FileUtils.copyURLToFile(CouncilTest.class.getResource("/millwallRoutesSubset.xml"), file);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        Council council = applicationContext.getDefaultInstance(Council.class);
        council.load(applicationContext, file);
        assertEquals(1, council.getWards().size());
        Ward millwall = council.getWard("E05000583");
        assertEquals(12, millwall.getRouteCount());
        List<Route> routes = new ArrayList<Route>(millwall.getRoutes());
        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        assertEquals("Telegraph Place", routes.get(8).getName());
        assertEquals(7, routes.get(8).getDwellingGroups().size());
        assertEquals("Route 9", routes.get(6).getName());
        assertEquals(10, routes.get(6).getDwellingGroups().size());
        assertEquals("Route 8", routes.get(5).getName());
        assertEquals(14, routes.get(5).getDwellingGroups().size());
        assertEquals("Transom Square", routes.get(10).getName());
        assertEquals(2, routes.get(10).getDwellingGroups().size());
        assertEquals("Undine Road", routes.get(11).getName());
        assertEquals(4, routes.get(11).getDwellingGroups().size());
        assertEquals("Route 3", routes.get(3).getName());
        assertEquals(6, routes.get(3).getDwellingGroups().size());
        assertEquals("Route 4", routes.get(4).getName());
        assertEquals(2, routes.get(4).getDwellingGroups().size());
        assertEquals("Harbinger Road", routes.get(0).getName());
        assertEquals(5, routes.get(0).getDwellingGroups().size());
        assertEquals("The Forge", routes.get(9).getName());
        assertEquals(6, routes.get(9).getDwellingGroups().size());
        assertEquals("Taeping Street", routes.get(7).getName());
        assertEquals(3, routes.get(7).getDwellingGroups().size());
        assertEquals("Route 1", routes.get(2).getName());
        assertEquals(10, routes.get(2).getDwellingGroups().size());
        assertEquals("Lockesfield Place", routes.get(1).getName());
        assertEquals(7, routes.get(1).getDwellingGroups().size());
    }
}
