package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.maps.gui.Dot;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.RenderedOverlay;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 13:52
 */
public class DottedDwellingGroupOverlayRendererTest {

    @Test
    public void containsTest() {

        boolean[][] expected = new boolean[][] {
                new boolean[] {false, false, false, false,  true, true, true, true, true,  true, false, false, false, false},
                new boolean[] {false, false, false,  true,  true, true, true, true, true,  true,  true, false, false, false},
                new boolean[] {false, false,  true,  true,  true, true, true, true, true,  true,  true,  true, false, false},
                new boolean[] {false,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true, false},
                new boolean[] { true,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true,  true},
                new boolean[] { true,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true,  true},
                new boolean[] { true,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true,  true},
                new boolean[] { true,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true,  true},
                new boolean[] { true,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true,  true},
                new boolean[] { true,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true,  true},
                new boolean[] {false,  true,  true,  true,  true, true, true, true, true,  true,  true,  true,  true, false},
                new boolean[] {false, false,  true,  true,  true, true, true, true, true,  true,  true,  true, false, false},
                new boolean[] {false, false, false,  true,  true, true, true, true, true,  true,  true, false, false, false},
                new boolean[] {false, false, false, false,  true, true, true, true, true,  true, false, false, false, false}
        };

        MapPanel mapPanel = new MapPanel(new DummyMapPanelModel(), 1.2);
        mapPanel.setSize(100, 100);
        DummyOverlayItem dummyOverlayItem = new DummyOverlayItem<DwellingGroup>(
                new DummyDwellingGroup("A", 25, new Point())
        );
        dummyOverlayItem.setGeoLocationOfCenter(new Point(7,7));
        RenderedOverlay renderedOverlay = new DottedDwellingGroupOverlayRenderer().getOverlayRendererComponent(
                mapPanel,
                dummyOverlayItem,
                new DummyImageAndGeoPointTranslator(),
                null, false, true);

        for (int x = 0; x < 18; x++) {
            for (int y = 0; y < 18; y++) {
                System.out.print(renderedOverlay.getBoundary().isInside(new Point(x, y)) ? "X" : " ");
            }
            System.out.println();
        }
        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 14; y++) {
                assertEquals("(" + x + ", " + y + "): ", expected[x][y],
                        renderedOverlay.getBoundary().isInside(new Point(x, y)));
            }
        }
    }



}
