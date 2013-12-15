package uk.co.epii.conservatives.fredericknorth.gui;

import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.BoundedAreaSelectionModel;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.DefaultBoundedAreaSelectionModel;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.RoutableAreaBuilderPanelModel;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.RouteBuilderMapPanelModel;
import uk.co.epii.conservatives.fredericknorth.gui.routebuilder.RouteBuilderPanelModel;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 05/10/2013
 * Time: 12:48
 */
public class MainWindowModel implements ChangeListener {

    private final BoundedAreaSelectionModel boundedAreaSelectionModel;
    private final RouteBuilderPanelModel routeBuilderPanelModel;
    private final RoutableAreaBuilderPanelModel routableAreaBuilderPanelModel;
    private final Map<Integer, Activateable> tabs;
    private int activeTab;

    public MainWindowModel(ApplicationContext applicationContext) {
        boundedAreaSelectionModel = new DefaultBoundedAreaSelectionModel(applicationContext);
        routableAreaBuilderPanelModel = new RoutableAreaBuilderPanelModel(applicationContext, boundedAreaSelectionModel) ;
        routeBuilderPanelModel = new RouteBuilderPanelModel(applicationContext, boundedAreaSelectionModel);
        tabs = new HashMap<Integer, Activateable>();
        activeTab = -1;
    }

    public BoundedAreaSelectionModel getBoundedAreaSelectionModel() {
        return boundedAreaSelectionModel;
    }

    public RouteBuilderPanelModel getRouteBuilderPanelModel() {
        return routeBuilderPanelModel;
    }

    public RoutableAreaBuilderPanelModel getRoutableAreaBuilderPanelModel() {
        return routableAreaBuilderPanelModel;
    }

    public void setTab(int index, Activateable tab) {
        tabs.put(index, tab);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if (activeTab != index) {
            Activateable tab = tabs.get(activeTab);
            if (tab != null) {
                tab.setActive(false);
            }
            activeTab = index;
            tab = tabs.get(activeTab);
            if (tab != null) {
                tab.setActive(true);
            }
        }

    }
}
