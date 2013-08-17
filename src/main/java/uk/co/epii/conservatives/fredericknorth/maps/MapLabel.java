package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 13:55
 */
public interface MapLabel {

    public String getName();
    public Rectangle getRectangle();
    public Corner getCorner();
    public void paint(Graphics g);

}
