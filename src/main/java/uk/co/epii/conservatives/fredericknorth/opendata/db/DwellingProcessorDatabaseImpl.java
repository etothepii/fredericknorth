package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessor;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.stubs.StubDwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.util.Group;
import uk.co.epii.spencerperceval.util.Grouper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:44
 */
public class DwellingProcessorDatabaseImpl implements DwellingProcessor {

    private PostcodeDatumFactory postcodeDatumFactory;

    public void setPostcodeDatumFactory(PostcodeDatumFactory postcodeDatumFactory) {
        this.postcodeDatumFactory = postcodeDatumFactory;
    }

    @Override
    public Collection<? extends DwellingGroup> getDwellingGroups(String postcode) {
        return null;
    }

    @Override
    public DwellingGroup getDwellingGroup(String postcode, String dwellingGroupName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DwellingGroup load(String postcode, String dwellingGroupName, ApplicationContext applicationContext, Element dwellingGroupElt) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public Collection<String> getDwellingGroups() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
