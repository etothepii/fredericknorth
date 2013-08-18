package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 23:13
 */
public class OSMapLocatorRegistrar {

    private static final String LargeSquareLocatorKey = "GridSquareReferences";
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
        applicationContext.registerDefaultInstance(OSMapLocator.class, new OSMapLocatorImpl(
                applicationContext.getProperty(LargeSquareLocatorKey), mapDimensions));
    }

}
