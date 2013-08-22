package uk.co.epii.conservatives.fredericknorth.routableareabuildergui.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.routableareabuildergui.SelectedBoundedAreaChangedListener;
import uk.co.epii.conservatives.fredericknorth.routableareabuildergui.SelectedBoundedAreaChangedEvent;

import javax.swing.*;
import java.util.*;

/**
 * User: James Robinson
 * Date: 28/07/2013
 * Time: 23:40
 */
class BoundedAreaComboBoxModelImpl extends DefaultComboBoxModel implements BoundedAreaComboBoxModel {

    private final Comparator<BoundedArea> alphabeticalComparator = new Comparator<BoundedArea>() {
        @Override
        public int compare(BoundedArea a, BoundedArea b) {
            return a.getName().compareTo(b.getName());
        }
    };
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
        addAllInternal(children);
        Collections.sort(this.children, alphabeticalComparator);
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

    private void addChild(BoundedArea boundedArea) {
        int index = Collections.binarySearch(children, boundedArea, alphabeticalComparator);
        if (index >= 0) {
            children.add(index, boundedArea);
        }
        else {
            children.add(~index, boundedArea);
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
        if (boundedArea.getBoundedAreaType() != childType) {
            throw new IllegalArgumentException("You can only add a BoundedArea if it is of the type expected of the ComboBoxModel");
        }
        if (parent != null) {
            throw new UnsupportedOperationException("You can only add a BoundedArea if the parent is null");
        }
        addChild(boundedArea);
        fireContentsChanged(this, 0, getSize());
    }

    @Override
    public void addAll(Collection<? extends BoundedArea> boundedAreas) {
        if (parent != null) {
            throw new UnsupportedOperationException("You can only add a BoundedArea if the parent is null");
        }
        addAllInternal(boundedAreas);
    }

    private void addAllInternal(Collection<? extends BoundedArea> boundedAreas) {
        for (BoundedArea boundedArea : boundedAreas) {
            if (boundedArea.getBoundedAreaType() != childType) {
                throw new IllegalArgumentException("You can only add a BoundedArea if it is of the type expected of the ComboBoxModel");
            }
        }
        children.addAll(boundedAreas);
        Collections.sort(children, alphabeticalComparator);
        fireContentsChanged(this, 0, getSize());
    }
}
