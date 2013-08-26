package uk.co.epii.conservatives.fredericknorth.maps;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 23:06
 */
public class MapViewGeneratorRegistrar {

    public static void registerToContext(ApplicationContext applicationContext, ProgressTracker progressTracker) {
        applicationContext.registerDefaultInstance(MapViewGenerator.class, new MapViewGeneratorImpl(
                applicationContext.getDefaultInstance(OSMapLoader.class),
                applicationContext.getDefaultInstance(OSMapLocator.class),
                applicationContext.getDefaultInstance(LocationFactory.class),
                applicationContext.getDefaultInstance(MapLabelFactory.class),
                progressTracker));
    }

    public static void registerNamedToContext(ApplicationContext applicationContext, String name,
                                              ProgressTracker progressTracker) {
        applicationContext.registerNamedInstance(MapViewGenerator.class, name, new MapViewGeneratorImpl(
                applicationContext.getDefaultInstance(OSMapLoader.class),
                applicationContext.getDefaultInstance(OSMapLocator.class),
                applicationContext.getDefaultInstance(LocationFactory.class),
                applicationContext.getDefaultInstance(MapLabelFactory.class),
                progressTracker));
    }

}
