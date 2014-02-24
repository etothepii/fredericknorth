package uk.co.epii.conservatives.fredericknorth.opendata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: James Robinson
 * Date: 24/02/2014
 * Time: 20:36
 */
public interface NumericIdentifierSummary {

    public void add(int dwellingNumber);
    public String summarize(String finalConcatination);

}
