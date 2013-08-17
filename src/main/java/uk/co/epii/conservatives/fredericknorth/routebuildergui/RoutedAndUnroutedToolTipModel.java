package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 22:02
 */
class RoutedAndUnroutedToolTipModel {

    private static final Logger LOG = Logger.getLogger(RoutedAndUnroutedToolTipModel.class);

    private RouteBuilderMapFrameModel routeBuilderMapFrameModel;
    private DwellingGroupModel dwellingGroupModel;
    private final List<RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener>
            routedAndUnroutedToolTipDwellingGroupsUpdatedListeners;

    public RoutedAndUnroutedToolTipModel(RouteBuilderMapFrameModel routeBuilderMapFrameModel) {
        this.routeBuilderMapFrameModel = routeBuilderMapFrameModel;
        dwellingGroupModel = new DwellingGroupModel(new AlwaysAddOrRemoveListSelectionModel(), routeBuilderMapFrameModel.getApplicationContext());
        routedAndUnroutedToolTipDwellingGroupsUpdatedListeners =
                new ArrayList<RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener>();
    }

    public DwellingGroupModel getDwellingGroupModel() {
        return dwellingGroupModel;
    }

    public void updateDwellingGroups(List<? extends DwellingGroup> dwellingGroups) {
        LOG.debug("Updating Dwelling Groups");
        boolean[] isSelected = new boolean[dwellingGroups.size()];
        for (int i = 0; i < dwellingGroups.size(); i++) {
            isSelected[i] = routeBuilderMapFrameModel.getRoutedDwellingGroups().contains(dwellingGroups.get(i));
        }
        dwellingGroupModel.setToContentsOf(dwellingGroups, isSelected);
        LOG.debug("Updating Dwelling Groups");
        fireDwellingGroupsUpdated();
    }

    public void addRoutedAndUnroutedToolTipDwellingGroupsUpdatedListener(
            RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener l) {
        synchronized (routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
            routedAndUnroutedToolTipDwellingGroupsUpdatedListeners.add(l);
        }
    }

    public void removeRoutedAndUnroutedToolTipDwellingGroupsUpdatedListener(
            RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener l) {
        synchronized (routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
            routedAndUnroutedToolTipDwellingGroupsUpdatedListeners.remove(l);
        }
    }

    protected void fireDwellingGroupsUpdated() {
        RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent e =
                new RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent(this);
        synchronized (routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
            for (RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener l :
                    routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
                l.routedAndUnroutedToolTipDataChanged(e);
            }
        }
    }


    public void invertSelection() {
        if (getDwellingGroupModel().getListSelectionModel().isSelectionEmpty()) {
            getDwellingGroupModel().getListSelectionModel().addSelectionInterval(0, getDwellingGroupModel().getRowCount() - 1);
        }
        else {
            getDwellingGroupModel().getListSelectionModel().clearSelection();
        }
    }
}
