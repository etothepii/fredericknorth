package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 24/06/2013
 * Time: 08:50
 */
public interface OSMap {

    public OSMapType getOSMapType();
    public String getLargeSquare();
    public Integer getSquare();
    public String getQuadrant();
    public Integer getSquareHundredth();
    public Integer getQuadrantHundredth();
    public String getMapName();
}
