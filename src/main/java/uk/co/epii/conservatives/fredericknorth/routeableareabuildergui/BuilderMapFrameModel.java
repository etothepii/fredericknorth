package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaOverlayItem;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MapPanelModel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.MouseLocation;
import uk.co.epii.conservatives.fredericknorth.maps.gui.OverlayItem;
import uk.co.epii.conservatives.fredericknorth.reports.DwellingCountReportBuilder;
import uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea.ConstructorOverlay;
import uk.co.epii.conservatives.fredericknorth.utilities.EnabledStateChangedEvent;
import uk.co.epii.conservatives.fredericknorth.utilities.EnabledStateChangedListener;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

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
public class BuilderMapFrameModel {

    private static final Logger LOG = LoggerFactory.getLogger(BuilderMapFrameModel.class);

    private MapPanelModel mapPanelModel;
    private BoundedAreaSelectionModel boundedAreaSelectionModel;
    private final Map<BoundedAreaType, Integer> priorities;
    private final ConstructorOverlay constructorOverlay = new ConstructorOverlay(null, 9999);
    private final ApplicationContext applicationContext;
    private final DwellingCountReportBuilder dwellingCountReportBuilder;
    private ProgressTracker progressTracker;
    private Executor executor;
    private final Object enabledSync = new Object();
    private boolean enabled = true;
    private final List<EnabledStateChangedListener<BuilderMapFrameModel>> enabledStateChangedListeners;

    public BuilderMapFrameModel(ApplicationContext applicationContext) {
        this(applicationContext, true);
    }

    BuilderMapFrameModel(ApplicationContext applicationContext, boolean loadKnown) {
        this.mapPanelModel = new BuilderMapPanelModel(
                applicationContext.getDefaultInstance(MapViewGenerator.class), this, constructorOverlay);
        enabledStateChangedListeners = new ArrayList<EnabledStateChangedListener<BuilderMapFrameModel>>();
        executor = Executors.newSingleThreadExecutor();
        this.applicationContext = applicationContext;
        dwellingCountReportBuilder = applicationContext.getDefaultInstance(DwellingCountReportBuilder.class);
        priorities = createPriorities();
        boundedAreaSelectionModel = new DefaultBoundedAreaSelectionModel(applicationContext);
        if (loadKnown) {
            LOG.debug("Loading known instances");
            boundedAreaSelectionModel.loadOSKnownInstances();
            LOG.debug("Loaded known instances");
        }
        boundedAreaSelectionModel.addBoundedAreaSelectionListener(new SelectedBoundedAreaChangedListener() {
            @Override
            public void masterParentSelectionChanged(SelectedBoundedAreaChangedEvent e) {
                updateAfterSelectionChange(e.getSelected());
            }

            @Override
            public void selectionChanged(SelectedBoundedAreaChangedEvent e) {
                updateAfterSelectionChange(e.getSelected());
            }
        });
    }

    private void updateAfterSelectionChange(BoundedArea changedTo) {
        if (changedTo != null &&
                changedTo.getBoundedAreaType() == boundedAreaSelectionModel.getMasterSelectedType()) {
            final Rectangle bounds = changedTo.getArea().getBounds();
            synchronized (enabledSync) {
                disable();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mapPanelModel.setUniverse(new Rectangle(bounds.x - bounds.width / 10, bounds.y - bounds.height / 10,
                                bounds.width * 6 / 5, bounds.height * 6 / 5), progressTracker);
                    }
                });
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
        overlayItems.add(constructorOverlay);
        mapPanelModel.setOverlays(overlayItems);
    }

    public void disable() {
        synchronized (enabledSync) {
            if (enabled) {
                enabled = false;
                fireEnabledStateChanged();
            }
        }
    }

    public void enable() {
        synchronized (enabledSync) {
            if (enabled) {
                enabled = false;
                fireEnabledStateChanged();
            }
        }
    }

    private void fireEnabledStateChanged() {
        synchronized (enabledStateChangedListeners) {
            EnabledStateChangedEvent<BuilderMapFrameModel> e =
                    new EnabledStateChangedEvent<BuilderMapFrameModel>(this, isEnabled());
            for (EnabledStateChangedListener l : enabledStateChangedListeners) {
                l.enabledStateChanged(e);
            }
        }
    }

    public void addEnableStateChangedListener(EnabledStateChangedListener<BuilderMapFrameModel> l) {
        synchronized (enabledStateChangedListeners) {
            enabledStateChangedListeners.add(l);
        }
    }

    public void removeEnableStateChangedListener(EnabledStateChangedListener<BuilderMapFrameModel> l) {
        synchronized (enabledStateChangedListeners) {
            enabledStateChangedListeners.remove(l);
        }
    }

    public boolean isEnabled() {
        synchronized (enabledSync) {
            return enabled;
        }
    }

    public ConstructorOverlay getConstructorOverlay() {
        return constructorOverlay;
    }

    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
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

    public BoundedArea getBoundedAreaOver() {
        Map<OverlayItem, MouseLocation> overlaysMouseOver = mapPanelModel.getImmutableOverlaysMouseOver();
        List<OverlayItem<BoundedArea>> boundedAreasMouseOver = new ArrayList<OverlayItem<BoundedArea>>(overlaysMouseOver.size());
        for (Map.Entry<OverlayItem, MouseLocation> entry : overlaysMouseOver.entrySet()) {
            OverlayItem overlayItem = entry.getKey();
            if (overlayItem.getItem() instanceof BoundedArea) {
                boundedAreasMouseOver.add((OverlayItem<BoundedArea>)overlayItem);
            }
        }
        if (boundedAreasMouseOver.isEmpty()) return null;
        if (boundedAreasMouseOver.size() == 1) return boundedAreasMouseOver.get(0).getItem();
        Collections.sort(boundedAreasMouseOver);
        return boundedAreasMouseOver.get(boundedAreasMouseOver.size() - 1).getItem();
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
                master, flattenedList, colorMap, master.getArea().getBounds().getSize());
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
}
