package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.BoundedAreaSelectionModel;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.MeetingPoint;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.SelectedBoundedAreaChangedListener;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 22/08/2013
 * Time: 16:48
 */
public class DummyBoundedAreaSelectionModel implements BoundedAreaSelectionModel {

    private BoundedArea boundedArea;

    public DummyBoundedAreaSelectionModel(BoundedArea boundedArea) {
        this.boundedArea = boundedArea;
    }

    @Override
    public BoundedAreaType[] getSelectionTypes() {
        return new BoundedAreaType[] {boundedArea.getBoundedAreaType()};
    }

    @Override
    public ComboBoxModel getComboBoxModel(BoundedAreaType type) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public int getChildTypes() {
        return 0;
    }

    @Override
    public void setMasterParentType(BoundedAreaType boundedAreaType) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public BoundedArea getSelected(BoundedAreaType boundedAreaType) {
        return boundedAreaType == boundedArea.getBoundedAreaType() ? boundedArea : null;
    }

    @Override
    public BoundedArea getMasterSelected() {
        return boundedArea;
    }

    @Override
    public BoundedAreaType getMasterSelectedType() {
        return boundedArea.getBoundedAreaType();
    }

    @Override
    public void addBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l) {
    }

    @Override
    public void removeBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l) {
    }

    @Override
    public int getMaximumBoundedAreaGenerations() {
        return 1;
    }

    @Override
    public BoundedAreaType[] getVisibleBoundedAreaSelectorTypes() {
        return getSelectionTypes();
    }

    @Override
    public String getNextSuggestedName(BoundedAreaType boundedAreaType) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public BoundedArea getParent(BoundedArea selection) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public void add(BoundedArea parent, BoundedArea boundedArea) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public void saveAll(File selectedFile) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public void loadFrom(File selectedFile, ApplicationContext applicationContext) {
        throw new UnsupportedOperationException("This operation is not supported by the dummy");
    }

    @Override
    public BoundedAreaType[] getRootSelectionTypes() {
        return getSelectionTypes();
    }

    @Override
    public void loadOSKnownInstances() {
    }

    @Override
    public Map<BoundedAreaType, BoundedArea> getAllSelected() {
        EnumMap<BoundedAreaType, BoundedArea> selected = new EnumMap<BoundedAreaType, BoundedArea>(BoundedAreaType.class);
        selected.put(boundedArea.getBoundedAreaType(), boundedArea);
        return selected;

    }

    @Override
    public BoundedArea getSelected() {
        return boundedArea;
    }

    @Override
    public List<MeetingPoint> getMeetingPoints() {
        return new ArrayList<MeetingPoint>();
    }
}
