package uk.co.epii.conservatives.fredericknorth.gui;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: James Robinson
 * Date: 05/10/2013
 * Time: 22:25
 */
public class DummyMouseEvent extends MouseEvent {

    private final Point point;

    public DummyMouseEvent(Point point) {
        super(new Panel(), 0, 0, 0, 0, 0, 0, false, 0);
        this.point = point;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public int getY() {
        return point.y;
    }

    @Override
    public int getX() {
        return point.x;
    }
}
