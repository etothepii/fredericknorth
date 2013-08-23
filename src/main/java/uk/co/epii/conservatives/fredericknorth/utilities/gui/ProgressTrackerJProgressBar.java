package uk.co.epii.conservatives.fredericknorth.utilities.gui;

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

    private final Object sync = new Object();
    private final JProgressBar progressBar;
    private final List<Integer> subsectionSizes = new ArrayList<Integer>();
    private int subsectionCount = 0;
    private String message;

    public ProgressTrackerJProgressBar(int steps) {
        super(new BorderLayout());
        progressBar = new JProgressBar();
        add(progressBar);
        setSteps(steps);
    }

    private Rectangle getMessageBounds(Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        return g.getFont().createGlyphVector(frc, message).getPixelBounds(frc, 0, 0);
    }

    @Override
    public void setSteps(int n) {
        synchronized (sync) {
            subsectionSizes.clear();
            progressBar.setMaximum(n);
            subsectionCount = 0;
        }
        repaint();
    }

    @Override
    public void setStep(int n) {
        synchronized (sync) {
            progressBar.setValue(n);
        }
        repaint();
    }

    @Override
    public void startSubsection(int steps) {
        synchronized (sync) {
            if (progressBar.getValue() == progressBar.getMaximum()) {
                finish();
            }
            subsectionSizes.add(0, steps);
            int value = progressBar.getValue();
            int maximum = progressBar.getMaximum();
            progressBar.setMaximum(maximum * steps);
            progressBar.setValue(value * steps);
        }
    }

    @Override
    public void increment(String message, int n) {
        synchronized (sync) {
            progressBar.setValue(progressBar.getValue() + n);
            this.message = message;
            subsectionCount += n;
            if (!subsectionSizes.isEmpty() && subsectionCount >= subsectionSizes.get(0)) {
                subsectionEnded();
            }
            if (progressBar.getValue() == progressBar.getMaximum()) {
                progressBar.setIndeterminate(true);
            }
        }
        progressBar.repaint();
    }

    private void subsectionEnded() {
        synchronized (sync) {
            int subsectionSize = subsectionSizes.remove(0);
            int value = progressBar.getValue();
            int maximum = progressBar.getMaximum();
            progressBar.setMaximum(maximum / subsectionSize);
            progressBar.setValue(value / subsectionSize);
        }
        repaint();
    }

    @Override
    public void increment(String message) {
        increment(message, 1);
    }

    @Override
    public void increment(int n) {
        increment(null, n);
    }

    @Override
    public void increment() {
        increment(1);
    }

    @Override
    public void setMessage(String message) {
        synchronized (sync) {
            this.message = message;
        }
        repaint();
    }

    @Override
    public Object getSync() {
        return sync;
    }

    @Override
    public void finish() {
        subsectionSizes.clear();
        progressBar.setMaximum(1);
        progressBar.setValue(0);
        progressBar.setIndeterminate(false);
    }

    @Override
    public boolean isAtEnd() {
        return progressBar.getMaximum() == progressBar.getValue();
    }

    public void paint(Graphics g) {
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
