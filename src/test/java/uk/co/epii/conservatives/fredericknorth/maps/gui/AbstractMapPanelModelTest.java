package uk.co.epii.conservatives.fredericknorth.maps.gui;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.boundaryline.AbstractBoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaOverlayItem;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.BoundedAreaExtensions;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 19:39
 */
public class AbstractMapPanelModelTest {

    @Test
    public void canFindInterfaceForRendering() {
        AbstractMapPanelModel abstractMapPanelModel = new AbstractMapPanelModel(null) {
            @Override
            public void doubleClicked(MouseEvent e) {}

            @Override
            public void clicked(MouseEvent e) {}

            @Override
            public void cancel() {}
        };
        HashMap<BoundedAreaType, Color> hashMap = new HashMap<BoundedAreaType, Color>();
        hashMap.put(BoundedAreaType.POLLING_DISTRICT, Color.RED);
        abstractMapPanelModel.setOverlayRenderer(BoundedArea.class, BoundedAreaExtensions.getOverlayRenderer(hashMap));
        BoundedAreaOverlayItem boundedAreaOverlayItem = new BoundedAreaOverlayItem(
                new AbstractBoundedArea(BoundedAreaType.POLLING_DISTRICT, "Blah") {
            @Override
            public BoundedArea[] getChildren() {
                return new BoundedArea[0];
            }

            @Override
            public BoundedAreaType getBoundedAreaType() {
                return BoundedAreaType.POLLING_DISTRICT;
            }
        }, 1);
        assertTrue(abstractMapPanelModel.getOverlayRenderer(boundedAreaOverlayItem.getItem().getClass()) != null);
    }

}
