package uk.co.epii.conservatives.fredericknorth.maps;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.LocaleServiceProviderPool;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private final Map<OSMapType, Dimension> mapDimensions;

    private String mapImagesRoot;
    private String mapImagesURLRoot;

    public OSMapLoaderImpl(String mapImagesRoot, String mapImagesURLRoot,
                           Map<OSMapType, String> mapLocationFormatStrings, Map<OSMapType, Dimension> mapDimensions) {
        this.mapImagesRoot = mapImagesRoot;
        this.mapImagesURLRoot = mapImagesURLRoot;
        this.mapLocationFormatStrings = mapLocationFormatStrings;
        this.mapDimensions = mapDimensions;
    }

    @Override
    public BufferedImage loadMap(OSMap map) {
        try {
            File file = getMapFile(map);
            LOG.debug("Loading ... {}", file);
            if (file != null) {
                return ImageIO.read(file);
            }
            return getDummyImage(map);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private BufferedImage getDummyImage(OSMap map) {
        Dimension size = mapDimensions.get(map.getOSMapType());
        BufferedImage bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        Font font = g.getFont();
        g.setFont(new Font(font.getName(), font.getStyle(), 24));
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, size.width, size.height);
        FontRenderContext frc = g.getFontRenderContext();
        drawCenteredString(g, frc, size, "Unable to locate", 0, -12);
        drawCenteredString(g, frc, size, map.getMapName(), 0, 12);
        return bufferedImage;
    }

    private void drawCenteredString(Graphics g, FontRenderContext frc, Dimension canvasSize,
                                    String string, int xOffset, int yOffset) {
        Rectangle bounds = g.getFont().createGlyphVector(frc, string).getPixelBounds(frc, 0, 0);
        int x = (canvasSize.width - bounds.width) / 2 - bounds.x + xOffset;
        int y = (canvasSize.height - bounds.height) / 2 - bounds.y + yOffset;
        g.drawString(string, x, y);
    }

    private File getMapFile(OSMap map) {
        StringBuilder stringBuilder = new StringBuilder(255);
        stringBuilder.append(mapImagesRoot);
        String postRootLocation = getPostRootLocation(map);
        stringBuilder.append(postRootLocation);
        File mapFile = new File(stringBuilder.toString());
        if (!mapFile.exists()) {
            return loadMapFile(postRootLocation, mapFile) ? mapFile : null;
        }
        return mapFile;
    }

    private boolean loadMapFile(String postRootLocation, File mapFile) {
        StringBuilder stringBuilder;
        if (mapImagesURLRoot == null) {
            return false;
        }
        stringBuilder = new StringBuilder(255);
        stringBuilder.append(mapImagesURLRoot);
        stringBuilder.append(postRootLocation);
        try {
            URL url = new URL(stringBuilder.toString());
            new File(mapFile.getParent()).mkdirs();
            FileUtils.copyURLToFile(url, mapFile);
            return true;
        }
        catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    String getPostRootLocation(OSMap map) {
        return String.format(mapLocationFormatStrings.get(map.getOSMapType()),
                map.getLargeSquare() == null ? null : map.getLargeSquare().toLowerCase(), map.getSquare(),
                map.getQuadrant() == null ? null : map.getQuadrant().toLowerCase(),
                map.getSquareHundredth(), map.getQuadrantHundredth());
    }
}
