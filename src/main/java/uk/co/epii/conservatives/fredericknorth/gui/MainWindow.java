package uk.co.epii.conservatives.fredericknorth.gui;

import uk.co.epii.conservatives.fredericknorth.gui.meetingpointselector.MeetingPointSelectorPanel;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.BoundedAreaSelectionPanel;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.RoutableAreaBuilderPanel;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.RouteBuilderPanel;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * User: James Robinson
 * Date: 05/10/2013
 * Time: 12:37
 */
public class MainWindow extends JFrame {

    private final MainWindowModel mainWindowModel;
    private final MeetingPointSelectorPanel meetingPointSelectorPanel;
    private final BoundedAreaSelectionPanel boundedAreaSelectionPanel;
    private final JTabbedPane tabbedPane;
    private final RoutableAreaBuilderPanel routableAreaBuilderPanel;
    private final RouteBuilderPanel routeBuilderPanel;

    public MainWindow(ApplicationContext applicationContext, MainWindowModel mainWindowModel) {
        this.mainWindowModel = mainWindowModel;
        boundedAreaSelectionPanel = new BoundedAreaSelectionPanel(mainWindowModel.getBoundedAreaSelectionModel());
        tabbedPane = new JTabbedPane();
        routableAreaBuilderPanel = new RoutableAreaBuilderPanel(applicationContext, mainWindowModel.getRoutableAreaBuilderPanelModel());
        tabbedPane.add("Areas", routableAreaBuilderPanel);
        routeBuilderPanel = new RouteBuilderPanel(mainWindowModel.getRouteBuilderPanelModel(), applicationContext);
        tabbedPane.add("Routes", routeBuilderPanel);
        meetingPointSelectorPanel = new MeetingPointSelectorPanel(mainWindowModel.getMeetingPointSelectorPanelModel(), applicationContext);
        tabbedPane.add("Meeting Points", meetingPointSelectorPanel);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(boundedAreaSelectionPanel, new GridBagConstraints(0, 0, 1, 1, 1d, 0d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        getContentPane().add(tabbedPane, new GridBagConstraints(0, 1, 1, 1, 1d, 1d,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        tabbedPane.addChangeListener(mainWindowModel);
        mainWindowModel.setTab(0, mainWindowModel.getRoutableAreaBuilderPanelModel());
        mainWindowModel.setTab(1, mainWindowModel.getRouteBuilderPanelModel());
        mainWindowModel.setTab(2, mainWindowModel.getMeetingPointSelectorPanelModel());
        mainWindowModel.stateChanged(new ChangeEvent(tabbedPane));
    }


}
