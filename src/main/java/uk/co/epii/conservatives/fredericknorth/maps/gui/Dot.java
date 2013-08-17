package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;
import java.util.Arrays;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 11:49
 */
public class Dot {

    private final int[] radii;
    private final Color[] colours;

    public Dot(int[] radii, Color[] colours) {
        this.radii = radii;
        this.colours = colours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dot dot = (Dot) o;

        if (!Arrays.equals(colours, dot.colours)) return false;
        if (!Arrays.equals(radii, dot.radii)) return false;

        return true;
    }

    public int[] getRadii() {
        return radii;
    }

    public Color[] getColours() {
        return colours;
    }

    @Override
    public int hashCode() {
        int result = radii != null ? Arrays.hashCode(radii) : 0;
        result = 31 * result + (colours != null ? Arrays.hashCode(colours) : 0);
        return result;
    }

}
