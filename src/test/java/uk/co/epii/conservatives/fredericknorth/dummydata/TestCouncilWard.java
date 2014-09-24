package uk.co.epii.conservatives.fredericknorth.dummydata;

import edu.emory.mathcs.backport.java.util.Arrays;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.boundaryline.DefaultBoundedArea;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingGroupDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.routes.DefaultRoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
    public static DwellingGroupDatabaseImpl cw1StreetA2;
    public static DwellingGroupDatabaseImpl cw1StreetB;
    public static DwellingGroupDatabaseImpl cw2StreetC;
    public static DwellingGroupDatabaseImpl cw2StreetD;
    public static DwellingGroupDatabaseImpl cwStreetE;
    public static DwellingGroupDatabaseImpl cwStreetF;

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
        cw1StreetA = createDwellingGroupDatabaseImpl("A Street", 1, 10, new Point(535165, 180986), "%s", "AA1 1AA");
        cw1StreetA2 = createDwellingGroupDatabaseImpl("A Street", 11, 20, new Point(535215, 180936), "%s", "AA1 1AA");
        map.put(PointExtensions.getLocationString(cw1StreetA.getPoint()), cw1StreetA);
        map.put(PointExtensions.getLocationString(cw1StreetA2.getPoint()), cw1StreetA2);
        cw1StreetB = createDwellingGroupDatabaseImpl("B Street", 1, 20, new Point(535369, 181182), "%s", "AA1 1AB");
        map.put(PointExtensions.getLocationString(cw1StreetB.getPoint()), cw1StreetB);
        map = new HashMap<String, DwellingGroupDatabaseImpl>();
        cw2StreetC = createDwellingGroupDatabaseImpl("C Street", 1, 10, new Point(535041, 181125), "%s", "AA1 1AC");
        map.put(PointExtensions.getLocationString(cw2StreetC.getPoint()), cw2StreetC);
        cw2StreetD = createDwellingGroupDatabaseImpl("D Street", 1, 10, new Point(535045, 181121), "%s", "AA1 1AD");
        map.put(PointExtensions.getLocationString(cw2StreetD.getPoint()), cw2StreetD);
        map = new HashMap<String, DwellingGroupDatabaseImpl>();
        cwStreetE = createDwellingGroupDatabaseImpl("E Street", 1, 10, new Point(535228, 180890), "%s", "AA1 1AE");
        map.put(PointExtensions.getLocationString(cwStreetE.getPoint()), cwStreetE);
        cwStreetF = createDwellingGroupDatabaseImpl("F Street", 1, 10, new Point(535248, 180870), "%s", "AA1 1AF");
        map.put(PointExtensions.getLocationString(cwStreetF.getPoint()), cwStreetF);
        postalDistrictCW1Routes.addDwellingGroup(cw1StreetA, false);
        postalDistrictCW1Routes.addDwellingGroup(cw1StreetA2, false);
        postalDistrictCW1Routes.addDwellingGroup(cw1StreetB, false);
        postalDistrictCW2Routes.addDwellingGroup(cw2StreetC, false);
        postalDistrictCW2Routes.addDwellingGroup(cw2StreetD, false);
        councilWardRoutes.addDwellingGroup(cwStreetE, false);
        councilWardRoutes.addDwellingGroup(cwStreetF, false);
    }

    public static void initiateRoutes() {
        Route route1 = postalDistrictCW1Routes.createRoute("Route 1", "df361dca-4aab-4289-b8c8-852be031957e");
        Route route2 = postalDistrictCW2Routes.createRoute("Route 2", "0cb395cd-a11e-4793-b7b0-2dcc772be7b9");
        Route route3 = councilWardRoutes.createRoute("Route 3", "613ea749-af7c-451c-ba5a-eba6c8d4057b");
        route1.addDwellingGroups(Arrays.asList(new DwellingGroup[]{cw1StreetA, cw1StreetA2, cw1StreetB}));
        route2.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cw2StreetC}));
        route3.addDwellingGroups(Arrays.asList(new DwellingGroup[] {cwStreetE}));
    }

    private static DwellingGroupDatabaseImpl createDwellingGroupDatabaseImpl(
                                                                      String name, int housesFrom, int housesTo, Point point,
                                                                      String format, String postcode) {
        List<DwellingDatabaseImpl> dwellings = new ArrayList<DwellingDatabaseImpl>();
        for (int i = housesFrom; i <= housesTo; i++) {
            DeliveryPointAddress deliveryPointAddress = new DeliveryPointAddress();
            deliveryPointAddress.setPostcode(postcode);
            DwellingDatabaseImpl dwelling = new DwellingDatabaseImpl(String.format(format, i + ""), point, deliveryPointAddress);
            dwellings.add(dwelling);
        }
        DwellingGroupDatabaseImpl dwellingGroup = new DwellingGroupDatabaseImpl(dwellings, name, point);
        return dwellingGroup;
    }

}
