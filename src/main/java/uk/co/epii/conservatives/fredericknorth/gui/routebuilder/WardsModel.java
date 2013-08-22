package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.routes.Ward;

import javax.swing.*;
import java.util.*;

/**
 * User: James Robinson
 * Date: 02/07/2013
 * Time: 20:23
 */
class WardsModel extends AbstractListModel implements ComboBoxModel {

    private final HashMap<Ward, Integer> wardsIndexMap;
    private final List<Ward> wards;
    private final RouteBuilderMapFrameModel routeBuilderMapFrameModel;

    private Ward selectedWard;

    public WardsModel(RouteBuilderMapFrameModel routeBuilderMapFrameModel) {
        this.routeBuilderMapFrameModel = routeBuilderMapFrameModel;
        this.wards = new ArrayList<Ward>(routeBuilderMapFrameModel.getCouncil().getWards());
        this.wardsIndexMap = new HashMap<Ward, Integer>(this.wards.size());
        updateWardsIndexMap();
        if (!this.wards.isEmpty()) {
            setSelectedWard(this.wards.get(0));
        }
    }

    private void updateWardsIndexMap() {
        Collections.sort(this.wards, new Comparator<Ward>() {
            @Override
            public int compare(Ward o1, Ward o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        wardsIndexMap.clear();
        int index = 0;
        for (Ward ward : this.wards) {
            wardsIndexMap.put(ward, index++);
        }
    }

    @Override
    public void setSelectedItem(Object o) {
        if (!(o instanceof Ward)) {
            throw new IllegalArgumentException("The item so provided must be of type Ward");
        }
        setSelectedWard((Ward) o);
    }

    private void setSelectedWard(Ward ward) {
        if (!wards.contains(ward)) {
            throw new IllegalArgumentException("You have selected an unknown Ward");
        }
        if (hasWardChanged(ward)) {
            selectedWard = ward;
            refresh();
        }
    }

    private boolean hasWardChanged(Ward ward) {
        if (selectedWard == null && ward == null) return false;
        if (selectedWard == null || ward == null) return true;
        return !selectedWard.equals(ward);
    }

    @Override
    public Ward getSelectedItem() {
        return selectedWard;
    }

    @Override
    public int getSize() {
        return wards.size();
    }

    @Override
    public Object getElementAt(int i) {
        return wards.get(i);
    }

    public void updateSelected() {
        int selectedIndex = wardsIndexMap.get(selectedWard);
        fireContentsChanged(this, selectedIndex, selectedIndex);
    }

    public void refresh() {
        routeBuilderMapFrameModel.getRoutesModel().setSelectedWard(selectedWard);
        routeBuilderMapFrameModel.updateOverlays();
    }

    public List<Ward> getUnselectedRoutes() {
        List<Ward> routes = new ArrayList<Ward>(this.wards.size());
        int selectedIndex = wardsIndexMap.get(selectedWard);
        for (int i = 0; i < routes.size(); i++) {
            if (i != selectedIndex) {
                routes.add(this.wards.get(i));
            }
        }
        return routes;
    }
}
