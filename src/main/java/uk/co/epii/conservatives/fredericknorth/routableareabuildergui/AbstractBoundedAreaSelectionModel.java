package uk.co.epii.conservatives.fredericknorth.routableareabuildergui;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;

import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 27/07/2013
 * Time: 00:21
 */
public abstract class AbstractBoundedAreaSelectionModel implements BoundedAreaSelectionModel {

    private final List<SelectedBoundedAreaChangedListener> selectedBoundedAreaChangedListenerArrayList;

    protected AbstractBoundedAreaSelectionModel() {
        this.selectedBoundedAreaChangedListenerArrayList = new ArrayList<SelectedBoundedAreaChangedListener>();
    }

    @Override
    public void removeBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l) {
        synchronized (selectedBoundedAreaChangedListenerArrayList) {
            selectedBoundedAreaChangedListenerArrayList.remove(l);
        }
    }

    @Override
    public void addBoundedAreaSelectionListener(SelectedBoundedAreaChangedListener l) {
        synchronized (selectedBoundedAreaChangedListenerArrayList) {
            selectedBoundedAreaChangedListenerArrayList.add(l);
        }
    }

    protected void fireMasterParentChangedEvent() {
        SelectedBoundedAreaChangedEvent e = new SelectedBoundedAreaChangedEvent(this, null);
        synchronized (selectedBoundedAreaChangedListenerArrayList) {
            for (SelectedBoundedAreaChangedListener l : selectedBoundedAreaChangedListenerArrayList) {
                l.masterParentSelectionChanged(e);
            }
        }
    }

    protected void fireSelectionChangedEvent(BoundedArea boundedArea) {
        SelectedBoundedAreaChangedEvent e = new SelectedBoundedAreaChangedEvent(this, boundedArea);
        synchronized (selectedBoundedAreaChangedListenerArrayList) {
            for (SelectedBoundedAreaChangedListener l : selectedBoundedAreaChangedListenerArrayList) {
                l.selectionChanged(e);
            }
        }
    }
}
