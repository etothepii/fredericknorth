package uk.co.epii.conservatives.fredericknorth.maps.gui;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 09:49
 */
public interface MapPanelDataListener {

    public void mapChanged(MapPanelDataEvent e);
    public void overlaysChanged(MapPanelDataEvent e);
    public void overlaysMouseOverChanged(MapPanelDataEvent e);
    public void universeChanged(MapPanelDataEvent e);

}
