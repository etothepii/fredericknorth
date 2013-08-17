package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 00:18
 */
public class MapImageFactoryRegistrar {

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(MapImageFactory.class, new MapImageFactoryImpl());
    }

}
