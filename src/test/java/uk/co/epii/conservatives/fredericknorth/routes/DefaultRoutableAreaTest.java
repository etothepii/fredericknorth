package uk.co.epii.conservatives.fredericknorth.routes;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.boundaryline.DefaultBoundedArea;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingGroupDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.db.PostcodeDatumDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerImpl;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 13/11/2013
 * Time: 06:27
 */
public class DefaultRoutableAreaTest {

    private BoundedArea councilWard;
    private BoundedArea postalDistrictCW1;
    private BoundedArea postalDistrictCW2;
    private DefaultRoutableArea councilWardRoutes;
    private DefaultRoutableArea postalDistrictCW1Routes;
    private DefaultRoutableArea postalDistrictCW2Routes;
    private DwellingGroupDatabaseImpl cw1StreetA;
    private DwellingGroupDatabaseImpl cw1StreetB;
    private DwellingGroupDatabaseImpl cw2StreetC;
    private DwellingGroupDatabaseImpl cw2StreetD;
    private DwellingGroupDatabaseImpl cwStreetE;
    private DwellingGroupDatabaseImpl cwStreetF;
    private PostcodeDatumDatabaseImpl cw1Postcode;
    private PostcodeDatumDatabaseImpl cw2Postcode;
    private PostcodeDatumDatabaseImpl cwPostcode;
    private Route route1;
    private Route route2;
    private Route route3;

    @Before
    public void setUp() {
        councilWard = new DefaultBoundedArea(BoundedAreaType.UNITARY_DISTRICT_WARD, "Council Ward");
        postalDistrictCW1 = new DefaultBoundedArea(BoundedAreaType.POLLING_DISTRICT, "CW1");
        postalDistrictCW2 = new DefaultBoundedArea(BoundedAreaType.POLLING_DISTRICT, "CW2");
        councilWard.addChild(postalDistrictCW1);
        councilWard.addChild(postalDistrictCW2);
        councilWardRoutes = new DefaultRoutableArea(councilWard, null);
        postalDistrictCW1Routes = new DefaultRoutableArea(postalDistrictCW1, councilWardRoutes);
        postalDistrictCW2Routes = new DefaultRoutableArea(postalDistrictCW2, councilWardRoutes);
        councilWardRoutes.addChild(postalDistrictCW1Routes);
        councilWardRoutes.addChild(postalDistrictCW2Routes);
        Map<String, DwellingGroupDatabaseImpl> map = new HashMap<String, DwellingGroupDatabaseImpl>();
        cw1Postcode = createPostcodeDatumDatabaseImpl("CW1 1AA", new Point(15, 15), map);
        cw1StreetA = createDwellingGroupDatabaseImpl(cw1Postcode, "A Street", 10, new Point(10, 10), "%s");
        map.put(PointExtensions.getLocationString(cw1StreetA.getPoint()), cw1StreetA);
        cw1StreetB = createDwellingGroupDatabaseImpl(cw1Postcode, "B Street", 10, new Point(20, 20), "%s");
        map.put(PointExtensions.getLocationString(cw1StreetB.getPoint()), cw1StreetB);
        map = new HashMap<String, DwellingGroupDatabaseImpl>();
        cw2Postcode = createPostcodeDatumDatabaseImpl("CW2 1AA", new Point(35, 35), map);
        cw2StreetC = createDwellingGroupDatabaseImpl(cw2Postcode, "C Street", 10, new Point(30, 30), "%s");
        map.put(PointExtensions.getLocationString(cw2StreetC.getPoint()), cw2StreetC);
        cw2StreetD = createDwellingGroupDatabaseImpl(cw2Postcode, "D Street", 10, new Point(40, 40), "%s");
        map.put(PointExtensions.getLocationString(cw2StreetD.getPoint()), cw2StreetD);
        map = new HashMap<String, DwellingGroupDatabaseImpl>();
        cwPostcode = createPostcodeDatumDatabaseImpl("CW3 1AA", new Point(55, 55),  map);
        cwStreetE = createDwellingGroupDatabaseImpl(cwPostcode, "E Street", 10, new Point(50, 50), "%s");
        map.put(PointExtensions.getLocationString(cwStreetE.getPoint()), cwStreetE);
        cwStreetF = createDwellingGroupDatabaseImpl(cwPostcode, "F Street", 10, new Point(60, 60), "%s");
        map.put(PointExtensions.getLocationString(cwStreetF.getPoint()), cwStreetF);
        postalDistrictCW1Routes.addDwellingGroup(cw1StreetA, false);
        postalDistrictCW1Routes.addDwellingGroup(cw1StreetB, false);
        postalDistrictCW2Routes.addDwellingGroup(cw2StreetC, false);
        postalDistrictCW2Routes.addDwellingGroup(cw2StreetD, false);
        councilWardRoutes.addDwellingGroup(cwStreetE, false);
        councilWardRoutes.addDwellingGroup(cwStreetF, false);
    }

    private PostcodeDatumDatabaseImpl createPostcodeDatumDatabaseImpl(
            String postcode, Point point, Map<String, DwellingGroupDatabaseImpl> map) {
        PostcodeDatumDatabaseImpl postcodeDatumDatabase = new PostcodeDatumDatabaseImpl(
                new Postcode(postcode, 10, point.x, point.y), map);
        return postcodeDatumDatabase;
    }

    private DwellingGroupDatabaseImpl createDwellingGroupDatabaseImpl(PostcodeDatumDatabaseImpl postcodeImpl,
                                                                      String name, int houses, Point point,
                                                                      String format) {
        Map<DwellingDatabaseImpl, Dwelling> map = new HashMap<DwellingDatabaseImpl, Dwelling>();
        for (int i = 1; i <= houses; i++) {
            map.put(new DwellingDatabaseImpl('A', String.format(format, i + ""), point), null);
        }
        DwellingGroupDatabaseImpl dwellingGroup = new DwellingGroupDatabaseImpl(postcodeImpl, map, name, point);
        return dwellingGroup;
    }

    @Test
    public void toXmlTest() {
        try {
            route1 = postalDistrictCW1Routes.createRoute("Route 1");
            route2 = postalDistrictCW2Routes.createRoute("Route 2");
            route3 = councilWardRoutes.createRoute("Route 3");
            route1.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cw1StreetA}));
            route2.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cw2StreetC}));
            route3.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cwStreetE}));
            String expected = FileUtils.fileRead(FileUtils.toFile(
                    DefaultRoutableAreaTest.class.getResource(
                            "/uk/co/epii/conservatives/fredericknorth/routes/DefaultRoutableAreaTest1.xml")));
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            }
            catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            Document document = documentBuilder.newDocument();
            Element routableAreasElt = councilWardRoutes.toXml(document);
            document.appendChild(routableAreasElt);
            String result = new XMLSerializerImpl().toString(document);
            assertEquals(expected, result);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test
    public void loadTest() {
        councilWardRoutes.load(new XMLSerializerImpl().fromFile(FileUtils.toFile(
                DefaultRoutableAreaTest.class.getResource(
                        "/uk/co/epii/conservatives/fredericknorth/routes/DefaultRoutableAreaTest1.xml"))).
                getDocumentElement());
        assertEquals(3, councilWardRoutes.getRouteCount());
        assertEquals(3, councilWardRoutes.getRoutedDwellingGroups().size());
        assertEquals(3, councilWardRoutes.getUnroutedDwellingGroups().size());
        assertEquals(1, postalDistrictCW1Routes.getRouteCount());
        assertEquals(1, postalDistrictCW1Routes.getRoutedDwellingGroups().size());
        assertEquals(1, postalDistrictCW1Routes.getUnroutedDwellingGroups().size());
        assertEquals(1, postalDistrictCW2Routes.getRouteCount());
        assertEquals(1, postalDistrictCW2Routes.getRoutedDwellingGroups().size());
        assertEquals(1, postalDistrictCW2Routes.getUnroutedDwellingGroups().size());

    }

}
