package uk.co.epii.conservatives.fredericknorth.geometry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.ShapeExtensions;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 23/02/2014
 * Time: 02:07
 */
public class SquareSearchPolygonSifterImpl implements PolygonSifter {

    private static final Logger LOG = LoggerFactory.getLogger(SquareSearchPolygonSifterImpl.class);

    private enum YesNoMaybe {YES, NO, MAYBE};

    private Polygon[] polygons;

    private YesNoMaybe[][] coarseGrid;
    private Shape[][][] maybeShapes;
    private Point coarseGridStart;
    private int grain;

    public SquareSearchPolygonSifterImpl(Polygon[] polygons, int points) {
        this.polygons = polygons;
        Rectangle bounds = PolygonExtensions.getBounds(polygons);
        double grainDouble = bounds.width;
        grainDouble *= bounds.height;
        grainDouble *= 150;
        grainDouble /= points;
        grain = (int)Math.sqrt(grainDouble);
        if (grain < 2) {
            coarseGrid = null;
            return;
        }
        LOG.info("Grain: {}", grain);
        coarseGridStart = new Point(bounds.x, bounds.y);
        int coarseWidth = bounds.width / grain + 1;
        int coarseHeight = bounds.height / grain + 1;
        coarseGrid = new YesNoMaybe[coarseWidth][];
        maybeShapes = new Polygon[coarseWidth][][];
        for (int x = 0; x < coarseWidth; x++) {
            coarseGrid[x] = new YesNoMaybe[coarseHeight];
            maybeShapes[x] = new Polygon[coarseHeight][];
            for (int y = 0; y < coarseHeight; y++) {
                Rectangle rectangle = new Rectangle(coarseGridStart.x + x * grain, coarseGridStart.y + y * grain, grain, grain);
                if (PolygonExtensions.contains(polygons, rectangle)) {
                    coarseGrid[x][y] = YesNoMaybe.YES;
                }
                else if (PolygonExtensions.intersects(polygons, rectangle)) {
                    coarseGrid[x][y] = YesNoMaybe.MAYBE;
                    maybeShapes[x][y] = PolygonExtensions.clip(polygons[0], RectangleExtensions.grow(rectangle, 5));
                }
                else {
                    coarseGrid[x][y] = YesNoMaybe.NO;
                }
            }
        }
    }

    @Override
    public boolean contains(Point p) {
        if (coarseGrid == null) {
            return PolygonExtensions.contains(polygons, p);
        }
        int x = (p.x - coarseGridStart.x) / grain;
        int y = (p.y - coarseGridStart.y) / grain;
        if (x < 0 || x >= coarseGrid.length) return false;
        if (y < 0 || y >= coarseGrid[0].length) return false;
        switch (coarseGrid[x][y]) {
            case YES: return true;
            case NO: return false;
            default:
                Shape[] maybeShape = maybeShapes[x][y];
                return ShapeExtensions.contains(maybeShape, p);
        }
    }
}
