package uk.co.epii.conservatives.fredericknorth.opendata;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 22/06/2013
 * Time: 16:56
 */
class DwellingProcessorImpl implements DwellingProcessor {

    private static final Logger LOG = Logger.getLogger(DwellingProcessorImpl.class);

    private final HashMap<String, HashMap<String, DwellingGroupImpl>> dwellingGroups;

    DwellingProcessorImpl(HashMap<String, HashMap<String, DwellingGroupImpl>> dwellingGroups) {
        this.dwellingGroups = dwellingGroups;
    }

    @Override
    public Collection<? extends DwellingGroup> getDwellingGroups(String postcode) {
        HashMap<String, DwellingGroupImpl> postcodeDwellingGroups = dwellingGroups.get(postcode);
        if (postcodeDwellingGroups == null) {
            postcodeDwellingGroups = new HashMap<String, DwellingGroupImpl>();
            dwellingGroups.put(postcode, postcodeDwellingGroups);
        }
        return postcodeDwellingGroups.values();
    }

    @Override
    public DwellingGroup getDwellingGroup(String postcode, String dwellingGroupName) {
        Map<String, DwellingGroupImpl> postcodeDwellingGroups = dwellingGroups.get(postcode);
        if (postcodeDwellingGroups == null) return null;
        return postcodeDwellingGroups.get(dwellingGroupName);
    }

    @Override
    public DwellingGroup load(String postcode, String dwellingGroupName, ApplicationContext applicationContext, Element dwellingGroupElt) {
        Map<String, DwellingGroupImpl> postcodeDwellingGroups = dwellingGroups.get(postcode);
        if (postcodeDwellingGroups == null) return null;
        DwellingGroupImpl dwellingGroupImpl = postcodeDwellingGroups.get(dwellingGroupName);
        dwellingGroupImpl.load(applicationContext, dwellingGroupElt);
        return dwellingGroupImpl;
    }

    @Override
    public Collection<String> getDwellingGroups() {
        return dwellingGroups.keySet();
    }
}
