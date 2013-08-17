package uk.co.epii.conservatives.fredericknorth.utilities;

import java.util.EventObject;

/**
 * User: James Robinson
 * Date: 17/08/2013
 * Time: 14:17
 */
public class EnabledStateChangedEvent<T> extends EventObject {

    private final boolean enabled;

    public EnabledStateChangedEvent(T o, boolean enabled) {
        super(o);
        this.enabled = enabled;
    }

    @Override
    public T getSource() {
        return (T)super.getSource();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
