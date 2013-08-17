package uk.co.epii.conservatives.fredericknorth.boundaryline;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 19:41
 */
public interface ExtendableBoundedArea extends BoundedArea {

    public void add(Point p, BoundedArea[] neighbours);
}
