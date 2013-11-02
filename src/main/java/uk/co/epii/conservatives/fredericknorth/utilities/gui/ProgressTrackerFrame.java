package uk.co.epii.conservatives.fredericknorth.utilities.gui;

import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private BufferedImage background;

    public ProgressTrackerFrame() {
        this(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), 1);
    }

    public ProgressTrackerFrame(BufferedImage background, int maximum) throws HeadlessException {
        progressBar = new ProgressTrackerJProgressBar(maximum);
        this.background = background;
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(panel, new GridBagConstraints(0, 0, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        getContentPane().add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        setUndecorated(true);
        init();
    }

    private void init() {
        pack();
        setLocationRelativeTo(null);
    }

    public void setProgressBarSteps(int steps) {
        progressBar.finish();
        progressBar.startSubsection(steps);
    }

    public void setImageResourceLocation(String imageResourceLocation) {
        try {
            background = ImageIO.read(ProgressTrackerFrame.class.getResourceAsStream(imageResourceLocation));
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void setImageLocation(String imageLocation) {
        try {
            background = ImageIO.read(new File(imageLocation));
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
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

    @Override
    public void endSubsection() {
        progressBar.endSubsection();
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
}
