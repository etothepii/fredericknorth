package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;

import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 27/07/2013
 * Time: 00:21
 */
public abstract class AbstractBoundedAreaSelectionModel implements BoundedAreaSelectionModel {

    private static final Logger LOG_SYNC =
            LoggerFactory.getLogger(AbstractBoundedAreaSelectionModel.class.getName().concat("_sync"));

    private final List<SelectedBoundedAreaChangedListener> selectedBoundedAreaChangedListenerArrayList;

    protected AbstractBoundedAreaSelectionModel() {
        this.selectedBoundedAreaChangedListenerArrayList = new ArrayList<SelectedBoundedAreaChangedListener>();
    }

    @Override
    public void removeBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l) {
        LOG_SYNC.debug("Awaiting selectedBoundedAreaChangedListenerArrayList");
        try {
            synchronized (selectedBoundedAreaChangedListenerArrayList) {
                LOG_SYNC.debug("Received selectedBoundedAreaChangedListenerArrayList");
                selectedBoundedAreaChangedListenerArrayList.remove(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released selectedBoundedAreaChangedListenerArrayList");
        }
    }

    @Override
    public void addBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l) {
        LOG_SYNC.debug("Awaiting selectedBoundedAreaChangedListenerArrayList");
        try {
            synchronized (selectedBoundedAreaChangedListenerArrayList) {
                LOG_SYNC.debug("Received selectedBoundedAreaChangedListenerArrayList");
                selectedBoundedAreaChangedListenerArrayList.add(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released selectedBoundedAreaChangedListenerArrayList");
        }
    }

    protected void fireMasterParentChangedEvent() {
        SelectedBoundedAreaChangedEvent e = new SelectedBoundedAreaChangedEvent(this, null);
        LOG_SYNC.debug("Awaiting selectedBoundedAreaChangedListenerArrayList");
        try {
            synchronized (selectedBoundedAreaChangedListenerArrayList) {
                LOG_SYNC.debug("Received selectedBoundedAreaChangedListenerArrayList");
                for (SelectedBoundedAreaChangedListener l : selectedBoundedAreaChangedListenerArrayList) {
                    l.masterParentSelectionChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released selectedBoundedAreaChangedListenerArrayList");
        }
    }

    protected void fireSelectionChangedEvent(BoundedArea boundedArea) {
        SelectedBoundedAreaChangedEvent e = new SelectedBoundedAreaChangedEvent(this, boundedArea);
        LOG_SYNC.debug("Awaiting selectedBoundedAreaChangedListenerArrayList");
        try {
            synchronized (selectedBoundedAreaChangedListenerArrayList) {
                LOG_SYNC.debug("Received selectedBoundedAreaChangedListenerArrayList");
                for (SelectedBoundedAreaChangedListener l : selectedBoundedAreaChangedListenerArrayList) {
                    l.selectionChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released selectedBoundedAreaChangedListenerArrayList");
        }
    }
}
