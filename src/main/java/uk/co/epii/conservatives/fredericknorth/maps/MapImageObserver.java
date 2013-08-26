package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.maps.MapImage;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 25/08/2013
 * Time: 20:33
 */
public interface MapImageObserver {

    public void imageUpdated(MapImage mapImage, Rectangle update, boolean completed);

}
