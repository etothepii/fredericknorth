package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 26/07/2013
 * Time: 19:00
 */
public interface BoundedAreaSelectionModel {

    public BoundedAreaType[] getSelectionTypes();
    public ComboBoxModel getComboBoxModel(BoundedAreaType type);
    public int getChildTypes();
    public void setMasterParentType(BoundedAreaType boundedAreaType);
    public BoundedArea getSelected(BoundedAreaType boundedAreaType);
    public BoundedArea getMasterSelected();
    public BoundedAreaType getMasterSelectedType();
    public void addBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l);
    public void removeBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l);
    public int getMaximumBoundedAreaGenerations();
    public BoundedAreaType[] getVisibleBoundedAreaSelectorTypes();
    public String getNextSuggestedName(BoundedAreaType boundedAreaType);
    public BoundedArea getParent(BoundedArea selection);
    public void add(BoundedArea parent, BoundedArea boundedArea);
    public void saveAll(File selectedFile);
    public void loadFrom(File selectedFile, ApplicationContext applicationContext);
    public BoundedAreaType[] getRootSelectionTypes();
    public void loadOSKnownInstances();
    public Map<BoundedAreaType,BoundedArea> getAllSelected();
    public BoundedArea getSelected();
    public List<MeetingPoint> getMeetingPoints();
}
