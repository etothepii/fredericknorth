package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 24/08/2013
 * Time: 18:44
 */
public class DimensionExtensions {

    public static Dimension scale(Dimension dimension, double scale) {
        return new Dimension((int)(dimension.width * scale), (int)(dimension.height * scale));
    }

}
