package uk.co.epii.conservatives.fredericknorth.dummydata;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.boundaryline.DefaultBoundedArea;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingGroupDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.db.PostcodeDatumDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.routes.DefaultRoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 15/12/2013
 * Time: 19:25
 */
public class TestCouncilWard {

    public static BoundedArea councilWard;
    public static BoundedArea postalDistrictCW1;
    public static BoundedArea postalDistrictCW2;
    public static DefaultRoutableArea councilWardRoutes;
    public static DefaultRoutableArea postalDistrictCW1Routes;
    public static DefaultRoutableArea postalDistrictCW2Routes;
    public static DwellingGroupDatabaseImpl cw1StreetA;
    public static DwellingGroupDatabaseImpl cw1StreetB;
    public static DwellingGroupDatabaseImpl cw2StreetC;
    public static DwellingGroupDatabaseImpl cw2StreetD;
    public static DwellingGroupDatabaseImpl cwStreetE;
    public static DwellingGroupDatabaseImpl cwStreetF;
    public static PostcodeDatumDatabaseImpl cw1Postcode;
    public static PostcodeDatumDatabaseImpl cw2Postcode;
    public static PostcodeDatumDatabaseImpl cwPostcode;

    public static void reset() {
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
         cw1Postcode = createPostcodeDatumDatabaseImpl("CW1 1AA", new Point(535267, 181084), map);
         cw1StreetA = createDwellingGroupDatabaseImpl(cw1Postcode, "A Street", 10, new Point(535265, 181086), "%s");
         map.put(PointExtensions.getLocationString(cw1StreetA.getPoint()), cw1StreetA);
         cw1StreetB = createDwellingGroupDatabaseImpl(cw1Postcode, "B Street", 10, new Point(535269, 181082), "%s");
         map.put(PointExtensions.getLocationString(cw1StreetB.getPoint()), cw1StreetB);
         map = new HashMap<String, DwellingGroupDatabaseImpl>();
         cw2Postcode = createPostcodeDatumDatabaseImpl("CW2 1AA", new Point(535043, 181123), map);
         cw2StreetC = createDwellingGroupDatabaseImpl(cw2Postcode, "C Street", 10, new Point(535041, 181125), "%s");
         map.put(PointExtensions.getLocationString(cw2StreetC.getPoint()), cw2StreetC);
         cw2StreetD = createDwellingGroupDatabaseImpl(cw2Postcode, "D Street", 10, new Point(535045, 181121), "%s");
         map.put(PointExtensions.getLocationString(cw2StreetD.getPoint()), cw2StreetD);
         map = new HashMap<String, DwellingGroupDatabaseImpl>();
         cwPostcode = createPostcodeDatumDatabaseImpl("CW3 1AA", new Point(535238, 180880),  map);
         cwStreetE = createDwellingGroupDatabaseImpl(cwPostcode, "E Street", 10, new Point(535228, 180890), "%s");
         map.put(PointExtensions.getLocationString(cwStreetE.getPoint()), cwStreetE);
         cwStreetF = createDwellingGroupDatabaseImpl(cwPostcode, "F Street", 10, new Point(535248, 180870), "%s");
         map.put(PointExtensions.getLocationString(cwStreetF.getPoint()), cwStreetF);
         postalDistrictCW1Routes.addDwellingGroup(cw1StreetA, false);
         postalDistrictCW1Routes.addDwellingGroup(cw1StreetB, false);
         postalDistrictCW2Routes.addDwellingGroup(cw2StreetC, false);
         postalDistrictCW2Routes.addDwellingGroup(cw2StreetD, false);
         councilWardRoutes.addDwellingGroup(cwStreetE, false);
         councilWardRoutes.addDwellingGroup(cwStreetF, false);
    }

    public static void initiateRoutes() {
        Route route1 = postalDistrictCW1Routes.createRoute("Route 1");
        Route route2 = postalDistrictCW2Routes.createRoute("Route 2");
        Route route3 = councilWardRoutes.createRoute("Route 3");
        route1.addDwellingGroups(Arrays.asList(new DwellingGroup[]{cw1StreetA}));
        route2.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cw2StreetC}));
        route3.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cwStreetE}));
    }

    private static PostcodeDatumDatabaseImpl createPostcodeDatumDatabaseImpl(
            String postcode, Point point, Map<String, DwellingGroupDatabaseImpl> map) {
        PostcodeDatumDatabaseImpl postcodeDatumDatabase = new PostcodeDatumDatabaseImpl(
                new Postcode(postcode, 10, point.x, point.y), map);
        return postcodeDatumDatabase;
    }

    private static DwellingGroupDatabaseImpl createDwellingGroupDatabaseImpl(PostcodeDatumDatabaseImpl postcodeImpl,
                                                                      String name, int houses, Point point,
                                                                      String format) {
        Map<DwellingDatabaseImpl, Dwelling> map = new HashMap<DwellingDatabaseImpl, Dwelling>();
        for (int i = 1; i <= houses; i++) {
            map.put(new DwellingDatabaseImpl('A', String.format(format, i + ""), point), null);
        }
        DwellingGroupDatabaseImpl dwellingGroup = new DwellingGroupDatabaseImpl(postcodeImpl, map, name, point);
        return dwellingGroup;
    }

}
