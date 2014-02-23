package uk.co.epii.conservatives.fredericknorth.geometry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.ShapeExtensions;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

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
        maybeShapes = new Shape[coarseWidth][][];
        for (int x = 0; x < coarseWidth; x++) {
            coarseGrid[x] = new YesNoMaybe[coarseHeight];
            maybeShapes[x] = new Shape[coarseHeight][];
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
//                debug(maybeShape, p);
                return ShapeExtensions.contains(maybeShape, p);
        }
    }

    private void debug(final Shape[] shapes, final Point p) {
        final Rectangle bounds = ShapeExtensions.getBounds(shapes);
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D)graphics;
                double scale = Math.max(bounds.width / (double)getWidth(), bounds.height / (double)getHeight());
                scale = 0.8d / scale;
                AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
                transform.translate(-bounds.x + bounds.width / 5 , -bounds.y + bounds.height / 5);
                g.setTransform(transform);
                Point2D.Double origin = new Point2D.Double();
                transform.transform(new Point2D.Double(bounds.getX(), bounds.getY()), origin);
                LOG.info(origin.toString());
                transform.transform(new Point2D.Double(bounds.getX()+ bounds.width, bounds.getY() + bounds.height), origin);
                LOG.info(origin.toString());
                g.setColor(new Color(255, 0, 0, 128));
                for (Polygon p : polygons) {
                    g.fill(p);
                }
                g.setColor(new Color(0, 255, 0, 128));
                for (Shape s : shapes) {
                    g.fill(s);
                }
                g.setColor(Color.BLACK);
                int ovalSize = (int)(5 * scale);
                g.fillOval(p.x - ovalSize, p.y - ovalSize, ovalSize * 2, ovalSize * 2);
            }
        };
        panel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        frame.dispose();
    }

}
