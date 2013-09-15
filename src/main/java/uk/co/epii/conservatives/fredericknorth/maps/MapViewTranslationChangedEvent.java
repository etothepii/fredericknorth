package uk.co.epii.conservatives.fredericknorth.maps;

import java.util.EventObject;

/**
 * User: James Robinson
 * Date: 14/09/2013
 * Time: 19:46
 */
public class MapViewTranslationChangedEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException
     *          if source is null.
     */
    public MapViewTranslationChangedEvent(MapViewGenerator source) {
        super(source);
    }

    @Override
    public MapViewGenerator getSource() {
        return (MapViewGenerator)super.getSource();
    }

}
