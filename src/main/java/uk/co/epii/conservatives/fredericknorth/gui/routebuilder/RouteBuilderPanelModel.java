package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.PolygonSifter;
import uk.co.epii.conservatives.fredericknorth.geometry.SquareSearchPolygonSifterImpl;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.gui.Activateable;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.*;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupFactory;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.pdf.RenderedRoute;
import uk.co.epii.conservatives.fredericknorth.routes.DefaultRoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.utilities.*;
import uk.co.epii.conservatives.fredericknorth.maps.gui.*;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.pdf.PDFRenderer;
import uk.co.epii.politics.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.politics.williamcavendishbentinck.tables.Leaflet;
import uk.co.epii.politics.williamcavendishbentinck.tables.LeafletMap;
import uk.co.epii.politics.williamcavendishbentinck.tables.Route;
import uk.co.epii.politics.williamcavendishbentinck.tables.RouteMember;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: James Robinson
 * Date: 01/07/2013
 * Time: 21:27
 */
public class RouteBuilderPanelModel implements Activateable {

    private static final Logger LOG = LoggerFactory.getLogger(RouteBuilderPanelModel.class);
    private static final Logger LOG_SYNC =
            LoggerFactory.getLogger(RouteBuilderPanelModel.class.getName().concat("_sync"));

    public static final String StationaryMouseRequirementKey = "StationaryMouseRequirement";

    private final MapPanelModel mapPanelModel;
    private final DwellingGroupModel routedDwellingGroups;
    private final DwellingGroupModel unroutedDwellingGroups;
    private final RoutedAndUnroutedToolTipModel routedAndUnroutedToolTipModel;
    private final RoutesModel routesModel;
    private final HashMap<BoundedArea, RoutableArea> routableAreas;
    private final BoundedAreaSelectionModel boundedAreaSelectionModel;
    private final DwellingGroupFactory dwellingGroupFactory;
    private final Executor executor;
    private final XMLSerializer xmlSerializer;
    private final SelectedBoundedAreaChangedListener selectedBoundedAreaChangedListener;

    private MapViewGenerator mapViewGenerator;
    private boolean dwellingGroupsBeingUpdated = false;
    private final DotFactory dotFactory;
    private final PDFRenderer pdfRenderer;
    private ApplicationContext applicationContext;
    private ProgressTracker progressTracker;
    private final Object enabledSync = new Object();
    private boolean enabled = true;
    private final ArrayList<EnabledStateChangedListener<RouteBuilderPanelModel>> enabledStateChangedListeners =
            new ArrayList<EnabledStateChangedListener<RouteBuilderPanelModel>>();
    private boolean active = false;

    public RouteBuilderPanelModel(ApplicationContext applicationContext, BoundedAreaSelectionModel boundedAreaSelectionModel) {
        this(applicationContext, boundedAreaSelectionModel, new HashMap<BoundedArea, RoutableArea>());
    }

    RouteBuilderPanelModel(ApplicationContext applicationContext, BoundedAreaSelectionModel boundedAreaSelectionModel,
                           HashMap<BoundedArea, RoutableArea> routableAreas) {
        xmlSerializer = applicationContext.getDefaultInstance(XMLSerializer.class);
        this.boundedAreaSelectionModel = boundedAreaSelectionModel;
        executor = Executors.newSingleThreadExecutor();
        progressTracker = new NullProgressTracker();
        this.routableAreas = routableAreas;
        this.mapViewGenerator = applicationContext.getDefaultInstance(MapViewGenerator.class);
        this.dotFactory = applicationContext.getDefaultInstance(DotFactory.class);
        this.dwellingGroupFactory = applicationContext.getDefaultInstance(DwellingGroupFactory.class);
        this.applicationContext = applicationContext;
        this.pdfRenderer = applicationContext.getDefaultInstance(PDFRenderer.class);
        routedDwellingGroups = new DwellingGroupModel(applicationContext);
        unroutedDwellingGroups = new DwellingGroupModel(applicationContext);
        routesModel = new RoutesModel(this);
        routedAndUnroutedToolTipModel = new RoutedAndUnroutedToolTipModel(this);
        mapPanelModel = new RouteBuilderMapPanelModel(this,
                Long.parseLong(applicationContext.getProperty(RouteBuilderPanelModel.StationaryMouseRequirementKey)));
        boundedAreaSelectionModel.loadOSKnownInstances();
        selectedBoundedAreaChangedListener = createSelectedBoundedAreaChangedListener();
        addListeners();
    }

    private SelectedBoundedAreaChangedListener createSelectedBoundedAreaChangedListener() {
        return new SelectedBoundedAreaChangedListener() {
            @Override
            public void masterParentSelectionChanged(SelectedBoundedAreaChangedEvent e) {
                setSelectedBoundedArea(boundedAreaSelectionModel.getSelected());
            }

            @Override
            public void selectionChanged(SelectedBoundedAreaChangedEvent e) {
                setSelectedBoundedArea(boundedAreaSelectionModel.getSelected());
            }
        };
    }

    private void addListeners() {
        mapPanelModel.addMapPanelDataListener(new MapPanelDataAdapter() {
            @Override
            public void overlaysMouseOverChanged(MapPanelDataEvent e) {
                Map<OverlayItem, MouseLocation> overlayItemList = mapPanelModel.getImmutableOverlaysMouseOver();
                List<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>(overlayItemList.size());
                for (Map.Entry<OverlayItem, MouseLocation> entry : overlayItemList.entrySet()) {
                    OverlayItem overlayItem = entry.getKey();
                    if (overlayItem.getItem() instanceof DwellingGroup) {
                        DwellingGroup dottedDwellingGroup = (DwellingGroup) overlayItem.getItem();
                        dwellingGroups.add(dottedDwellingGroup);
                    }
                }
                routedAndUnroutedToolTipModel.updateDwellingGroups(dwellingGroups);
            }
        });
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

    void setSelectedBoundedArea(BoundedArea boundedArea) {
        setSelectedBoundedArea(boundedArea, false);
    }

    void setSelectedBoundedArea(final BoundedArea boundedArea, boolean force) {
        if (!force && boundedAreaSelectionModel.getSelected() != boundedArea) {
            return;
        }
        disable();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setSelectedBoundedAreaOnThread(boundedArea);
            }
        });
    }

    private void setSelectedBoundedAreaOnThread(BoundedArea boundedArea) {
        final RoutableArea routableArea = getRoutableArea(boundedArea);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LOG.debug("Setting selected routable area: {}", routableArea == null ? "null" : routableArea.getName());
                routesModel.setSelectedRoutableArea(routableArea);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (routableArea == null) {
                            enable();
                            return;
                        }
                        Rectangle bounds = PolygonExtensions.getBounds(routableArea.getBoundedArea().getAreas());
                        LOG.debug("Setting universe");
                        mapPanelModel.display(new Rectangle(bounds.x - bounds.width / 10, bounds.y - bounds.height / 10,
                                bounds.width * 6 / 5, bounds.height * 6 / 5), true);
                        LOG.debug("Set universe");
                    }
                });
                updateOverlays();
            }
        });
    }

    public void disable() {
        LOG_SYNC.debug("Awaiting enabledSync");
        try {
            synchronized (enabledSync) {
                LOG_SYNC.debug("Received enabledSync");
                if (enabled) {
                    enabled = false;
                    fireEnabledStateChanged();
                }
            }
        }
        finally {
            LOG_SYNC.debug("Realeased enabledSync");
        }
    }

    private void fireEnabledStateChanged() {
        LOG_SYNC.debug("Awaiting enabledStateChangedListeners");
        try {
            synchronized (enabledStateChangedListeners) {
                LOG_SYNC.debug("Received enabledStateChangedListeners");
                EnabledStateChangedEvent<RouteBuilderPanelModel> e =
                        new EnabledStateChangedEvent<RouteBuilderPanelModel>(this, isEnabled());
                for (EnabledStateChangedListener l : enabledStateChangedListeners) {
                    l.enabledStateChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Realeased enabledStateChangedListeners");
        }
    }

    public boolean isEnabled() {
        LOG_SYNC.debug("Awaiting enabledSync");
        try {
            synchronized (enabledSync) {
                LOG_SYNC.debug("Received enabledSync");
                return enabled;
            }
        }
        finally {
            LOG_SYNC.debug("Realeased enabledSync");
        }
    }

    public void addEnableStateChangedListener(EnabledStateChangedListener<RouteBuilderPanelModel> l) {
        LOG_SYNC.debug("Awaiting enabledStateChangedListeners");
        try {
            synchronized (enabledStateChangedListeners) {
                LOG_SYNC.debug("Received enabledStateChangedListeners");
                enabledStateChangedListeners.add(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released enabledStateChangedListeners");
        }
    }

    public void removeEnableStateChangedListener(EnabledStateChangedListener<RouteBuilderPanelModel> l) {
        LOG_SYNC.debug("Awaiting enabledStateChangedListeners");
        try {
            synchronized (enabledStateChangedListeners) {
                LOG_SYNC.debug("Received enabledStateChangedListeners");
                enabledStateChangedListeners.remove(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released enabledStateChangedListeners");
        }
    }

    public void enable() {
        LOG_SYNC.debug("Awaiting enabledSync");
        try {
            synchronized (enabledSync) {
                LOG_SYNC.debug("Received enabledSync");
                if (!enabled) {
                    enabled = true;
                    fireEnabledStateChanged();
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released enabledSync");
        }
    }

    public BoundedAreaSelectionModel getBoundedAreaSelectionModel() {
        return boundedAreaSelectionModel;
    }

    private void updateSelectedRouteAndWard() {
        routesModel.updateSelected();
    }

    public RoutableArea getRoutableArea(BoundedArea boundedArea) {
        if (boundedArea == null) {
            return null;
        }
        RoutableArea routableArea = routableAreas.get(boundedArea);
        if (routableArea == null) {
            loadRoutableArea(boundedArea);
            routableArea = routableAreas.get(boundedArea);
        }
        return routableArea;
    }

    private void loadRoutableArea(BoundedArea boundedArea) {
        Map<BoundedAreaType, BoundedArea> selected = boundedAreaSelectionModel.getAllSelected();
        if (selected.get(boundedArea.getBoundedAreaType()) != boundedArea) {
            throw new IllegalArgumentException("The boundedArea supplied is not selected");
        }
        List<BoundedArea> ancestors = new ArrayList<BoundedArea>();
        BoundedAreaType boundedAreaType = boundedAreaSelectionModel.getMasterSelectedType();
        do {
            ancestors.add(selected.get(boundedAreaType));
        } while (boundedAreaType != boundedArea.getBoundedAreaType() &&
                (boundedAreaType = boundedAreaType.getChildType()) != null);
        ancestors.add(boundedArea);
        loadRoutableAreas(ancestors);
    }

    private void loadRoutableAreas(List<BoundedArea> ancestors) {
        RoutableArea parent = null;
        for (int i = 0; i < ancestors.size(); i++) {
            BoundedArea boundedArea = ancestors.get(i);
            LOG.debug("ancestor({}) == null: {}", new Object[] {i, boundedArea == null});
            LOG.debug("ancestor({}): {} {}", new Object[] {i, boundedArea.getBoundedAreaType(),
                    boundedArea.getName() == null ? "Unnamed" : boundedArea.getName()});
            progressTracker.setMessage(String.format("Loading %s", boundedArea.getName()));
            RoutableArea routableArea = routableAreas.get(boundedArea);
            if (routableArea == null) {
                routableArea = loadRoutableArea(boundedArea, parent);
                routableAreas.put(boundedArea, routableArea);
            }
            parent = routableArea;
        }
        if (progressTracker.isAtEnd()) {
            progressTracker.finish();
        }
    }

    private RoutableArea loadRoutableArea(BoundedArea boundedArea, RoutableArea parent) {
        LOG.debug("loadRoutableArea: boundedArea {}, parent {}", new Object[] {boundedArea.getName(), parent == null ?
                "null" : parent.getBoundedArea().getName()});
        DefaultRoutableArea routableArea = new DefaultRoutableArea(boundedArea, parent);
        progressTracker.startSubsection(2);
        if (parent != null) {
            if (parent.getUnroutedDwellingGroups().isEmpty() && parent.getRoutedDwellingGroups().isEmpty()) {
                progressTracker.increment();
            }
            else {
                int total = parent.getUnroutedDwellingGroups().size() +
                        parent.getRoutedDwellingGroups().size();
                progressTracker.startSubsection(total);
                int count = 0;
                PolygonSifter polygonSifter = new SquareSearchPolygonSifterImpl(boundedArea.getAreas(),
                        parent.getUnroutedDwellingGroups().size());
                for (DwellingGroup dwellingGroup : parent.getUnroutedDwellingGroups()) {
                    if (polygonSifter.contains(dwellingGroup.getPoint())) {
                        routableArea.addDwellingGroup(dwellingGroup, false);
                    }
                    progressTracker.increment(routableArea.getName() +": " + 100 * count++ / total + "%");
                }
                for (DwellingGroup dwellingGroup : parent.getRoutedDwellingGroups()) {
                    if (polygonSifter.contains(dwellingGroup.getPoint())) {
                        routableArea.addDwellingGroup(dwellingGroup, true);
                    }
                    progressTracker.increment(routableArea.getName() +": " + 100 * count++ / total + "%");
                }
            }
        }
        else {
            Rectangle bounds = PolygonExtensions.getBounds(boundedArea.getAreas());
            Collection<? extends DwellingGroup> dwellingGroups = dwellingGroupFactory.getDwellingGroups(bounds);
            if (!dwellingGroups.isEmpty()) {
                progressTracker.startSubsection(dwellingGroups.size());
                PolygonSifter polygonSifter = new SquareSearchPolygonSifterImpl(boundedArea.getAreas(), dwellingGroups.size());
                int count = 0;
                for (DwellingGroup dwellingGroup : dwellingGroups) {
                    if (dwellingGroup.getPoint() != null && polygonSifter.contains(dwellingGroup.getPoint())) {
                        routableArea.addDwellingGroup(dwellingGroup, false);
                    }
                    count++;
                    progressTracker.increment(routableArea.getName() +": " + 100 * count / dwellingGroups.size() + "%");
                }
            }
        }
        BoundedArea[] children = boundedArea.getChildren();
        if (children.length > 0) {
            progressTracker.startSubsection(children.length);
            for (BoundedArea child : children) {
                RoutableArea childRoutableArea = loadRoutableArea(child, routableArea);
                routableAreas.put(child, childRoutableArea);
                routableArea.addChild(childRoutableArea);
            }
        }
        else {
            progressTracker.increment();
        }
        return routableArea;
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
        LOG.debug("Getting Unselected Unrouted DwellingGroups");
        dwellingGroupLists.add(unroutedDwellingGroups.getSelected(SelectedState.UNSELECTED));
        LOG.debug("Getting Unselected Routed DwellingGroups");
        dwellingGroupLists.add(routedDwellingGroups.getSelected(SelectedState.UNSELECTED));
        LOG.debug("Getting Selected Unrouted DwellingGroups");
        dwellingGroupLists.add(unroutedDwellingGroups.getSelected(SelectedState.SELECTED));
        LOG.debug("Getting Selected Routed DwellingGroups");
        dwellingGroupLists.add(routedDwellingGroups.getSelected(SelectedState.SELECTED));
        int count = 0;
        for (List<DwellingGroup> dwellingGroupsList : dwellingGroupLists) {
            count += dwellingGroupsList.size();
        }
        List<OverlayItem> overlayItems = new ArrayList<OverlayItem>(count);
        for (int i = 0; i < dwellingGroupLists.size(); i++) {
            LOG.debug("Creating DwellingGroup Overlays");
            for (DwellingGroup dwellingGroup : dwellingGroupLists.get(i)) {
                overlayItems.add(
                        new DottedDwellingGroupOverlayItemImpl(dwellingGroup, i));
            }
            LOG.debug("Created DwellingGroup Overlays");
        }
        return overlayItems;
    }

    public void updateOverlays() {
        LOG.debug("Updating dots");
        List<OverlayItem> dwellingGroupOverlays = getDwellingGroupOverlays();
        ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>(dwellingGroupOverlays.size());
        overlayItems.addAll(dwellingGroupOverlays);
        for (MeetingPoint meetingPoint : boundedAreaSelectionModel.getMeetingPoints()) {
            overlayItems.add(new MeetingPointOverlayItem(meetingPoint));
        }
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
        getRoutableArea(getBoundedAreaSelectionModel().getMasterSelected()).save(selectedFile);
    }

    public void load(File selectedFile) {
        getRoutableArea(getBoundedAreaSelectionModel().getMasterSelected()).load(
                xmlSerializer.fromFile(selectedFile).getDocumentElement());
        setSelectedBoundedArea(boundedAreaSelectionModel.getSelected(), true);
    }

    public void export(final File selectedFile, final DistributionModel distributionModel) {
        synchronized (pdfRenderer) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (pdfRenderer) {
                        pdfRenderer.setMeetingPoints(boundedAreaSelectionModel.getMeetingPoints());
                        Collection<RenderedRoute> renderedRoutes =
                                pdfRenderer.buildRoutesGuide(getRoutableArea(boundedAreaSelectionModel.getSelected()),
                                        selectedFile, progressTracker);
                        uploadRenderedRoutes(progressTracker, distributionModel, renderedRoutes);
                        enable();
                    }
                }
            });
            disable();
        }
    }

  private void uploadRenderedRoutes(ProgressTracker progressTracker, DistributionModel distributionModel, Collection<RenderedRoute> renderedRoutes) {
    if (distributionModel == null) {
      progressTracker.increment();
      return;
    }
    DatabaseSession databaseSession = applicationContext.getDefaultInstance(DatabaseSession.class);
    Leaflet leaflet = new Leaflet(UUID.randomUUID().toString(),
            new java.sql.Date(distributionModel.getDistributionStart().getTime()),
            distributionModel.getTitle(),
            distributionModel.getDescription());
    databaseSession.upload(Arrays.asList(leaflet));
    progressTracker.startSubsection(renderedRoutes.size());
    for (RenderedRoute renderedRoute : renderedRoutes) {
      uploadRenderedRoute(databaseSession, leaflet, renderedRoute);
      progressTracker.increment();
    }
  }

  private void uploadRenderedRoute(DatabaseSession databaseSession, Leaflet leaflet, RenderedRoute renderedRoute) {
    Route route = getRouteFromDatabase(databaseSession, renderedRoute.getRoute());
    LeafletMap leafletMap = new LeafletMap(
            renderedRoute.getUUID().toString(), leaflet.getId(), route.getId(), null, null);
    databaseSession.upload(Arrays.asList(leafletMap));
  }

  private Route getRouteFromDatabase(DatabaseSession databaseSession, uk.co.epii.conservatives.fredericknorth.routes.Route route) {
    List<Route> routes = databaseSession.getByUuid(Route.class, route.getUuid());
    if (routes.size() > 0) {
      return routes.get(0);
    }
    uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea parent =
            getBoundedArea(databaseSession, route.getRoutableArea().getBoundedArea());
    Route databaseRoute = new Route(route.getUuid().toString(), route.getName(), parent.getId(), getOwner(), getOwnerGroup(),
            getDeliveredBy(), null);
    databaseSession.upload(Arrays.asList(databaseRoute));
    List<RouteMember> routeMembers = new ArrayList<RouteMember>(route.getDwellingCount());
    for (DwellingGroup dwellingGroup : route.getDwellingGroups()) {
      for (Location location : dwellingGroup.getDwellings()) {
        if (location instanceof DwellingDatabaseImpl) {
          DwellingDatabaseImpl dwelling = (DwellingDatabaseImpl)location;
          routeMembers.add(new RouteMember(0, databaseRoute.getId(), dwelling.getDeliveryPointAddress().getUprn()));
        }
      }
    }
    databaseSession.upload(routeMembers);
    return databaseRoute;
  }

  private Integer getDeliveredBy() {
    return null;
  }

  private uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea getBoundedArea(
          DatabaseSession databaseSession, BoundedArea boundedArea) {
    if (boundedArea == null) {
      return null;
    }
    List<uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea> boundedAreas = databaseSession.getByUuid(
            uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea.class, boundedArea.getUuid());
    if (boundedAreas.size() > 0) {
      return boundedAreas.get(0);
    }
    uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea parent =
            getBoundedArea(databaseSession, boundedArea.getParent());
    uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea databaseBoundedArea =
            new uk.co.epii.politics.williamcavendishbentinck.tables.BoundedArea(boundedArea.getUuid().toString(),
                    parent.getId(), getOwner(), getOwnerGroup(), boundedArea.getName(), null);
    databaseSession.upload(Arrays.asList(databaseBoundedArea));
    return databaseBoundedArea;
  }

  private Integer getOwnerGroup() {
    return null;
  }

  private Integer getOwner() {
    return null;
  }

  public void invertSelectionInRoutedAndUnrouted() {
        routedAndUnroutedToolTipModel.invertSelection();
        for (DwellingGroup dwellingGroup :
                routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.SELECTED)) {
            mapPanelModel.select(dwellingGroup);
        }
        for (DwellingGroup dwellingGroup :
                routedAndUnroutedToolTipModel.getDwellingGroupModel().getSelected(SelectedState.UNSELECTED)) {
            mapPanelModel.deselect(dwellingGroup);
        }
    }

    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        BoundedArea boundedArea = boundedAreaSelectionModel.getSelected();
        if (boundedArea == null) {
            return;
        }
        RoutableArea routableArea = getRoutableArea(boundedArea);
        routableArea.autoGenerate(targetSize, unroutedOnly);
        routesModel.update();
    }

    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
        this.mapPanelModel.setProgressTracker(progressTracker);
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
        if (active) {
            boundedAreaSelectionModel.addBoundedAreaSelectionListener(selectedBoundedAreaChangedListener);
            setSelectedBoundedArea(getBoundedAreaSelectionModel().getSelected());
        }
        else {
            boundedAreaSelectionModel.removeBoundedAreaSelectionListener(selectedBoundedAreaChangedListener);
        }
    }

    @Override
    public boolean getActive() {
        return active;
    }
}
