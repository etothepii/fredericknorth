package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 23:13
 */
public class OSMapLocatorRegistrar {

    private static final String LargeSquareLocatorKey = "GridSquareReferences";

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(OSMapLocator.class, new OSMapLocatorImpl(
                applicationContext.getProperty(LargeSquareLocatorKey)));
    }

}
