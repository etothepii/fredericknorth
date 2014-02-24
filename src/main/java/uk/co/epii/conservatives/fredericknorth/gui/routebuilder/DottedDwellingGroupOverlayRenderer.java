package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 12:58
 */
class DottedDwellingGroupOverlayRenderer extends OvalOverlayRenderer<DwellingGroup> {

    private static Color UNSELECTED_COLOUR = Color.BLUE;
    private static Color SELECTED_COLOUR = Color.GREEN;

    private final int[] radii = new int[] {0, 2};
    private final Color[] colours = new Color[] {null, Color.WHITE};

    @Override
    protected Color[] getColours(OverlayItem<DwellingGroup> overlayItem, boolean selected) {
        colours[0] = selected ? SELECTED_COLOUR : UNSELECTED_COLOUR;
        return colours;
    }

    @Override
    protected int[] getRadii(OverlayItem<DwellingGroup> overlayItem, boolean selected) {
        radii[0] = (int)Math.ceil(Math.sqrt(overlayItem.getItem().size()));
        return radii;
    }
}
