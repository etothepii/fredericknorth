package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 01:05
 */
public class DotFactoryImpl implements DotFactory {

    private final Map<Color, Dot> standardDots;
    private final int dotRadius;
    private final int dotBorderRadiusAddition;

    public DotFactoryImpl(int dotRadius, int dotBorderRadiusAddition) {
        this.dotRadius = dotRadius;
        this.dotBorderRadiusAddition = dotBorderRadiusAddition;
        standardDots = new HashMap<Color, Dot>();
    }

    @Override
    public Dot getStandardDot(Color color) {
        if (!standardDots.containsKey(color)) {
            standardDots.put(color,
                    new Dot(
                            new int[] {dotRadius, dotBorderRadiusAddition},
                            new Color[]{color, Color.WHITE}));
        }
        return standardDots.get(color);
    }
}
