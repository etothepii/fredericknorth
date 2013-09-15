package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 14/09/2013
 * Time: 11:25
 */
public abstract class ReusableOverlay extends Component {

    public abstract void setMouseLocation(Point p);
    public abstract boolean isStillUsable();

}
