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
import uk.co.epii.spencerperceval.util.Group;
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
        List<Duple<Dwelling, BLPU>> dwellings =
                databaseSession.fromPostcode(postcode.getPostcode(), Dwelling.class, BLPU.class, "UPRN", "UPRN");
        Map<StubDwelling, Duple<Dwelling, BLPU>> map =
                new HashMap<StubDwelling, Duple<Dwelling, BLPU>>();
        for (Duple<Dwelling, BLPU> duple : dwellings) {
            map.put(new StubDwelling(duple.getFirst()), duple);
        }
        List<Group<StubDwelling>> groupedDwellings = stubDwellingGrouper.group(map.keySet());
        Map<String, DwellingGroupDatabaseImpl> dwellingGroupsMap = new HashMap<String, DwellingGroupDatabaseImpl>();
        for (Group<StubDwelling> group : groupedDwellings) {
            ArrayList<Integer> xCoords = new ArrayList<Integer>(group.size());
            ArrayList<Integer> yCoords = new ArrayList<Integer>(group.size());
            Map<DwellingDatabaseImpl, Dwelling> dwellingGroupDatabaseImplMap =
                    new HashMap<DwellingDatabaseImpl, Dwelling>();
            for (StubDwelling stubDwelling : group) {
                Duple<Dwelling, BLPU> duple = map.get(stubDwelling);
                Dwelling dwelling = duple.getFirst();
                BLPU blpu = duple.getSecond();
                char councilTaxBand = dwelling.getCouncilTaxBand();
                Point point =
                        PointExtensions.fromFloat(
                                blpu == null ?
                                        new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate()) :
                                        new Point2D.Float(blpu.getXCoordinate(), blpu.getYCoordinate()));
                String name = group.getCommon().getDifference(stubDwelling);
                xCoords.add(point.x);
                yCoords.add(point.y);
                dwellingGroupDatabaseImplMap.put(new DwellingDatabaseImpl(councilTaxBand, name, point), dwelling);
            }
            DwellingGroupDatabaseImpl dwellingGroup =
                    new DwellingGroupDatabaseImpl(dwellingGroupDatabaseImplMap, group.getCommon().toString(),
                            calculateMedian(xCoords, yCoords));
            dwellingGroupsMap.put(dwellingGroup.getName(), dwellingGroup);
        }
        PostcodeDatumDatabaseImpl postcodeImpl = new PostcodeDatumDatabaseImpl(postcode, dwellingGroupsMap);
        loaded.put(postcode.getPostcode(), postcodeImpl);
        return postcodeImpl;
    }

    private Point calculateMedian(ArrayList<Integer> xCoords, ArrayList<Integer> yCoords) {
        Collections.sort(xCoords);
        Collections.sort(yCoords);
        return xCoords.size() % 2 == 1 ?
                new Point(xCoords.get(xCoords.size() / 2), yCoords.get(yCoords.size() / 2)) :
                new Point((xCoords.get(xCoords.size() / 2) + xCoords.get(xCoords.size() / 2 - 1) / 2),
                          (yCoords.get(yCoords.size() / 2) + yCoords.get(yCoords.size() / 2 - 1) / 2));
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
