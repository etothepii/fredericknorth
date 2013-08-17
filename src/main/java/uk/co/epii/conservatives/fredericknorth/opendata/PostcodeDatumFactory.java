package uk.co.epii.conservatives.fredericknorth.opendata;

import java.util.Collection;
import java.util.HashMap;

/**
 * User: James Robinson
 * Date: 01/07/2013
 * Time: 08:16
 */
public interface PostcodeDatumFactory {

    public PostcodeDatum getInstance(String postcode);
    public Collection<? extends PostcodeDatum> getPostcodes();
    public String[] getRequiredPostcodePrefices();
}
