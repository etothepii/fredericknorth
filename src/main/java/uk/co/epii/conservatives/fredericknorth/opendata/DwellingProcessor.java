package uk.co.epii.conservatives.fredericknorth.opendata;

import java.util.Collection;

/**
 * User: James Robinson
 * Date: 22/06/2013
 * Time: 16:47
 */
public interface DwellingProcessor {

    public Collection<? extends DwellingGroup> getDwellingGroups(String postcode);
    public DwellingGroup getDwellingGroup(String postcode, String dwellingGroupName);
    public Collection<String> getDwellingGroups();
}
