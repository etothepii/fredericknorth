package uk.co.epii.conservatives.fredericknorth.boundaryline;

import java.awt.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 31/07/2013
 * Time: 19:41
 */
public interface ExtendableBoundedArea extends BoundedArea {

    public void addCurrent();
    public void setCurrent(Point p, BoundedArea[] neighbours);
    public List<Point> getPointsToDraw();

}
