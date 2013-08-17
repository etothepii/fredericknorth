package uk.co.epii.conservatives.fredericknorth.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.LocaleServiceProviderPool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 22:56
 */
public class OSMapLoaderImpl implements OSMapLoader {

    private static final Logger LOG = LoggerFactory.getLogger(OSMapLoaderImpl.class);

    private String mapImagesRoot;
    private String mapImagesExtention;

    public OSMapLoaderImpl(String mapImagesRoot, String mapImagesExtention) {
        this.mapImagesRoot = mapImagesRoot;
        this.mapImagesExtention = mapImagesExtention;
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
        stringBuilder.append("OS Street View ");
        stringBuilder.append(map.getLargeSquare().toUpperCase());
        stringBuilder.append(File.separator);
        stringBuilder.append("data");
        stringBuilder.append(File.separator);
        stringBuilder.append(map.getLargeSquare());
        stringBuilder.append(File.separator);
        stringBuilder.append(map.getMapName());
        stringBuilder.append(".");
        stringBuilder.append(mapImagesExtention);
        return new File(stringBuilder.toString());
    }
}
