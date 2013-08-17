package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.io.File;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 23:01
 */
public class OSMapLoaderRegistrar {

    private static final String MAP_IMAGE_DIRECTORY_KEY = "MapsDirectory";
    private static final String MAP_IMAGE_EXTENTION_KEY = "MapImageExtention";

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(OSMapLoader.class,
                new OSMapLoaderImpl(applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER).toString() + File.separator +
                        applicationContext.getProperty(MAP_IMAGE_DIRECTORY_KEY),
                        applicationContext.getProperty(MAP_IMAGE_EXTENTION_KEY)));
    }

}
