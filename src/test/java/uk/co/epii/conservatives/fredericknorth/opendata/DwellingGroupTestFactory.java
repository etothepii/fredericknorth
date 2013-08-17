package uk.co.epii.conservatives.fredericknorth.opendata;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 17:59
 */
public class DwellingGroupTestFactory {

    public static DwellingGroup getInstance(String name, String displayName, String postcode) {
        return new DwellingGroupImpl(name, displayName, new PostcodeDatumImpl(postcode));
    }

}
