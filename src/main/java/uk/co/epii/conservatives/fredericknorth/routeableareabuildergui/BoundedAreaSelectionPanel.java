package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.util.LocaleServiceProviderPool;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea.BoundedAreaExtensions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 27/07/2013
 * Time: 00:30
 */
public class BoundedAreaSelectionPanel extends JPanel {

    private static Logger LOG = LoggerFactory.getLogger(BoundedAreaSelectionPanel.class);

    private final BoundedAreaSelectionModel model;
    private final Map<BoundedAreaType, JCheckBox> masterBoundedAreaSelectionTypes;
    private final Map<BoundedAreaType, JComboBox> boundedAreaSelectionComboBoxes;
    private final BoundedAreaType[] displayedOrder;
    private final ListCellRenderer boundedAreaCellRenderer;

    public BoundedAreaSelectionPanel(BoundedAreaSelectionModel boundedAreaSelectionModel) {
        super(new GridBagLayout());
        boundedAreaCellRenderer = BoundedAreaExtensions.getListCellRenderer();
        this.model = boundedAreaSelectionModel;
        masterBoundedAreaSelectionTypes = getMasterBoundedAreaSelectionTypes();
        boundedAreaSelectionComboBoxes = getBoundedAreaSelectionComboBoxes();
        displayedOrder = new BoundedAreaType[boundedAreaSelectionModel.getMaximumBoundedAreaGenerations()];
        addCheckboxes();
        updateVisibleBoundedAreaSelectors();
        addListeners();
        model.addBoundedAreaSelectionListener(new BoundedAreaSelectionAdapter() {
            @Override
            public void masterParentSelectionChanged(SelectedBoundedAreaChangedEvent e) {
                updateVisibleBoundedAreaSelectors();
            }
        });
        masterBoundedAreaSelectionTypes.get(model.getMasterSelectedType()).setSelected(true);
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                    boolean enabled = (Boolean)propertyChangeEvent.getNewValue();
                    for (JCheckBox checkBox : masterBoundedAreaSelectionTypes.values()) {
                        checkBox.setEnabled(enabled);
                    }
                    for (JComboBox comboBox : boundedAreaSelectionComboBoxes.values()) {
                        comboBox.setEnabled(enabled);
                    }
                }
            }
        });
    }

    private void addCheckboxes() {
        JPanel panel = new JPanel(new GridBagLayout());
        int column = 0;
        for (BoundedAreaType boundedAreaType : model.getRootSelectionTypes()) {
            panel.add(
                    masterBoundedAreaSelectionTypes.get(boundedAreaType),
                    new GridBagConstraints(column++, 0, 1, 1, 1d, 0d, GridBagConstraints.WEST,
                            GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel,
                new GridBagConstraints(0, 0, 4, 1, 1d, 0d, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void addListeners() {
        for (final BoundedAreaType boundedAreaType : model.getRootSelectionTypes()) {
            masterBoundedAreaSelectionTypes.get(boundedAreaType).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (((JCheckBox)e.getSource()).isSelected()) {
                        model.setMasterParentType(boundedAreaType);
                    }
                }
            });
        }
    }

    private void updateVisibleBoundedAreaSelectors() {
        LOG.debug("Updating Visible Bounded Area Selectors");
        BoundedAreaType[] visibleTypes = model.getVisibleBoundedAreaSelectorTypes();
        for (int i = displayedOrder.length - 1; i >= 0; i--) {
            BoundedAreaType visibleType = i < visibleTypes.length ? visibleTypes[i] : null;
            if (visibleType == displayedOrder[i]) {
                continue;
            }
            removeBoundedAreaType(displayedOrder[i]);
        }
        for (int i = displayedOrder.length - 1; i >= 0; i--) {
            BoundedAreaType visibleType = i < visibleTypes.length ? visibleTypes[i] : null;
            if (visibleType == displayedOrder[i]) {
                continue;
            }
            addBoundedAreaType(visibleType, i + 1);
            displayedOrder[i] = visibleType;
        }
        validate();
        if (getParent() != null) {
            getParent().validate();
        }
        repaint();
    }

    private void removeBoundedAreaType(BoundedAreaType type) {
        if (type != null) {
            remove(boundedAreaSelectionComboBoxes.get(type));
        }
    }

    private void addBoundedAreaType(BoundedAreaType type, int row) {
        if (type != null) {
            add(boundedAreaSelectionComboBoxes.get(type),
                    new GridBagConstraints(0, row, 1, 1, 1d, 1d, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        }
    }

    private Map<BoundedAreaType, JComboBox> getBoundedAreaSelectionComboBoxes() {
        EnumMap<BoundedAreaType, JComboBox> map = new EnumMap<BoundedAreaType, JComboBox>(BoundedAreaType.class);
        for (BoundedAreaType boundedAreaType : model.getSelectionTypes()) {
            JComboBox comboBox = new JComboBox(model.getComboBoxModel(boundedAreaType));
            comboBox.setRenderer(boundedAreaCellRenderer);
            map.put(boundedAreaType, comboBox);
        }
        return map;
    }

    private Map<BoundedAreaType, JCheckBox> getMasterBoundedAreaSelectionTypes() {
        EnumMap<BoundedAreaType, JCheckBox> checkboxes = new EnumMap<BoundedAreaType, JCheckBox>(BoundedAreaType.class);
        ButtonGroup buttonGroup = new ButtonGroup();
        BoundedAreaType[] boundedAreaTypes = model.getRootSelectionTypes();
        for (BoundedAreaType boundedAreaType : boundedAreaTypes) {
            JCheckBox button = new JCheckBox(boundedAreaType.getName());
            checkboxes.put(boundedAreaType, button);
            buttonGroup.add(button);
        }
        return checkboxes;
    }
}
