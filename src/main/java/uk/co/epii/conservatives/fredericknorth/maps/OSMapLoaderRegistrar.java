package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.io.File;
import java.util.EnumMap;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 23:01
 */
public class OSMapLoaderRegistrar {

    private static final String MAP_IMAGE_DIRECTORY_KEY = "MapsDirectory";
    private static final String FileFormatKeyFormat = "FormatFor%sMapFile";

    public static void registerToContext(ApplicationContext applicationContext) {
        String dataFolder = applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER).toString();
        EnumMap<OSMapType, String> mapLocationFormatStrings = new EnumMap<OSMapType, String>(OSMapType.class);
        for(OSMapType osMapType : OSMapType.values()) {
            mapLocationFormatStrings.put(osMapType, applicationContext.getProperty(String.format(FileFormatKeyFormat, osMapType.getName())));
        }
        applicationContext.registerDefaultInstance(OSMapLoader.class,
                new OSMapLoaderImpl(dataFolder + File.separator +
                        applicationContext.getProperty(MAP_IMAGE_DIRECTORY_KEY), mapLocationFormatStrings));
    }

}
