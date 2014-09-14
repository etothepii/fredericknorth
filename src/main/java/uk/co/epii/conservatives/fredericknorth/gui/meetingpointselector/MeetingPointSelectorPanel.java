package uk.co.epii.conservatives.fredericknorth.gui.meetingpointselector;

import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanel;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerJProgressBar;

import javax.swing.*;
import java.awt.*;

/**
 * User: James Robinson
 * Date: 21/02/2014
 * Time: 19:57
 */
public class MeetingPointSelectorPanel extends JPanel {

    private final MapPanel mapPanel;
    private final JTable table;
    private final JButton add;
    private final JButton remove;
    private final JButton save;
    private final JButton load;
    private final ProgressTrackerJProgressBar progressTracker;

    public MeetingPointSelectorPanel(MeetingPointSelectorPanelModel model, ApplicationContext applicationContext) {
        super(new GridBagLayout());
        mapPanel = new MapPanel(model.getMapPanelModel(), 1d);
        table = new JTable(model.getMeetingPointsModel());
        add = makeButton("Add");
        remove = makeButton("Remove");
        save = makeButton("Save");
        load = makeButton("Load");
        progressTracker = new ProgressTrackerJProgressBar(1);
        model.setProgressTracker(progressTracker);
        add(mapPanel, new GridBagConstraints(0, 0, 1, 3, 1d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(table, new GridBagConstraints(1, 0, 2, 1, 0d, 1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 0, 5, 5), 0, 0));
        add(add, new GridBagConstraints(1, 1, 1, 1, 0d, 0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(remove, new GridBagConstraints(2, 1, 1, 1, 0d, 0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(save, new GridBagConstraints(1, 2, 1, 1, 0d, 0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(load, new GridBagConstraints(2, 2, 1, 1, 0d, 0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(progressTracker, new GridBagConstraints(0, 3, 3, 1, 1d, 0d, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    private JButton makeButton(String name) {
        JButton button = new JButton(name);
        int height = button.getPreferredSize().height;
        button.setPreferredSize(new Dimension(100, height));
        return button;
    }

}
