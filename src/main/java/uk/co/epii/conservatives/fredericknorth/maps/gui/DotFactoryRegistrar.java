package uk.co.epii.conservatives.fredericknorth.maps.gui;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 01:07
 */

public class DotFactoryRegistrar {

    private static final String DotRadiusKey = "DotRadius";
    private static final String DotBorderRadiusAdditionKey = "DotBorderRadiusAddition";

    public static void registerToContext(ApplicationContext applicationContext) {
        int dotRadius = Integer.parseInt(applicationContext.getProperty(DotRadiusKey));
        int dotBorderRadiusAddition = Integer.parseInt(applicationContext.getProperty(DotBorderRadiusAdditionKey));
        applicationContext.registerDefaultInstance(DotFactory.class, new DotFactoryImpl(dotRadius, dotBorderRadiusAddition));
    }
}
