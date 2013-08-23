package uk.co.epii.conservatives.fredericknorth.utilities.gui;

import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 09/08/2013
 * Time: 19:32
 */
public class ProgressTrackerFrame extends JFrame implements ProgressTracker {

    private final JPanel panel = new JPanel() {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(background.getWidth(), background.getHeight());
        }

        @Override
        public void paint(Graphics g) {
            g.drawImage(background, 0, 0, this);
        }
    };

    private final ProgressTrackerJProgressBar progressBar;
    private final BufferedImage background;

    public ProgressTrackerFrame(BufferedImage background, int maximum) throws HeadlessException {
        progressBar = new ProgressTrackerJProgressBar(maximum);
        this.background = background;
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(panel, new GridBagConstraints(0, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        setUndecorated(true);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void startSubsection(int steps) {
        progressBar.startSubsection(steps);
    }

    @Override
    public void increment(String message, int n) {
        progressBar.increment(message, n);
    }

    @Override
    public void increment(String message) {
        progressBar.increment(message);
    }

    @Override
    public void increment(int n) {
        progressBar.increment(n);
    }

    @Override
    public void increment() {
        progressBar.increment();
    }

    @Override
    public void setMessage(String message) {
        progressBar.setMessage(message);
    }

    @Override
    public Object getSync() {
        return progressBar.getSync();
    }

    @Override
    public void finish() {
        progressBar.finish();
    }

    @Override
    public boolean isAtEnd() {
        return progressBar.isAtEnd();
    }
}
