package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 01:46
 */
public class MeetingPointOverlayRenderer extends OvalOverlayRenderer<MeetingPoint> {

    private static final Color[] UNSELECTED_COLOUR = new Color[] {Color.BLACK};
    private static final Color[] SELECTED_COLOUR = new Color[] {Color.WHITE, Color.RED, Color.WHITE, Color.RED, Color.WHITE, Color.RED, Color.WHITE, Color.RED};
    private static final int[] UNSELECTED_RADII = new int[] {10};
    private static final int[] SELECTED_RADII = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

    @Override
    protected Color[] getColours(OverlayItem<MeetingPoint> overlayItem, boolean selected) {
        return selected ? SELECTED_COLOUR : UNSELECTED_COLOUR;
    }

    @Override
    protected int[] getRadii(OverlayItem<MeetingPoint> overlayItem, boolean selected) {
        return selected ? SELECTED_RADII : UNSELECTED_RADII;
    }
}
