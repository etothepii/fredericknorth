package uk.co.epii.conservatives.fredericknorth.gui.meetingpointselector;

import uk.co.epii.conservatives.fredericknorth.gui.Activateable;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelModel;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerJProgressBar;

/**
 * User: James Robinson
 * Date: 21/02/2014
 * Time: 19:58
 */
public class MeetingPointSelectorPanelModel implements Activateable {

    private MeetingPointsModel meetingPointsModel;
    private MapPanelModel mapPanelModel;

    public MeetingPointSelectorPanelModel(ApplicationContext applicationContext) {
        meetingPointsModel = new MeetingPointsModel();
        mapPanelModel = new MapPanelModelImpl(
                applicationContext.getDefaultInstance(MapViewGenerator.class), meetingPointsModel);
    }

    public MapPanelModel getMapPanelModel() {
        return mapPanelModel;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public boolean getActive() {
        return false;
    }

    public MeetingPointsModel getMeetingPointsModel() {
        return meetingPointsModel;
    }

    public void setProgressTracker(ProgressTracker progressTracker) {
        mapPanelModel.setProgressTracker(progressTracker);
    }
}
