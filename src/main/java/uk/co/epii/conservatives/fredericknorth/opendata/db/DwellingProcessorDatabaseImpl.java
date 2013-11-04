package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessor;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.util.Collection;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:44
 */
public class DwellingProcessorDatabaseImpl implements DwellingProcessor {

    private PostcodeDatumFactoryDatabaseImpl postcodeDatumFactory;

    public void setPostcodeDatumFactory(PostcodeDatumFactoryDatabaseImpl postcodeDatumFactory) {
        this.postcodeDatumFactory = postcodeDatumFactory;
    }

    @Override
    public Collection<? extends DwellingGroup> getDwellingGroups(String postcode) {
        return postcodeDatumFactory.getInstance(postcode).getDwellingGroups().values();
    }

    @Override
    public DwellingGroup getDwellingGroup(String postcode, String dwellingGroupName) {
        return postcodeDatumFactory.getInstance(postcode).getDwellingGroups().get(dwellingGroupName);
    }

    @Override
    public DwellingGroup load(String postcode, String dwellingGroupName, ApplicationContext applicationContext, Element dwellingGroupElt) {
        throw new UnsupportedOperationException("This method is not supported");
    }

}
