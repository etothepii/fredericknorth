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
    private static final Logger LOG_INCREMENT = LoggerFactory.getLogger(
            ProgressTrackerJProgressBar.class.getName().concat("_increment"));
    private static final Logger LOG_PAINT = LoggerFactory.getLogger(
            ProgressTrackerJProgressBar.class.getName().concat("_paint"));
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(
            ProgressTrackerJProgressBar.class.getName().concat("_sync"));

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
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
                if (progressBar.getValue() == progressBar.getMaximum()) {
                    finish();
                }
                progressBar.setIndeterminate(false);
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
        finally {
            LOG_SYNC.debug("Released sync");
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
        if (n == 0) return;
        if (n < 0) {
            throw new IllegalArgumentException("Cannot decrement with a progress tracker n = ".concat(n + ""));
        }
        LOG_INCREMENT.debug("increment({}, {})", new Object[] {message, n});
        LOG_INCREMENT.debug("increment start max: {} value: {}", getMaximum(), getValue());
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
                progressBar.setValue(progressBar.getValue() + n);
                if (message != null) {
                    this.message = message;
                }
                incrementSubsection(n);
                if (isActiveSubsectionFinished()) {
                    subsectionEnded();
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released sync");
        }
        progressBar.repaint();
        LOG_INCREMENT.debug("increment end max: {} value: {}", getMaximum(), getValue());
    }

    private int removeActiveSubsection() {
        _subsectionCounts.remove(0);
        return  _subsectionSizes.remove(0);
    }

    private void subsectionEnded() {
        LOG.debug("subsectionEnded()");
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
                int subsectionSize = removeActiveSubsection();
                int value = progressBar.getValue();
                int maximum = progressBar.getMaximum();
                progressBar.setMaximum(maximum / subsectionSize);
                progressBar.setValue(value / subsectionSize);
                if (hasActiveSubsection()) {
                    incrementSubsection(1);
                    if (isActiveSubsectionFinished()) {
                        subsectionEnded();
                    }
                }
                else {
                    finish();
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released sync");
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
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
                this.message = message;
            }
        }
        finally {
            LOG_SYNC.debug("Released sync");
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
        progressBar.setIndeterminate(true);
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

    @Override
    public void endSubsection() {
        increment(_subsectionSizes.get(0) - _subsectionCounts.get(0));
    }

    @Override
    public int getMaximum() {
        return progressBar.getMaximum();
    }

    @Override
    public int getValue() {
        return progressBar.getValue();
    }

    @Override
    public boolean isIndeterminate() {
        return progressBar.isIndeterminate();
    }

    @Override
    public void setIndeterminate() {
        progressBar.setIndeterminate(true);
    }

    public void paint(Graphics g) {
        LOG_PAINT.debug("paint(Graphics g)");
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
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
        finally {
            LOG_SYNC.debug("Released sync");
        }
    }
}
