package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 00:50
 */
public class LocationFactoryRegistrar {

    private static final String MinimumMapPaddingKey = "MinimumMapPadding";

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(LocationFactory.class,
                new LocationFactoryImpl(Integer.parseInt(applicationContext.getProperty(MinimumMapPaddingKey))));
    }

}
