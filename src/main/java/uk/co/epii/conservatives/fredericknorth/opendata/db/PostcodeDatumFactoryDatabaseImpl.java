package uk.co.epii.conservatives.fredericknorth.opendata.db;

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
    public PostcodeDatum getInstance(String postcodeString) {
        PostcodeDatumDatabaseImpl loadedPostcode = loaded.get(postcodeString);
        if (loadedPostcode != null) {
            return loadedPostcode;
        }
        List<Duple<Dwelling, BLPU>> dwellings =
                databaseSession.fromPostcode(postcodeString, Dwelling.class, BLPU.class, "UPRN", "UPRN");
        Postcode postcode = databaseSession.getPostcode(postcodeString);
        Map<StubDwelling, Duple<Dwelling, BLPU>> map =
                new HashMap<StubDwelling, Duple<Dwelling, BLPU>>();
        for (Duple<Dwelling, BLPU> duple : dwellings) {
            map.put(new StubDwelling(duple.getFirst()), duple);
        }
        List<Group<StubDwelling>> groupedDwellings = stubDwellingGrouper.group(map.keySet());
        for (Group<StubDwelling> group : groupedDwellings) {
            DwellingGroupDatabaseImpl dwellingGroup = new DwellingGroupDatabaseImpl();
        }
    }

    @Override
    public Collection<? extends PostcodeDatum> getPostcodes(Rectangle bounds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
