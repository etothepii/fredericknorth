package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.pdf.PDFRenderer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 01/07/2013
 * Time: 21:27
 */
public class RouteBuilderMapFrameModel {

    private static final Logger LOG = Logger.getLogger(RouteBuilderMapFrameModel.class);

    private static final String StationaryMouseRequirementKey = "StationaryMouseRequirement";

    private final MapPanelModel mapPanelModel;
    private final DwellingGroupModel routedDwellingGroups;
    private final DwellingGroupModel unroutedDwellingGroups;
    private final RoutedAndUnroutedToolTipModel routedAndUnroutedToolTipModel;
    private final RoutesModel routesModel;

    private MapViewGenerator mapViewGenerator;
    private boolean dwellingGroupsBeingUpdated = false;
    private final DotFactory dotFactory;
    private final PDFRenderer pdfRenderer;
    private ApplicationContext applicationContext;

    public RouteBuilderMapFrameModel(ApplicationContext applicationContext) {
        this.mapViewGenerator = applicationContext.getDefaultInstance(MapViewGenerator.class);
        this.dotFactory = applicationContext.getDefaultInstance(DotFactory.class);
        this.applicationContext = applicationContext;
        this.pdfRenderer = applicationContext.getDefaultInstance(PDFRenderer.class);
        mapPanelModel = new RouteBuilderMapPanelModel(this, Long.parseLong(applicationContext.getProperty(StationaryMouseRequirementKey)));
        routedDwellingGroups = new DwellingGroupModel(applicationContext);
        unroutedDwellingGroups = new DwellingGroupModel(applicationContext);
        routesModel = new RoutesModel(this);
        routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel(this);
        addListeners();
    }

    private void addListeners() {
        routedDwellingGroups.getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                clearOtherDwellingGroup(unroutedDwellingGroups);
            }
        });
        unroutedDwellingGroups.getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                clearOtherDwellingGroup(routedDwellingGroups);
            }
        });
        mapPanelModel.addMapPanelDataListener(new MapPanelDataAdapter() {
            @Override
            public void overlaysMouseOverChanged(MapPanelDataEvent e) {
                Map<OverlayItem, MouseLocation> overlayItemList = mapPanelModel.getImmutableOverlaysMouseOver();
                List<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>(overlayItemList.size());
                for (Map.Entry<OverlayItem, MouseLocation> entry : overlayItemList.entrySet()) {
                    OverlayItem overlayItem = entry.getKey();
                    if (overlayItem.getItem() instanceof DottedDwellingGroup) {
                        DottedDwellingGroup dottedDwellingGroup = (DottedDwellingGroup) overlayItem.getItem();
                        dwellingGroups.add(dottedDwellingGroup.getDwellingGroup());
                    }
                }
                routedAndUnroutedToolTipModel.updateDwellingGroups(dwellingGroups);
            }
        });
        routedAndUnroutedToolTipModel.getDwellingGroupModel().getListSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            if (routesModel.getSelectedItem() != null) {
                                routesModel.moveInToRoute(
                                        routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.SELECTED));
                                routesModel.moveOutOfRoute(
                                        routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.UNSELECTED));
                                updateOverlays();
                                updateSelectedRouteAndWard();
                            }
                        }
                    }
                });
    }

    private void updateSelectedRouteAndWard() {
        routesModel.updateSelected();
    }

    private void clearOtherDwellingGroup(DwellingGroupModel otherDwellingGroup) {
        if (dwellingGroupsBeingUpdated) return;
        dwellingGroupsBeingUpdated = true;
        otherDwellingGroup.getListSelectionModel().clearSelection();
        dwellingGroupsBeingUpdated = false;
    }

    public DwellingGroupModel getUnroutedDwellingGroups() {
        return unroutedDwellingGroups;
    }

    public DwellingGroupModel getRoutedDwellingGroups() {
        return routedDwellingGroups;
    }

    public RoutesModel getRoutesModel() {
        return routesModel;
    }

    public MapViewGenerator getMapViewGenerator() {
        return mapViewGenerator;
    }

    private List<OverlayItem> getDwellingGroupOverlays() {
        ArrayList<List<DwellingGroup>> dwellingGroupLists = new ArrayList<List<DwellingGroup>>(4);
        dwellingGroupLists.add(unroutedDwellingGroups.getSelected(SelectedState.UNSELECTED));
        dwellingGroupLists.add(routedDwellingGroups.getSelected(SelectedState.UNSELECTED));
        dwellingGroupLists.add(unroutedDwellingGroups.getSelected(SelectedState.SELECTED));
        dwellingGroupLists.add(routedDwellingGroups.getSelected(SelectedState.SELECTED));
        Color[] colors = new Color[] {Color.BLUE, Color.GREEN, Color.RED, Color.RED};
        int count = 0;
        for (List<DwellingGroup> dwellingGroupsList : dwellingGroupLists) {
            count += dwellingGroupsList.size();
        }
        List<OverlayItem> overlayItems = new ArrayList<OverlayItem>(count);
        for (int i = 0; i < dwellingGroupLists.size(); i++) {
            for (DwellingGroup dwellingGroup : dwellingGroupLists.get(i)) {
                overlayItems.add(
                        new DottedDwellingGroupOverlayItemImpl(
                                new DottedDwellingGroup(
                                        dwellingGroup,
                                        dotFactory.getStandardDot(colors[i])),
                                i));
            }
        }
        return overlayItems;
    }

    public void updateOverlays() {
        LOG.debug("Updating dots");
        List<OverlayItem> dwellingGroupOverlays = getDwellingGroupOverlays();
        ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>(dwellingGroupOverlays.size());
        overlayItems.addAll(dwellingGroupOverlays);
        mapPanelModel.setOverlays(overlayItems);
    }

    public void moveSelectedUnroutedDwellingGroupsInToRoute() {
        routesModel.moveInToRoute(unroutedDwellingGroups.getSelected(SelectedState.SELECTED));
        updateOverlays();
    }

    public void moveSelectedRoutedDwellingGroupsOutOfRoute() {
        routesModel.moveOutOfRoute(routedDwellingGroups.getSelected(SelectedState.SELECTED));
        updateOverlays();
    }

    public MapPanelModel getMapPanelModel() {
        return mapPanelModel;
    }

    public RoutedAndUnroutedToolTipModel getRoutedAndUnroutedToolTipModel() {
        return routedAndUnroutedToolTipModel;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void save(File selectedFile) {
        council.save(selectedFile);
    }

    public void load(File selectedFile) {
        council.load(applicationContext, selectedFile);
        wardsModel.refresh();
    }

    public void export(File selectedFile) {
        pdfRenderer.buildRoutesGuide(council, selectedFile);
    }

    public void invertSelectionInRoutedAndUnrouted() {
        routedAndUnroutedToolTipModel.invertSelection();
    }

    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        council.autoGenerate(targetSize, unroutedOnly);
    }
}
