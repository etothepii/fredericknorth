package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 19:25
 */
public class BoundedAreaExtensions {

    public static ListCellRenderer getListCellRenderer() {
        return new BoundedAreaCellRenderer();
    }

    public static BoundedAreaComboBoxModel getComboBoxModel(BoundedAreaType boundedAreaType, BoundedArea parent) {
        return new BoundedAreaComboBoxModelImpl(boundedAreaType, parent);
    }

    public static OverlayRenderer<BoundedArea> getOverlayRenderer(Map<BoundedAreaType, Color> colors,
                                                                  int onBoundedAreaEdgeRadius) {
        return new BoundedAreaOverlayRenderer(colors, onBoundedAreaEdgeRadius);
    }

    public static OverlayRenderer<BoundedAreaConstructor> getConstructorOverlayRenderer(
            Map<BoundedAreaType, Color> color, int onBoundedAreaEdgeRadius) {
        return new BoundedAreaConstructorOverlayRenderer(color, onBoundedAreaEdgeRadius);
    }

}
