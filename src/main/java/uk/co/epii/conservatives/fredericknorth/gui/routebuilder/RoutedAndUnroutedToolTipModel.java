package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

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
    private static final Logger LOG_SYNC = Logger.getLogger(RoutedAndUnroutedToolTipModel.class.getName().concat("_sync"));

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
        LOG_SYNC.debug("Awaiting routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
        try {
            synchronized (routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
                LOG_SYNC.debug("Received routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
                routedAndUnroutedToolTipDwellingGroupsUpdatedListeners.add(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
        }
    }

    public void removeRoutedAndUnroutedToolTipDwellingGroupsUpdatedListener(
            RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener l) {
        try {
            LOG_SYNC.debug("Awaiting routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
            synchronized (routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
                LOG_SYNC.debug("Received routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
                routedAndUnroutedToolTipDwellingGroupsUpdatedListeners.remove(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
        }
    }

    protected void fireDwellingGroupsUpdated() {
        RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent e =
                new RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent(this);
        LOG_SYNC.debug("Awaiting routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
        try {
            synchronized (routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
                LOG_SYNC.debug("Received routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
                for (RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener l :
                        routedAndUnroutedToolTipDwellingGroupsUpdatedListeners) {
                    l.routedAndUnroutedToolTipDataChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released routedAndUnroutedToolTipDwellingGroupsUpdatedListeners");
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
