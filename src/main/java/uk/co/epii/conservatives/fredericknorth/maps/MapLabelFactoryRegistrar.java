package uk.co.epii.conservatives.fredericknorth.maps;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 23:57
 */
public class MapLabelFactoryRegistrar {

    private static final String MapLabelPaddingKey = "MapLabelPadding";
    private static final String LabelFontKey = "LabelFont";
    private static final String MapDotRadiusKey = "MapDotRadius";
    private static final String LabelFontSizeKey = "LabelFontSize";

    public static void registerToContext(ApplicationContext applicationContext) {
        int padding = Integer.parseInt(applicationContext.getProperty(MapLabelPaddingKey));
        String labelFontName = applicationContext.getProperty(LabelFontKey);
        int labelFontSize = Integer.parseInt(applicationContext.getProperty(LabelFontSizeKey));
        Font labelFont = new Font(labelFontName, Font.PLAIN, labelFontSize);
        int dotRadius = Integer.parseInt(applicationContext.getProperty(MapDotRadiusKey));
        applicationContext.registerDefaultInstance(MapLabelFactory.class, new MapLabelFactoryImpl(labelFont, padding, dotRadius));
    }

}
