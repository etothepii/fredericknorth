package uk.co.epii.conservatives.fredericknorth.opendata;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:21
 */
public interface PostcodeDatum {
    public String getPostcode();
    public Point getPoint();
    public String getWardCode();
    public int[] getCouncilBandCount();
    public int getDwellingCount();
    public void addHouse(char councilTaxBand);
    public void setWardCode(String wardCode);
    public void setPoint(Point location);
}
