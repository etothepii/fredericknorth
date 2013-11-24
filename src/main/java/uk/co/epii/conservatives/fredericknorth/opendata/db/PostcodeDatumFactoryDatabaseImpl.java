package uk.co.epii.conservatives.fredericknorth.opendata.db;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.stubs.StubDwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.util.Grouper;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 03/11/2013
 * Time: 11:36
 */
public class PostcodeDatumFactoryDatabaseImpl implements PostcodeDatumFactory {

    private final Map<String, PostcodeDatumDatabaseImpl> loaded =
            new HashMap<String, PostcodeDatumDatabaseImpl>();
    private final Grouper<StubDwelling> stubDwellingGrouper = new Grouper<StubDwelling>();
    private DatabaseSession databaseSession;

    public void setDatabaseSession(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
    }

    @Override
    public PostcodeDatumDatabaseImpl getInstance(String postcode) {
        PostcodeDatumDatabaseImpl loadedPostcode = loaded.get(postcode);
        if (loadedPostcode != null) {
            return loadedPostcode;
        }
        return create(databaseSession.getPostcode(postcode));
    }

    private PostcodeDatumDatabaseImpl create(Postcode postcode) {
        Map<Point, List<Duple<Dwelling, BLPU>>> groupedByLocation = groupByLocation(
                PointExtensions.fromFloat(new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate())),
                databaseSession.fromPostcode(postcode.getPostcode(), Dwelling.class, BLPU.class, "UPRN", "UPRN"));
        Map<String, DwellingGroupDatabaseImpl> dwellingGroupsMap = new HashMap<String, DwellingGroupDatabaseImpl>();
        for (Map.Entry<Point, List<Duple<Dwelling, BLPU>>> group : groupedByLocation.entrySet()) {
            StubDwelling common = null;
            Map<StubDwelling, Duple<Dwelling, BLPU>> map = new HashMap<StubDwelling, Duple<Dwelling, BLPU>>();
            for (Duple<Dwelling, BLPU> duple : group.getValue()) {
                StubDwelling stubDwelling = new StubDwelling(duple.getFirst());
                map.put(stubDwelling, duple);
                common = common == null ? stubDwelling : common.getCommon(stubDwelling);
            }
            Map<DwellingDatabaseImpl, Dwelling> dwellingGroupDatabaseImplMap = new HashMap<DwellingDatabaseImpl, Dwelling>();
            for (StubDwelling stubDwelling : map.keySet()) {
                Duple<Dwelling, BLPU> duple = map.get(stubDwelling);
                Dwelling dwelling = duple.getFirst();
                BLPU blpu = duple.getSecond();
                char councilTaxBand = dwelling.getCouncilTaxBand();
                Point point =
                        PointExtensions.fromFloat(
                                blpu == null ?
                                        new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate()) :
                                        new Point2D.Float(blpu.getXCoordinate(), blpu.getYCoordinate()));
                String name = common.getDifference(stubDwelling);
                dwellingGroupDatabaseImplMap.put(new DwellingDatabaseImpl(councilTaxBand, name, point), dwelling);
            }
            DwellingGroupDatabaseImpl dwellingGroup =
                    new DwellingGroupDatabaseImpl(dwellingGroupDatabaseImplMap, common.toString(), group.getKey());
            if (dwellingGroupsMap.containsKey(dwellingGroup.getName()))  {
                throw new IllegalStateException("One can not simply replace a dwelling group with another of the " +
                        "same name, all must be uniquely named: " + dwellingGroup.getName());
            }
            dwellingGroupsMap.put(PointExtensions.getLocationString(dwellingGroup.getPoint()), dwellingGroup);
        }
        PostcodeDatumDatabaseImpl postcodeImpl = new PostcodeDatumDatabaseImpl(postcode, dwellingGroupsMap);
        loaded.put(postcode.getPostcode(), postcodeImpl);
        return postcodeImpl;
    }

    private Map<Point, List<Duple<Dwelling, BLPU>>> groupByLocation(Point postcodePoint, List<Duple<Dwelling, BLPU>> dwellings) {
        Map<Point, List<Duple<Dwelling, BLPU>>> map = new HashMap<Point, List<Duple<Dwelling, BLPU>>>();
        for (Duple<Dwelling, BLPU> dwelling : dwellings) {
            Point point = dwelling.getSecond() == null ? postcodePoint : PointExtensions.fromFloat(new Point2D.Float(
                    dwelling.getSecond().getXCoordinate(), dwelling.getSecond().getYCoordinate()
            ));
            List<Duple<Dwelling, BLPU>> list = map.get(point);
            if (list == null) {
                list = new ArrayList<Duple<Dwelling, BLPU>>();
                map.put(point, list);
            }
            list.add(dwelling);
        }
        return map;
    }

    Point calculateMedian(List<Integer> xCoords, List<Integer> yCoords) {
        Collections.sort(xCoords);
        Collections.sort(yCoords);
        return xCoords.size() % 2 == 1 ?
                new Point(xCoords.get(xCoords.size() / 2), yCoords.get(yCoords.size() / 2)) :
                new Point((xCoords.get(xCoords.size() / 2) + xCoords.get(xCoords.size() / 2 - 1)) / 2,
                          (yCoords.get(yCoords.size() / 2) + yCoords.get(yCoords.size() / 2 - 1)) / 2);
    }

    @Override
    public Collection<? extends PostcodeDatum> getPostcodes(Rectangle bounds) {
        Collection<Postcode> postcodes = databaseSession.getPostcodesWithin(bounds);
        List<PostcodeDatum> postcodeDatums = new ArrayList<PostcodeDatum>(postcodes.size());
        for (Postcode postcode : postcodes) {
            postcodeDatums.add(getInstance(postcode.getPostcode()));
        }
        return postcodeDatums;
    }

}
