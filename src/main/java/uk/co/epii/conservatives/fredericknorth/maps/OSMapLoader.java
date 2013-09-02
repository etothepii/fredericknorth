package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.CancellationToken;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 22:55
 */
public interface OSMapLoader {

    public BufferedImage loadMap(OSMap map, Dimension targetSize, ProgressTracker progressTracker,
                                 int incrementsForImageLoad, CancellationToken cancellationToken);

}
