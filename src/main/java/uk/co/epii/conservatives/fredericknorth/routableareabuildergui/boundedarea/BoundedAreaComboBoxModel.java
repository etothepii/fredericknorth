package uk.co.epii.conservatives.fredericknorth.routableareabuildergui.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.routableareabuildergui.SelectedBoundedAreaChangedListener;

import javax.swing.*;
import java.util.Collection;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 23:36
 */
public interface BoundedAreaComboBoxModel extends ComboBoxModel {

    public void setParent(BoundedArea boundedArea);
    public void setParentlessBoundedArea(Collection<? extends BoundedArea> boundedAreas);
    public BoundedArea getSelectedItem();
    public void addSelectedBoundedAreaChangedListener(SelectedBoundedAreaChangedListener l);
    public void removeSelectedBoundedAreaChangedListener(SelectedBoundedAreaChangedListener l);
    public void refresh();
    public BoundedArea getElementAt(int index);
    public void add(BoundedArea boundedArea);
    public void addAll(Collection<? extends BoundedArea> boundedAreas);
}
