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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
    private final GraphicsConfiguration configuration;

    private String mapImagesRoot;
    private String mapImagesURLRoot;
    private String urlEncodingFormat;


    public OSMapLoaderImpl(String mapImagesRoot, String mapImagesURLRoot,
                           Map<OSMapType, String> mapLocationFormatStrings, Map<OSMapType, Dimension> mapDimensions,
                           String urlEncodingFormat) {
         configuration = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        this.mapImagesRoot = mapImagesRoot;
        this.mapImagesURLRoot = mapImagesURLRoot;
        this.mapLocationFormatStrings = mapLocationFormatStrings;
        this.mapDimensions = mapDimensions;
        this.urlEncodingFormat = urlEncodingFormat;
    }

    @Override
    public BufferedImage loadMap(OSMap map) {
        try {
            if (map instanceof SeaMapImpl) {
                return getDummyImage(map);
            }
            File file = getMapFile(map);
            LOG.debug("Loading ... {}", file);
            if (file != null) {
                BufferedImage image = ImageIO.read(file);
                LOG.debug("Converting to compatible image");
                BufferedImage compatibleImage = configuration.createCompatibleImage(image.getWidth(),
                        image.getHeight());
                LOG.debug("Converted to compatible image");
                Graphics g = compatibleImage.getGraphics();
                LOG.debug("Creating graphics");
                g.drawImage(image, 0, 0, null);
                LOG.debug("Drawing Image");
                g.dispose();
                LOG.debug("Converted to compatible image");
                return compatibleImage;
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
        g.setColor(getSeaColor(map.getOSMapType()));
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.BLACK);
        FontRenderContext frc = g.getFontRenderContext();
        drawCenteredString(g, frc, size, map.getMapName(), 0, 0);
        return bufferedImage;
    }

    private Color getSeaColor(OSMapType osMapType) {
        switch (osMapType) {
            case STREET_VIEW:
                return new Color(231, 247 ,255);
            case VECTOR_MAP:
                return new Color(214, 245, 249);
            case RASTER:
                return new Color(229, 241, 255);
            case MINI:
                return new Color(190, 240, 247);
        }
        return null;
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
        try {
            boolean first = true;
            for (String part : postRootLocation.split("/")) {
                if (first) {
                    first = false;
                }
                else {
                    stringBuilder.append("/");
                }
                stringBuilder.append(URLEncoder.encode(part, urlEncodingFormat).replaceAll("\\+", "%20"));
            }
            LOG.debug("encoded URL: {}", stringBuilder);
        }
        catch (UnsupportedEncodingException uee) {
            LOG.warn(uee.getMessage(), uee);
            return false;
        }
        try {
            URL url = new URL(stringBuilder.toString());
            new File(mapFile.getParent()).mkdirs();
            LOG.debug("Loading from: {}", url.toString());
            BufferedImage image = ImageIO.read(url);
            LOG.debug("Converting to compatible image");
            BufferedImage compatibleImage = configuration.createCompatibleImage(image.getWidth(),
                    image.getHeight());
            LOG.debug("Converted to compatible image");
            Graphics g = compatibleImage.getGraphics();
            LOG.debug("Creating graphics");
            g.drawImage(image, 0, 0, null);
            LOG.debug("Drawing Image");
            g.dispose();
            LOG.debug("Converted to compatible image");
            ImageIO.write(compatibleImage, "tif", mapFile);
            LOG.debug("Saved to: {}", url.toString());
            return true;
        }
        catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
        catch (IOException e) {
            LOG.debug(e.getMessage());
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
