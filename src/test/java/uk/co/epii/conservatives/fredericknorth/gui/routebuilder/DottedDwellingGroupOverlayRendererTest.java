package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.maps.gui.Dot;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.RenderedOverlay;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingGroupDatabaseImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Test
    public void singleDwellingTest() {
        DwellingGroup dwellingGroup = new DummyDwellingGroup("A Street", 1, new Point(0, 0));
        DottedDwellingGroupOverlayItemImpl overlayItem = new DottedDwellingGroupOverlayItemImpl(dwellingGroup, 0);
        DottedDwellingGroupOverlayRenderer overlayRenderer = new DottedDwellingGroupOverlayRenderer();
        RenderedOverlay renderedOverlay =
                overlayRenderer.getOverlayRendererComponent(null, overlayItem, new DummyImageAndGeoPointTranslator(),
                        new Point(0, 0), false, false);
        Component component = renderedOverlay.getComponent();
        BufferedImage bufferedImage = new BufferedImage(
                component.getPreferredSize().width, component.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.createGraphics();
        component.paint(g);
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int[][] result = new int[h][w];
        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                result[i][j] = bufferedImage.getRGB( j, i );
                System.out.print(result[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        int[][] expected = new int[][] {
                new int[] {0, 0, 0, 0, 0, 0},
                new int[] {0, -1, -1, -1, -1, -1},
                new int[] {0, -1, -1, -1, -1, -1},
                new int[] {-1, -1, -16776961, -16776961, -1, -1},
                new int[] {-1, -1, -16776961, -16776961, -1, -1},
                new int[] {0, -1, -1, -1, -1, -1},
                new int[] {0, -1, -1, -1, -1, -1}
        };
        assertArrayEquals(expected, result);
    }

}
