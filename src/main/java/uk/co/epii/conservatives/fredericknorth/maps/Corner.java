package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 13:56
 */
public enum Corner {
    NW,
    NE,
    SW,
    SE;

    public static Corner[][] allOrders = new Corner[24][];

    static {
        Corner[] corners = values();
        int index = 0;
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                for (int c = 0; c < 4; c++) {
                    for (int d = 0; d < 4; d++) {
                        if (a != b && a != c && a != d && b != c && b != d && c != d) {
                            allOrders[index++] = new Corner[] {corners[a], corners[b], corners[c], corners[d]};
                        }
                    }
                }
            }
        }
    }

    public static Corner[] getRandomizedCorners() {
        int randomOrderIndex = (int)(Math.random() * 24);
        return allOrders[randomOrderIndex];
    }

    public Rectangle getRectangle(Point p, Dimension size) {
        switch (this) {
            case NW: return new Rectangle(p, size);
            case NE: return new Rectangle(new Point(p.x - size.width, p.y), size);
            case SE: return new Rectangle(new Point(p.x - size.width, p.y - size.height), size);
            case SW: return new Rectangle(new Point(p.x, p.y - size.height), size);
        }
        throw new RuntimeException("The corners are exhausted this isn't possible");
    }

    public Point getCorner(Rectangle r) {
        switch (this) {
            case NW: return r.getLocation();
            case NE: return new Point(r.x + r.width, r.y);
            case SE: return new Point(r.x + r.width, r.y + r.height);
            case SW: return new Point(r.x, r.y + r.height);
        }
        throw new RuntimeException("The cases have been exhausted, how has this occurred");
    }
}
