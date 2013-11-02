package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.util.Collection;

/**
 * User: James Robinson
 * Date: 22/06/2013
 * Time: 16:47
 */
public interface DwellingProcessor {

    public Collection<? extends DwellingGroup> getDwellingGroups(String postcode);
    public DwellingGroup getDwellingGroup(String postcode, String dwellingGroupName);
    public DwellingGroup load(String postcode, String dwellingGroupName, ApplicationContext applicationContext, Element dwellingGroupElt);
    public Collection<String> getDwellingGroups();
}
