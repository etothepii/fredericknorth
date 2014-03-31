package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.stubs.StubDwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.tuple.Single;
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

    private static final Logger LOG = Logger.getLogger(PostcodeDatumFactory.class);

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
        Map<Dwelling, Single<StubDwelling>> commonNames = getCommonNames(groupedByLocation);
        Map<String, DwellingGroupDatabaseImpl> dwellingGroupsMap = new HashMap<String, DwellingGroupDatabaseImpl>();
        PostcodeDatumDatabaseImpl postcodeImpl = new PostcodeDatumDatabaseImpl(postcode, dwellingGroupsMap);
        for (Map.Entry<Point, List<Duple<Dwelling, BLPU>>> group : groupedByLocation.entrySet()) {
            for (Map.Entry<StubDwelling, List<Duple<Dwelling, BLPU>>> nameGroup : groupByName(commonNames, group).entrySet()) {
                StubDwelling dwellingGroupName = getDwellingGroupName(nameGroup);
                DwellingGroupDatabaseImpl dwellingGroup =
                        getDwellingGroup(postcode, postcodeImpl, group, nameGroup, dwellingGroupName);
                if (dwellingGroupsMap.containsKey(dwellingGroup.getName()))  {
                    throw new IllegalStateException("One can not simply replace a dwelling group with another of the " +
                            "same name, all must be uniquely named: " + dwellingGroup.getKey());
                }
                dwellingGroupsMap.put(dwellingGroup.getKey(), dwellingGroup);
            }
        }
        loaded.put(postcode.getPostcode(), postcodeImpl);
        return postcodeImpl;
    }

    private StubDwelling getDwellingGroupName(Map.Entry<StubDwelling, List<Duple<Dwelling, BLPU>>> nameGroup) {
        StubDwelling common = nameGroup.getKey();
        if (nameGroup.getValue().size() == 1) {
            String[] address = common.getAddress();
            address = Arrays.copyOf(address, address.length);
            address[0] = null;
            common = new StubDwelling(address);
        }
        return common;
    }

    private DwellingGroupDatabaseImpl getDwellingGroup(Postcode postcode, PostcodeDatumDatabaseImpl postcodeImpl,
                                                       Map.Entry<Point, List<Duple<Dwelling, BLPU>>> group,
                                                       Map.Entry<StubDwelling, List<Duple<Dwelling, BLPU>>> nameGroup,
                                                       StubDwelling common) {
        Map<DwellingDatabaseImpl, Dwelling> dwellingGroupDatabaseImplMap = new HashMap<DwellingDatabaseImpl, Dwelling>();
        for (Duple<Dwelling, BLPU> duple : nameGroup.getValue()) {
            Dwelling dwelling = duple.getFirst();
            BLPU blpu = duple.getSecond();
            char councilTaxBand = dwelling.getCouncilTaxBand();
            Point point =
                    PointExtensions.fromFloat(
                            blpu == null ?
                                    new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate()) :
                                    new Point2D.Float(blpu.getXCoordinate(), blpu.getYCoordinate()));
            String name = common.getDifference(new StubDwelling(duple.getFirst()));
            dwellingGroupDatabaseImplMap.put(new DwellingDatabaseImpl(councilTaxBand, name, point), dwelling);
        }
        return new DwellingGroupDatabaseImpl(postcodeImpl, dwellingGroupDatabaseImplMap, common.toString(),
                group.getKey());
    }

    private Map<StubDwelling, List<Duple<Dwelling, BLPU>>> groupByName(
            Map<Dwelling, Single<StubDwelling>> commonNames, Map.Entry<Point, List<Duple<Dwelling, BLPU>>> group) {
        Map<StubDwelling, List<Duple<Dwelling, BLPU>>> groupedByName =
                new HashMap<StubDwelling, List<Duple<Dwelling, BLPU>>>();
        for (Duple<Dwelling, BLPU> duple : group.getValue()) {
            StubDwelling stubDwelling = commonNames.get(duple.getFirst()).getFirst();
            List<Duple<Dwelling, BLPU>> sameNamedDwellings = groupedByName.get(stubDwelling);
            if (sameNamedDwellings == null) {
                sameNamedDwellings = new ArrayList<Duple<Dwelling, BLPU>>();
                groupedByName.put(stubDwelling, sameNamedDwellings);
            }
            sameNamedDwellings.add(duple);
        }
        return groupedByName;
    }

    private Map<StubDwelling, Set<Dwelling>> getStubDwellingsSetMap(Map<Dwelling, Single<StubDwelling>> commonNames) {
        Map<StubDwelling, Set<Dwelling>> stubDwellingSetMap = new HashMap<StubDwelling, Set<Dwelling>>();
        for (Map.Entry<Dwelling, Single<StubDwelling>> entry : commonNames.entrySet()) {
            Set<Dwelling> dwellings = stubDwellingSetMap.get(entry.getValue().getFirst());
            if (dwellings == null) {
                dwellings = new HashSet<Dwelling>();
                stubDwellingSetMap.put(entry.getValue().getFirst(), dwellings);
            }
            dwellings.add(entry.getKey());
        }
        return stubDwellingSetMap;
    }

    static Map<Dwelling, Single<StubDwelling>> getCommonNames(Map<Point, List<Duple<Dwelling, BLPU>>> groupedByLocation) {
        Map<Dwelling, Single<StubDwelling>> commonNames = new HashMap<Dwelling, Single<StubDwelling>>();
        for (List<Duple<Dwelling, BLPU>> dwellings : groupedByLocation.values()) {
            toNextDwelling: for (Duple<Dwelling, BLPU> duple : dwellings) {
                Dwelling dwelling = duple.getFirst();
                StubDwelling stubDwelling = new StubDwelling(dwelling);
                for (Single<StubDwelling> common : commonNames.values()) {
                    if (common.getFirst().d(stubDwelling) == 1) {
                        common.setFirst(common.getFirst().getCommon(stubDwelling));
                        commonNames.put(dwelling, common);
                        continue toNextDwelling;
                    }
                }
                commonNames.put(dwelling, new Single<StubDwelling>(stubDwelling));
            }
        }
        return commonNames;
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
