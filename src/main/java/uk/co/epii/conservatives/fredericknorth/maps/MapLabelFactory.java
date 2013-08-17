package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:08
 */
public interface MapLabelFactory {

    public List<MapLabel> getMapLabels(Rectangle imageBounds, List<? extends Location> namedPoints, Graphics g);

}
