package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.maps.gui.Dot;

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

        Component renderedDot = new DottedDwellingGroupOverlayRenderer().getOverlayRendererComponent(
                new DummyOverlayItem<DottedDwellingGroup>(
                        new DottedDwellingGroup(
                                null,
                                new Dot(
                                        new int[] {5, 2},
                                        new Color[] {Color.RED, Color.WHITE}
                                )
                        )
                ),
                null,
                null);
        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 14; y++) {
                assertEquals("(" + x + ", " + y + "): ", expected[x][y], renderedDot.contains(x, y));
                assertEquals("(" + x + ", " + y + "): ", expected[x][y], renderedDot.contains(new Point(x, y)));
            }
        }
    }



}
