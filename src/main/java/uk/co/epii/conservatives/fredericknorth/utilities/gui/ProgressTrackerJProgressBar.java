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
    private final List<Integer> _subsectionSizes = new ArrayList<Integer>();
    private final List<Integer> _subsectionCounts = new ArrayList<Integer>();
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

    private void addSubsection(int steps) {
        _subsectionSizes.add(0, steps);
        _subsectionCounts.add(0, 0);
    }

    @Override
    public void startSubsection(int steps) {
        LOG.debug("startSubsection({})", steps);
        synchronized (sync) {
            if (progressBar.getValue() == progressBar.getMaximum()) {
                finish();
            }
            addSubsection(steps);
            LOG.debug("from progressBar.getValue(): {}", progressBar.getValue());
            LOG.debug("from progressBar.getMaximum(): {}", progressBar.getMaximum());
            int value = progressBar.getValue();
            int maximum = progressBar.getMaximum();
            progressBar.setMaximum(maximum * steps);
            progressBar.setValue(value * steps);
            LOG.debug("to progressBar.getValue(): {}", progressBar.getValue());
            LOG.debug("to progressBar.getMaximum(): {}", progressBar.getMaximum());
        }
    }

    private void incrementSubsection(int n) {
        if (_subsectionCounts.isEmpty() || _subsectionSizes.isEmpty()) {
            throw new RuntimeException("No Active Subsection");
        }
        _subsectionCounts.set(0, _subsectionCounts.get(0) + n);
    }

    private boolean isActiveSubsectionFinished() {
        return _subsectionCounts.get(0) >= _subsectionSizes.get(0);
    }

    private boolean hasActiveSubsection() {
        return !_subsectionCounts.isEmpty();
    }

    @Override
    public void increment(String message, int n) {
        LOG_INCREMENT.debug("increment({}, {})", new Object[] {message, n});
        synchronized (sync) {
            progressBar.setValue(progressBar.getValue() + n);
            this.message = message;
            incrementSubsection(n);
            if (isActiveSubsectionFinished()) {
                subsectionEnded();
            }
        }
        progressBar.repaint();
    }

    private int removeActiveSubsection() {
        _subsectionCounts.remove(0);
        return  _subsectionSizes.remove(0);
    }

    private void subsectionEnded() {
        LOG.debug("subsectionEnded()");
        synchronized (sync) {
            int subsectionSize = removeActiveSubsection();
            int value = progressBar.getValue();
            int maximum = progressBar.getMaximum();
            progressBar.setMaximum(maximum / subsectionSize);
            progressBar.setValue(value / subsectionSize);
            if (!hasActiveSubsection()) {
                finish();
            }
            else {
                if (isActiveSubsectionFinished()) {
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
        clearSubsections();
        progressBar.setMaximum(1);
        progressBar.setValue(0);
        progressBar.setIndeterminate(false);
    }

    private void clearSubsections() {
        if (!_subsectionCounts.isEmpty() || !_subsectionSizes.isEmpty()) {
            LOG.warn("Clearing ({}, {}) subsections", _subsectionCounts.size(), _subsectionSizes.size());
        }
        _subsectionCounts.clear();
        _subsectionSizes.clear();
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
