package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.CancellationToken;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: James Robinson
 * Date: 08/12/2013
 * Time: 01:09
 */
public class DummyOSMapLoader implements OSMapLoader {
    @Override
    public BufferedImage loadMap(OSMap map, Dimension targetSize, ProgressTracker progressTracker,
                                 int incrementsForImageLoad, CancellationToken cancellationToken) {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
}
