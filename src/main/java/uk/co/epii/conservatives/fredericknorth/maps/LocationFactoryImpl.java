package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 00:47
 */
public class LocationFactoryImpl implements LocationFactory {

    private final int minimumPadding;

    public LocationFactoryImpl(int minimumPadding) {
        this.minimumPadding = minimumPadding;
    }

    public Location getInstance(String name, Point p) {
        return new LocationImpl(name, p);
    }

    public Rectangle calculatePaddedRectangle(java.util.List<? extends Location> locations) {
        Point min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Location mapLocation : locations) {
            updateMinMax(mapLocation.getPoint(), min, max);
        }
        Dimension size = new Dimension(max.x - min.x, max.y - min.y);
        Dimension paddedSize = new Dimension(size.width * 6 / 5, size.height * 6 / 5);
        if (paddedSize.width < size.width + minimumPadding) {
            paddedSize.width = size.width + minimumPadding;
        }
        if (paddedSize.height < size.height + minimumPadding) {
            paddedSize.height = size.height + minimumPadding;
        }
        return new Rectangle(
                new Point(min.x - ((paddedSize.width - size.width) / 2), min.y - ((paddedSize.height - size.height) / 2)),
                paddedSize);
    }

    private void updateMinMax(Point p, Point min, Point max) {
        if (p.x > max.x) max.x = p.x;
        if (p.y > max.y) max.y = p.y;
        if (p.x < min.x) min.x = p.x;
        if (p.y < min.y) min.y = p.y;
    }
}
