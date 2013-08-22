package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 23:01
 */
public class OSMapLoaderRegistrar {

    private static final String MAP_IMAGE_DIRECTORY_KEY = "MapsDirectory";
    private static final String RemoteMapsURLKey = "RemoteMapsURL";
    private static final String FileFormatKeyFormat = "FormatFor%sMapFile";
    private static final String URLEncodingFormatKey = "URLEncodingFormat";


    private static final String MapTypeWidthKeyFormat = "%sWidth";
    private static final String MapTypeHeightKeyFormat = "%sHeight";

    public static void registerToContext(ApplicationContext applicationContext) {
        Map<OSMapType, Dimension> mapDimensions = new EnumMap<OSMapType, Dimension>(OSMapType.class);
        for (OSMapType osMapType : OSMapType.values()) {
            int width = Integer.parseInt(
                    applicationContext.getProperty(String.format(MapTypeWidthKeyFormat, osMapType.getName())));
            int height = Integer.parseInt(
                    applicationContext.getProperty(String.format(MapTypeHeightKeyFormat, osMapType.getName())));
            mapDimensions.put(osMapType, new Dimension(width, height));
        }
        String dataFolder = applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER).toString();
        EnumMap<OSMapType, String> mapLocationFormatStrings = new EnumMap<OSMapType, String>(OSMapType.class);
        for(OSMapType osMapType : OSMapType.values()) {
            mapLocationFormatStrings.put(osMapType,
                    applicationContext.getProperty(String.format(FileFormatKeyFormat, osMapType.getName())));
        }
        applicationContext.registerDefaultInstance(OSMapLoader.class,
                new OSMapLoaderImpl(dataFolder + File.separator +
                        applicationContext.getProperty(MAP_IMAGE_DIRECTORY_KEY),
                        applicationContext.getProperty(RemoteMapsURLKey), mapLocationFormatStrings, mapDimensions,
                        applicationContext.getProperty(URLEncodingFormatKey)));
    }

}
