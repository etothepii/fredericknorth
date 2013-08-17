package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import javax.swing.*;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 16:31
 */
class AlwaysAddOrRemoveListSelectionModel extends DefaultListSelectionModel {

    boolean gestureStarted = false;

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if(!gestureStarted){
            if (isSelectedIndex(index0)) {
                super.removeSelectionInterval(index0, index1);
            } else {
                super.addSelectionInterval(index0, index1);
            }
        }
        gestureStarted = true;
    }

    @Override
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (!isAdjusting) {
            gestureStarted = false;
        }
        super.setValueIsAdjusting(isAdjusting);
    }

    @Override
    public boolean getValueIsAdjusting() {
        return super.getValueIsAdjusting() || gestureStarted;
    }

}
