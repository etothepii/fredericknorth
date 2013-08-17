package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;
import java.util.EventObject;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 02:12
 */
public class MouseStableEvent extends EventObject {

    private final Point stable;

    public MouseStableEvent(Object source, Point stable) {
        super(source);
        this.stable = stable;
    }

    public Point getStable() {
        return stable;
    }
}
