package uk.co.epii.conservatives.fredericknorth.gui.meetingpointselector;

import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.AbstractMapPanelModel;

import java.awt.event.MouseEvent;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 00:35
 */
class MapPanelModelImpl extends AbstractMapPanelModel {

    private MeetingPointsModel meetingPointsModel;

    public MapPanelModelImpl(MapViewGenerator mapViewGenerator, MeetingPointsModel meetingPointsModel) {
        super(mapViewGenerator);
        this.meetingPointsModel = meetingPointsModel;
    }

    @Override
    public void doubleClicked(MouseEvent e) {

    }

    @Override
    public void clicked(MouseEvent e) {

    }

    @Override
    public void cancel() {

    }
}
