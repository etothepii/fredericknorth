package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.gui.Activateable;
import uk.co.epii.conservatives.fredericknorth.maps.MapImage;
import uk.co.epii.conservatives.fredericknorth.maps.MapImageObserver;
import uk.co.epii.conservatives.fredericknorth.utilities.*;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaOverlayItem;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelModel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MouseLocation;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.reports.DwellingCountReportBuilder;
import uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder.boundedarea.ConstructorOverlay;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 13:22
 */
public class RoutableAreaBuilderPanelModel implements Activateable {

    private static final Logger LOG = LoggerFactory.getLogger(RoutableAreaBuilderPanelModel.class);
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(RoutableAreaBuilderPanelModel.class.getName().concat("_sync"));

    private MapPanelModel mapPanelModel;
    private final BoundedAreaSelectionModel boundedAreaSelectionModel;
    private final List<MeetingPoint> meetingPoints;
    private final Map<BoundedAreaType, Integer> priorities;
    private final ConstructorOverlay constructorOverlay = new ConstructorOverlay(null, 9999);
    private final ApplicationContext applicationContext;
    private final DwellingCountReportBuilder dwellingCountReportBuilder;
    private ProgressTracker progressTracker;
    private Executor executor;
    private final Object enabledSync = new Object();
    private boolean enabled = true;
    private final List<EnabledStateChangedListener<RoutableAreaBuilderPanelModel>> enabledStateChangedListeners;
    private boolean active = false;
    private final SelectedBoundedAreaChangedListener selectedBoundedAreaChangedListener;

    public RoutableAreaBuilderPanelModel(ApplicationContext applicationContext, BoundedAreaSelectionModel boundedAreaSelectionModel) {
        this.mapPanelModel = new RoutableAreaBuilderMapPanelModel(
                applicationContext.getDefaultInstance(MapViewGenerator.class), this, constructorOverlay);
        enabledStateChangedListeners = new ArrayList<EnabledStateChangedListener<RoutableAreaBuilderPanelModel>>();
        executor = Executors.newSingleThreadExecutor();
        this.applicationContext = applicationContext;
        dwellingCountReportBuilder = applicationContext.getDefaultInstance(DwellingCountReportBuilder.class);
        priorities = createPriorities();
        this.boundedAreaSelectionModel = boundedAreaSelectionModel;
        selectedBoundedAreaChangedListener = createSelectedBoundedAreaChangedListener();
        meetingPoints = new ArrayList<MeetingPoint>();
    }

    private SelectedBoundedAreaChangedListener createSelectedBoundedAreaChangedListener() {
        return new SelectedBoundedAreaChangedListener() {
            @Override
            public void masterParentSelectionChanged(SelectedBoundedAreaChangedEvent e) {
                updateAfterSelectionChange(e.getSelected());
            }

            @Override
            public void selectionChanged(SelectedBoundedAreaChangedEvent e) {
                updateAfterSelectionChange(e.getSelected());
            }
        };
    }

    private void updateAfterSelectionChange(BoundedArea changedTo) {
        if (changedTo != null) {
            final Rectangle bounds = PolygonExtensions.getBounds(changedTo.getAreas());
            LOG_SYNC.debug("Awaiting enabledSync");
            try {
                synchronized (enabledSync) {
                    LOG_SYNC.debug("Received enabledSync");
                    disable();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            mapPanelModel.display(new Rectangle(bounds.x - bounds.width / 10, bounds.y - bounds.height / 10,
                                    bounds.width * 6 / 5, bounds.height * 6 / 5), true);
                        }
                    });
                }
            }
            finally {
                LOG_SYNC.debug("Released enabledSync");
            }
        }
        updateOverlays();
    }

    private void updateOverlays() {

        List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
        for (BoundedAreaType boundedAreaType :
                boundedAreaSelectionModel.getMasterSelectedType().getAllPossibleDecendentTypes()) {
            BoundedArea selected = boundedAreaSelectionModel.getSelected(boundedAreaType);
            if (selected != null) {
                overlayItems.add(
                        new BoundedAreaOverlayItem(
                                selected,
                                priorities.get(boundedAreaType)));
            }
        }
        for (MeetingPoint meetingPoint : meetingPoints) {
            overlayItems.add(new MeetingPointOverlayItem(meetingPoint));
        }
        overlayItems.add(constructorOverlay);
        mapPanelModel.setOverlays(overlayItems);
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
            LOG_SYNC.debug("Released enabledSync");
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

    private void fireEnabledStateChanged() {
        LOG_SYNC.debug("Awaiting enabledStateChangedListeners");
        try {
            synchronized (enabledStateChangedListeners) {
                LOG_SYNC.debug("Received enabledStateChangedListeners");
                EnabledStateChangedEvent<RoutableAreaBuilderPanelModel> e =
                        new EnabledStateChangedEvent<RoutableAreaBuilderPanelModel>(this, isEnabled());
                for (EnabledStateChangedListener l : enabledStateChangedListeners) {
                    l.enabledStateChanged(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released enabledStateChangedListeners");
        }
    }

    public void addEnableStateChangedListener(EnabledStateChangedListener<RoutableAreaBuilderPanelModel> l) {
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

    public void removeEnableStateChangedListener(EnabledStateChangedListener<RoutableAreaBuilderPanelModel> l) {
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

    public boolean isEnabled() {
        LOG_SYNC.debug("Awaiting enabledSync");
        try {
            synchronized (enabledSync) {
                LOG_SYNC.debug("Received enabledSync");
                return enabled;
            }
        }
        finally {
            LOG_SYNC.debug("Released enabledSync");
        }
    }

    public ConstructorOverlay getConstructorOverlay() {
        return constructorOverlay;
    }

    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
        this.mapPanelModel.setProgressTracker(this.progressTracker);
    }

    private Map<BoundedAreaType, Integer> createPriorities() {
        EnumMap<BoundedAreaType, Integer> priorities = new EnumMap<BoundedAreaType, Integer>(BoundedAreaType.class);
        for (BoundedAreaType boundedAreaType : BoundedAreaType.values()) {
            if (!priorities.containsKey(boundedAreaType)) {
                priorities.put(boundedAreaType, boundedAreaType.getPriority());
            }
        }
        return priorities;
    }

    public MapPanelModel getMapPanelModel() {
        return mapPanelModel;
    }

    public BoundedAreaSelectionModel getBoundedAreaSelectionModel() {
        return boundedAreaSelectionModel;
    }

    public <T> T getMouseOver(Class<T> clazz) {
        Map<OverlayItem, MouseLocation> overlaysMouseOver = mapPanelModel.getImmutableOverlaysMouseOver();
        List<OverlayItem<T>> tMouseOver = new ArrayList<OverlayItem<T>>(overlaysMouseOver.size());
        for (Map.Entry<OverlayItem, MouseLocation> entry : overlaysMouseOver.entrySet()) {
            OverlayItem overlayItem = entry.getKey();
            if (clazz.isInstance(overlayItem.getItem())) {
                tMouseOver.add((OverlayItem<T>)overlayItem);
            }
        }
        if (tMouseOver.isEmpty()) return null;
        if (tMouseOver.size() == 1) return tMouseOver.get(0).getItem();
        Collections.sort(tMouseOver);
        return tMouseOver.get(tMouseOver.size() - 1).getItem();
    }

    public void save(File selectedFile) {
        boundedAreaSelectionModel.saveAll(selectedFile);
    }

    public void load(File selectedFile) {
        boundedAreaSelectionModel.loadFrom(selectedFile, applicationContext);
    }

    public boolean buildReport(File largeMap, File csvFile) {
        BoundedArea master = boundedAreaSelectionModel.getSelected(boundedAreaSelectionModel.getMasterSelectedType());
        if (master == null) {
            return false;
        }
        List<BoundedArea> flattenedList = dwellingCountReportBuilder.flatten(master);
        Map<BoundedArea, int[]> dwellingCount = dwellingCountReportBuilder.countDwellings(master);
        HashMap<BoundedAreaType, Color>  colorMap = new HashMap<BoundedAreaType, Color>();
        colorMap.put(BoundedAreaType.NEIGHBOURHOOD, Color.ORANGE);
        colorMap.put(BoundedAreaType.POLLING_DISTRICT, Color.GREEN);
        colorMap.put(BoundedAreaType.UNITARY_DISTRICT_WARD, Color.BLUE);
        colorMap.put(BoundedAreaType.UNITARY_DISTRICT, Color.RED);
        BufferedImage bufferedImage = dwellingCountReportBuilder.getImage(
                master, flattenedList, colorMap, PolygonExtensions.getBounds(master.getAreas()).getSize());
        try {
            ImageIO.write(bufferedImage, "png", largeMap);
            FileWriter fileWriter = new FileWriter(csvFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("index,name,A,B,C,D,E,F,G,H,I,Total");
            printWriter.flush();
            int index = 0;
            for (BoundedArea boundedArea : flattenedList) {
                printWriter.print(++index);
                printWriter.print(",");
                printWriter.print(boundedArea.getName());
                for (int count : dwellingCount.get(boundedArea)) {
                    printWriter.print(",");
                    printWriter.print(count);
                }
                printWriter.println();
                printWriter.flush();
            }
            printWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            enable();
        }
        else {
            disable();
        }
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            boundedAreaSelectionModel.addBoundedAreaSelectionListener(selectedBoundedAreaChangedListener);
        }
        else {
            boundedAreaSelectionModel.removeBoundedAreaSelectionListener(selectedBoundedAreaChangedListener);
        }
    }

    @Override
    public boolean getActive() {
        return active;
    }

    public void exportWardMaps(File currentDir, Dimension output) {
        BoundedArea boundedArea = getBoundedAreaSelectionModel().getMasterSelected();
        MapViewGenerator mapViewGenerator = applicationContext.getDefaultInstance(MapViewGenerator.class);
        mapViewGenerator.setViewPortSize(output, new NullProgressTracker(), null);
        exportWardMaps(boundedArea, mapViewGenerator, currentDir, output, true);
        exportWardMaps(boundedArea, mapViewGenerator, currentDir, output, false);
    }

    private void exportWardMaps(BoundedArea boundedArea, MapViewGenerator mapViewGenerator, File currentDir, Dimension output, boolean outlineOnly) {
        File exportFile = new File(currentDir, boundedArea.getName().replace("[() ]", "").concat(outlineOnly ? "_outline" : "").concat(".png"));
        File exportAllFile = new File(currentDir, boundedArea.getName().replace("[() ]", "").concat(outlineOnly ? "_outline" : "").concat("_all.png"));
        System.out.println(exportFile);
        mapViewGenerator.scaleToFitRectangle(
                RectangleExtensions.grow(
                        PolygonExtensions.getBounds(boundedArea.getAreas()), .2),
                new NullProgressTracker(), null);
        BufferedImage buffereredImage = new BufferedImage(output.width, output.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)buffereredImage.getGraphics();
        if (!outlineOnly) {
            g.drawImage(mapViewGenerator.getView().getMap(), 0, 0, null);
        }
        g.setColor(new Color(255, 0, 0, 64));
        for (Polygon p : boundedArea.getAreas()) {
            g.fill(mapViewGenerator.getView().getGeoToImageTransform().createTransformedShape(p));
        }
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(5f));
        for (Polygon p : boundedArea.getAreas()) {
            g.draw(mapViewGenerator.getView().getGeoToImageTransform().createTransformedShape(p));
        }
        try {
            ImageIO.write(buffereredImage, "png", exportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BoundedArea[] children = boundedArea.getChildren();
        if (children.length != 0) {
            for (BoundedArea child : children) {
                for (Polygon p : child.getAreas()) {
                    g.draw(mapViewGenerator.getView().getGeoToImageTransform().createTransformedShape(p));
                }
            }
            try {
                ImageIO.write(buffereredImage, "png", exportAllFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (BoundedArea child : children) {
                exportWardMaps(child, mapViewGenerator, currentDir, output, outlineOnly);
            }
        }
    }

    public void createMeetingPoint(String name, Point point) {
        meetingPoints.add(new MeetingPoint(name, point));
        updateOverlays();
    }
}
