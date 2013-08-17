package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.SelectedBoundedAreaChangedEvent;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.SelectedBoundedAreaChangedListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 28/07/2013
 * Time: 23:40
 */
class BoundedAreaComboBoxModelImpl extends DefaultComboBoxModel implements BoundedAreaComboBoxModel {

    private final BoundedAreaType childType;
    private BoundedArea parent;
    private BoundedArea selected;
    private final List<BoundedArea> children;
    private final List<SelectedBoundedAreaChangedListener> selectedBoundedAreaChangedListeners;

    public BoundedAreaComboBoxModelImpl(BoundedAreaType childType, BoundedArea parent) {
        this.childType = childType;
        children = new ArrayList<BoundedArea>();
        selectedBoundedAreaChangedListeners  = new ArrayList<SelectedBoundedAreaChangedListener>();
        setParent(parent);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem == null || anItem instanceof BoundedArea) {
            setSelectedItem((BoundedArea)anItem);
        }
        else {
            throw new IllegalArgumentException("You can not provide an argument of type BoundedArea");
        }
    }

    public void setParent(BoundedArea parent) {
        this.parent = parent;
        setChildren(parent == null ? new ArrayList<BoundedArea>() : Arrays.asList(parent.getChildren()));
    }

    private void setChildren(Collection<BoundedArea> children) {
        setSelectedItem(null);
        int oldSize = getSize();
        this.children.clear();
        for (BoundedArea boundedArea : children) {
            if (boundedArea.getBoundedAreaType() == childType) {
                this.children.add(boundedArea);
            }
        }
        int size = getSize();
        if (size > 1) {
            int to = Math.min(oldSize, getSize());
            if (to > 1) {
                fireContentsChanged(this, 1, to - 1);
            }
            if (oldSize < size) {
                fireIntervalAdded(this, to, size - 1);
            }
            else if (oldSize > size) {
                fireIntervalRemoved(this, to, oldSize - 1);
            }
        }
    }

    private void setSelectedItem(BoundedArea boundedArea) {
        selected = boundedArea;
        fireContentsChanged(this, -1, -1);
        fireSelectedBoundedAreaChanged();
    }

    private void fireSelectedBoundedAreaChanged() {
        SelectedBoundedAreaChangedEvent e = new SelectedBoundedAreaChangedEvent(this, getSelectedItem());
        synchronized (selectedBoundedAreaChangedListeners) {
            for (SelectedBoundedAreaChangedListener listener : selectedBoundedAreaChangedListeners) {
                listener.selectionChanged(e);
            }
        }
    }

    @Override
    public BoundedArea getSelectedItem() {
        return selected;
    }

    @Override
    public void addSelectedBoundedAreaChangedListener(SelectedBoundedAreaChangedListener l) {
        synchronized (selectedBoundedAreaChangedListeners) {
            selectedBoundedAreaChangedListeners.add(l);
        }
    }

    @Override
    public void removeSelectedBoundedAreaChangedListener(SelectedBoundedAreaChangedListener l) {
        synchronized (selectedBoundedAreaChangedListeners) {
            selectedBoundedAreaChangedListeners.remove(l);
        }
    }

    @Override
    public void refresh() {
        setParent(parent);
    }

    @Override
    public int getSize() {
        return children.size() + 1;
    }

    @Override
    public BoundedArea getElementAt(int index) {
        if (index == 0) return null;
        return children.get(index - 1);
    }

    @Override
    public void setParentlessBoundedArea(Collection<? extends BoundedArea> boundedAreas) {
        List<BoundedArea> parentlessBoundedAreas = new ArrayList<BoundedArea>(boundedAreas.size());
        parentlessBoundedAreas.addAll(boundedAreas);
        setChildren(parentlessBoundedAreas);
    }

    @Override
    public void add(BoundedArea boundedArea) {
        if (parent == null) {
            children.add(boundedArea);
            fireContentsChanged(this, 0, getSize());
        }
        else {
            throw new UnsupportedOperationException("You can only add a BoundedArea if the parent is null");
        }
    }
}
