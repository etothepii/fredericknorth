package uk.co.epii.conservatives.fredericknorth.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 22:56
 */
public class OSMapLoaderImpl implements OSMapLoader {

    private static final Logger LOG = LoggerFactory.getLogger(OSMapLoaderImpl.class);
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(OSMapLoaderImpl.class.getName().concat("_sync"));
    private static final Logger LOG_FILES = LoggerFactory.getLogger(OSMapLoaderImpl.class.getName().concat("_files"));

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
    public BufferedImage loadMap(OSMap map, Dimension targetSize, ProgressTracker progressTracker) {
        if (map instanceof SeaMapImpl) {
            return getDummyImage(map);
        }
        File file = getMapFile(map);
        LOG_FILES.debug("Loading ... {} => {} at {} x {}", new Object[] {
                map.getMapName(), file, targetSize.width, targetSize.height
        });
        if (file != null) {
            return readFile(file, targetSize, progressTracker);
        }
        return getDummyImage(map);
    }

    private BufferedImage readFile(File file, Dimension targetSize, ProgressTracker progressTracker) {
        ImageReader imageReader = null;
        ImageInputStream iis = null;
        FileInputStream fin = null;
        try {
            LOG_SYNC.debug("Waiting to receive an imageReader");
            synchronized (configuration) {
                imageReader = ImageIO.getImageReadersBySuffix("tif").next();
            }
            LOG_SYNC.debug("Received an imageReader");
            try {

                fin = new FileInputStream(file);
                iis = ImageIO.createImageInputStream(fin);
                imageReader.setInput(iis, false);
                int sourceXSubSampling = targetSize == null ?
                        1 : Math.max(1, imageReader.getWidth(0) / targetSize.width);
                int sourceYSubSampling = targetSize == null ?
                        1 : Math.max(1, imageReader.getHeight(0) / targetSize.height);
                ImageReadParam subSamplingParam = new ImageReadParam();
                subSamplingParam.setSourceSubsampling(sourceXSubSampling, sourceYSubSampling, 0, 0);
                return imageReader.read(0, subSamplingParam);
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                if (fin != null) {
                    try {
                        fin.close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (iis != null) {
                    try {
                        iis.flush();
                        iis.close();
                    }
                    catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }
            }
        }
        finally {
            if (imageReader != null) {
                imageReader.dispose();
                LOG_SYNC.debug("Returned an imageReader");
            }
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
        return bufferedImage;
    }

    static Color getSeaColor(OSMapType osMapType) {
        switch (osMapType) {
            case STREET_VIEW:
                return new Color(230, 246 ,255);
            case VECTOR_MAP:
                return new Color(213, 244, 248);
            case RASTER:
                return new Color(228, 240, 254);
            case MINI:
                return new Color(195, 230, 250);
        }
        return null;
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
            BufferedImage compatibleImage;
            synchronized (configuration) {
                compatibleImage = configuration.createCompatibleImage(image.getWidth(),
                        image.getHeight());
            }
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
