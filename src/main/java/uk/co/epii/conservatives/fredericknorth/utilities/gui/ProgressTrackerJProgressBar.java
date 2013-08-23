package uk.co.epii.conservatives.fredericknorth.utilities.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 11/08/2013
 * Time: 19:54
 */
public class ProgressTrackerJProgressBar extends JPanel implements ProgressTracker {

    private static final Logger LOG = LoggerFactory.getLogger(ProgressTrackerJProgressBar.class);
    private static final Logger LOG_INCREMENT = LoggerFactory.getLogger(ProgressTrackerJProgressBar.class.toString().concat(".increment"));
    private static final Logger LOG_PAINT = LoggerFactory.getLogger(ProgressTrackerJProgressBar.class.toString().concat(".paint"));

    private final Object sync = new Object();
    private final JProgressBar progressBar;
    private final List<Integer> subsectionSizes = new ArrayList<Integer>();
    private final List<Integer> subsectionCounts = new ArrayList<Integer>();
    private String message;

    public ProgressTrackerJProgressBar(int steps) {
        super(new BorderLayout());
        progressBar = new JProgressBar();
        add(progressBar);
        progressBar.setMaximum(1);
        startSubsection(steps);
    }

    private Rectangle getMessageBounds(Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        return g.getFont().createGlyphVector(frc, message).getPixelBounds(frc, 0, 0);
    }

    @Override
    public void startSubsection(int steps) {
        LOG.debug("startSubsection({})", steps);
        synchronized (sync) {
            if (progressBar.getValue() == progressBar.getMaximum()) {
                finish();
            }
            subsectionSizes.add(0, steps);
            subsectionCounts.add(0, 0);
            LOG.debug("progressBar.getValue(): {}", progressBar.getValue());
            LOG.debug("progressBar.getMaximum(): {}", progressBar.getMaximum());
            LOG.debug("progressBar.isIndeterminate(): {}", progressBar.isIndeterminate());
            int value = progressBar.getValue();
            int maximum = progressBar.getMaximum();
            progressBar.setMaximum(maximum * steps);
            progressBar.setValue(value * steps);
        }
    }

    @Override
    public void increment(String message, int n) {
        LOG_INCREMENT.debug("increment({}, {})", new Object[] {message, n});
        synchronized (sync) {
            progressBar.setValue(progressBar.getValue() + n);
            this.message = message;
            subsectionCounts.set(0, subsectionCounts.get(0) + n);
            if (!subsectionSizes.isEmpty() && subsectionCounts.get(0) >= subsectionSizes.get(0)) {
                subsectionEnded();
            }
            if (progressBar.getValue() == progressBar.getMaximum()) {
                LOG.debug("Maximum reached");
                progressBar.setIndeterminate(true);
            }
        }
        progressBar.repaint();
    }

    private void subsectionEnded() {
        LOG.debug("subsectionEnded()");
        synchronized (sync) {
            int subsectionSize = subsectionSizes.remove(0);
            subsectionCounts.remove(0);
            int value = progressBar.getValue();
            int maximum = progressBar.getMaximum();
            progressBar.setMaximum(maximum / subsectionSize);
            progressBar.setValue(value / subsectionSize);
            if (subsectionCounts.isEmpty()) {
                finish();
            }
            else {
                subsectionCounts.set(0, subsectionCounts.get(0) + 1);
                if (subsectionCounts.get(0) >= subsectionSizes.get(0)) {
                    subsectionEnded();
                }
            }
        }
        repaint();
    }

    @Override
    public void increment(String message) {
        LOG_INCREMENT.debug("increment({})", new Object[] {message});
        increment(message, 1);
    }

    @Override
    public void increment(int n) {
        LOG_INCREMENT.debug("increment({})", new Object[] {n});
        increment(null, n);
    }

    @Override
    public void increment() {
        LOG_INCREMENT.debug("increment()");
        increment(1);
    }

    @Override
    public void setMessage(String message) {
        LOG_INCREMENT.debug("setMessage({})", new Object[] {message});
        synchronized (sync) {
            this.message = message;
        }
        repaint();
    }

    @Override
    public Object getSync() {
        LOG.debug("getSync()");
        return sync;
    }

    @Override
    public void finish() {
        LOG.debug("finish()");
        subsectionSizes.clear();
        progressBar.setMaximum(1);
        progressBar.setValue(0);
        progressBar.setIndeterminate(false);
    }

    @Override
    public boolean isAtEnd() {
        LOG.debug("isAtEnd()");
        return progressBar.getMaximum() == progressBar.getValue();
    }

    public void paint(Graphics g) {
        LOG_PAINT.debug("paint(Graphics g)");
        synchronized (sync) {
            super.paint(g);
            if (message == null) {
                return;
            }
            Rectangle messageBounds = getMessageBounds((Graphics2D)g);
            Dimension size = getSize();
            int x = (size.width - messageBounds.width) / 2 + messageBounds.x;
            int y = (size.height - messageBounds.height) / 2 - messageBounds.y;
            g.drawString(message, x, y);
        }
    }
}
