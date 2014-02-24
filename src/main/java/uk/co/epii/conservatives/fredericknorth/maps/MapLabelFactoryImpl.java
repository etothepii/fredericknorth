package uk.co.epii.conservatives.fredericknorth.maps;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 23:48
 */
public class MapLabelFactoryImpl implements MapLabelFactory {

    private static final Logger LOG = Logger.getLogger(MapLabelFactoryImpl.class);

    private final Font labelFont;
    private final int padding;
    private final int dotRadius;

    MapLabelFactoryImpl(Font labelFont, int padding, int dotRadius) {
        this.labelFont = labelFont;
        this.padding = padding;
        this.dotRadius = dotRadius;
    }

    @Override
    public List<MapLabel> getMapLabels(Rectangle imageBounds, List<? extends Location> namedPoints, Graphics g, ImageAndGeoPointTranslator translator) {
        ArrayList<MapLabel> rectangles = new ArrayList<MapLabel>();
        for (Location location : namedPoints) {
            rectangles.add(getMapLabel(imageBounds, rectangles,
                    new LocationImpl(location.getName(), translator.getImageLocation(location.getPoint())), g));
        }
        return rectangles;
    }

    private MapLabel getMapLabel(Rectangle imageBounds, java.util.List<MapLabel> rectangles, Location location, Graphics g) {
        Rectangle2D stringBounds = g.getFontMetrics(labelFont).getStringBounds(location.getName(), g);
        Dimension size = new Dimension((int)stringBounds.getWidth() + padding * 2, (int)stringBounds.getHeight() + padding * 2);
        MapLabel backup = null;
        for (Corner corner : Corner.getRandomizedCorners()) {
            Rectangle r = corner.getRectangle(location.getPoint(), size);
            MapLabel potential = new MapLabelImpl(location.getName(), r, corner, labelFont, padding, dotRadius);
            if (backup == null) {
                backup = potential;
            }
            if (whollyContainedWithin(imageBounds, r)) {
                backup = potential;
                if (!intersectsAny(rectangles, r)) {
                    return potential;
                }
            }
        }
        LOG.debug(location.getName() + " resorting to backup");
        return backup;
    }

    private boolean intersectsAny(java.util.List<MapLabel> rectangles, Rectangle r) {
        for (MapLabel compare : rectangles) {
            if (compare.getRectangle().intersects(r)) return true;
        }
        return false;
    }

    private boolean whollyContainedWithin(Rectangle outer, Rectangle inner) {
        return inner.x >= outer.x &&
                inner.y >= outer.y &&
                inner.x + inner.width <= outer.x + outer.width &&
                inner.y + inner.height <= outer.y + outer.height;
    }
}
