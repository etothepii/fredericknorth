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

    private Rectangle[][] clips;
    private Polygon[] polygons;
    private Shape[][][] clippedShapes;
    private Point coarseGridStart;
    private int grain;

    public SquareSearchPolygonSifterImpl(Polygon[] polygons, int points) {
        this.polygons = polygons;
        Rectangle bounds = PolygonExtensions.getBounds(polygons);
        double grainDouble = bounds.width;
        grainDouble *= bounds.height;
        grainDouble *= 500;
        grainDouble /= points;
        grain = (int)Math.sqrt(grainDouble);
        if (grain < 2) {
            clippedShapes = null;
            return;
        }
        LOG.info("Grain: {}", grain);
        coarseGridStart = new Point(bounds.x, bounds.y);
        int coarseWidth = bounds.width / grain + 1;
        int coarseHeight = bounds.height / grain + 1;
        clippedShapes = new Shape[coarseWidth][][];
        clips = new Rectangle[coarseWidth][];
        for (int x = 0; x < coarseWidth; x++) {
            clippedShapes[x] = new Shape[coarseHeight][];
            clips[x] = new Rectangle[coarseHeight];
            for (int y = 0; y < coarseHeight; y++) {
                Rectangle rectangle = new Rectangle(coarseGridStart.x + x * grain, coarseGridStart.y + y * grain, grain, grain);
                clips[x][y] = RectangleExtensions.grow(rectangle, 5);
                clippedShapes[x][y] = PolygonExtensions.clip(polygons, clips[x][y]);
            }
        }
    }

    @Override
    public boolean contains(Point p) {
        if (clippedShapes == null) {
            return PolygonExtensions.contains(polygons, p);
        }
        int x = (p.x - coarseGridStart.x) / grain;
        int y = (p.y - coarseGridStart.y) / grain;
        if (x < 0 || x >= clippedShapes.length) return false;
        if (y < 0 || y >= clippedShapes[0].length) return false;
        Shape[] maybeShape = clippedShapes[x][y];
        debug(maybeShape, p, clips[x][y]);
        if (tooClose(maybeShape, p)) {
            return PolygonExtensions.contains(polygons, p);
        }
        return ShapeExtensions.contains(maybeShape, p);
    }

    private boolean tooClose(Shape[] maybeShape, Point p) {
        double minD = Double.MAX_VALUE;
        for (Shape s : maybeShape) {
            if (s instanceof Polygon) {
                NearestPoint nearestPoint =
                        PolygonExtensions.getNearestPoint((Polygon)s, new Point2D.Float(p.x, p.y));
                if (nearestPoint.dSquared < minD) {
                    minD = nearestPoint.dSquared;
                }
            }
        }
        LOG.debug("minD: {}", minD);
        return minD < 2d;
    }

    private void debug(final Shape[] shapes, final Point p, final Rectangle clip) {
        boolean allRects = true;
        for (Shape shape : shapes) {
            if (!(shape instanceof  Rectangle)) {
                allRects = false;
            }
        }
        if (allRects) {
            return;
        }
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D)graphics;
                double scale = 1;
                AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
                transform.translate(-p.x + getWidth() / 2 , -p.y + getHeight() / 2);
                g.setTransform(transform);
                g.setColor(new Color(255, 0, 0, 128));
                for (Polygon p : polygons) {
                    g.fill(p);
                }
                g.setColor(Color.RED);
                for (Polygon p : polygons) {
                    for (int i = 0; i < p.npoints; i++) {
                        g.fillOval(p.xpoints[i] - 1, p.ypoints[i] - 1, 2, 2);
                    }
                }
                g.setColor(new Color(0, 255, 0, 128));
                for (Shape s : shapes) {
                    g.fill(s);
                }
                g.setColor(new Color(0, 0, 255, 128));
//                g.fill(clip);
                g.setColor(Color.BLACK);
                g.setTransform(AffineTransform.getScaleInstance(1, 1));
                g.fillRect(getWidth() / 2 - 1, getHeight() / 2 - 1, 3, 3);
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
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        frame.dispose();
    }

}
