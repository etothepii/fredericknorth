package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * User: James Robinson
 * Date: 01/07/2013
 * Time: 08:16
 */
public interface DwellingGroupFactory {

    public Collection<? extends DwellingGroup> getPostcodes(Rectangle bounds);
}
