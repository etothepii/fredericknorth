package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import java.util.EventObject;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 10:38
 */
class RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent extends EventObject {

    public RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent(RoutedAndUnroutedToolTipModel source) {
        super(source);
    }

    @Override
    public RoutedAndUnroutedToolTipModel getSource() {
        return (RoutedAndUnroutedToolTipModel)source;
    }
}
