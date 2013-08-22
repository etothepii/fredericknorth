package uk.co.epii.conservatives.fredericknorth.routableareabuildergui;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;

import java.util.EventObject;

/**
 * User: James Robinson
 * Date: 27/07/2013
 * Time: 00:19
 */
public class SelectedBoundedAreaChangedEvent extends EventObject {

    private BoundedArea selected;

    public SelectedBoundedAreaChangedEvent(Object source, BoundedArea selected) {
        super(source);
        this.selected = selected;
    }

    public BoundedArea getSelected() {
        return selected;
    }
}
