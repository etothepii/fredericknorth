package uk.co.epii.conservatives.fredericknorth.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.LocaleServiceProviderPool;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 22:56
 */
public class OSMapLoaderImpl implements OSMapLoader {

    private static final Logger LOG = LoggerFactory.getLogger(OSMapLoaderImpl.class);

    private final Map<OSMapType, String> mapLocationFormatStrings;

    private String mapImagesRoot;

    public OSMapLoaderImpl(String mapImagesRoot, Map<OSMapType, String> mapLocationFormatStrings) {
        this.mapImagesRoot = mapImagesRoot;
        this.mapLocationFormatStrings = mapLocationFormatStrings;
    }

    @Override
    public BufferedImage loadMap(OSMap map) {
        try {
            File file = getMapFile(map);
            LOG.debug("Loading ... {}", file);
            return ImageIO.read(file);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private File getMapFile(OSMap map) {
        StringBuilder stringBuilder = new StringBuilder(255);
        stringBuilder.append(mapImagesRoot);
        stringBuilder.append(getPostRootLocation(map));
        return new File(stringBuilder.toString());
    }

    String getPostRootLocation(OSMap map) {
        return String.format(mapLocationFormatStrings.get(map.getOSMapType()),
                map.getLargeSquare() == null ? null : map.getLargeSquare().toLowerCase(), map.getSquare(),
                map.getQuadrant() == null ? null : map.getQuadrant().toLowerCase(),
                map.getSquareHundredth(), map.getQuadrantHundredth());
    }
}
