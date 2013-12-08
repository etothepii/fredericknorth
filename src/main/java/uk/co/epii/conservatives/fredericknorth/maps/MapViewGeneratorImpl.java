package uk.co.epii.conservatives.fredericknorth.maps;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.DimensionExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.CancellationToken;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * It is important to remember that the co-ordinates in an Image start at increase as one descends down the page
 * whereas our friends at the ordinance survey increase the value of y as we go North
 *
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:29
 */
class MapViewGeneratorImpl implements MapViewGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MapViewGeneratorImpl.class);
    private static final Logger LOG_EACH_MAP = LoggerFactory.getLogger(MapViewGeneratorImpl.class.getName().concat("_eachMap"));
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(MapViewGeneratorImpl.class.getName().concat("_sync"));
    private static final Logger LOG_SCALE = LoggerFactory.getLogger(MapViewGeneratorImpl.class.getName().concat("_scale"));

    private static Dimension DEFAULT_SIZE = new Dimension(640,480);

    private final Executor initializer;
    private final LocationFactory locationFactory;
    private final MapLabelFactory mapLabelFactory;
    private final OSMapLoader osMapLoader;
    private final OSMapLocator osMapLocator;
    private Point geoCenter;
    private Dimension viewPortSize;
    private double scale;
    private CancellationToken cancellationToken;
    private final Object cancellationTokenSync = new Object();
    private MapImageImpl currentImage;
    private final Object currentImageSync = new Object();
    private final Object paintImageSync = new Object();
    private double maxScale = 5;
    private double minScale = 0.001;
    private int incrementsPerImage = 1000;
    private int incrementsForImageLoad = 800;
    private final List<MapViewChangedTranslationListener> mapViewChangedTranslationListeners;

    MapViewGeneratorImpl(ApplicationContext applicationContext,
                         Map<OSMapType, MapImage> mapCache, LocationFactory locationFactory, MapLabelFactory mapLabelFactory) {
        this(applicationContext.getDefaultInstance(OSMapLoader.class),
                applicationContext.getDefaultInstance(OSMapLocator.class),
                locationFactory, mapLabelFactory, NullProgressTracker.NULL);
    }

    public MapViewGeneratorImpl(OSMapLoader osMapLoader, OSMapLocator osMapLocator, LocationFactory locationFactory,
                                MapLabelFactory mapLabelFactory, ProgressTracker progressTracker) {
        mapViewChangedTranslationListeners = new ArrayList<MapViewChangedTranslationListener>();
        Rectangle initial = new Rectangle(new Dimension(1, 1));
        currentImage = new MapImageImpl(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Rectangle(0, 0, 1, 1), OSMapType.STREET_VIEW, 1d);
        initializer = Executors.newSingleThreadExecutor();
        this.osMapLoader = osMapLoader;
        this.osMapLocator = osMapLocator;
        this.locationFactory = locationFactory;
        this.mapLabelFactory = mapLabelFactory;
        setViewPortSize(DEFAULT_SIZE, true, false, new NullProgressTracker(), null);
        scaleToFitRectangle(initial, progressTracker, null);
    }

    private MapImage getMapImage() {
        return getCurrentImage();
    }

    @Override
    @NotNull
    public boolean setGeoCenter(Point geoCenter, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        return setGeoCenter(geoCenter, true, progressTracker, imageObserver);
    }

    private boolean setGeoCenter(Point geoCenter, boolean updateImage, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        if (geoCenter.equals(this.geoCenter)) {
            return false;
        }
        this.geoCenter = geoCenter;
        if (updateImage) {
            updateImage(progressTracker, imageObserver);
        }
        fireMapViewTranslationChangedEvent();
        return true;
    }

    @Override
    public boolean setViewPortSize(Dimension viewPortSize, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        return setViewPortSize(viewPortSize, false, true, progressTracker, imageObserver);
    }

    private boolean setViewPortSize(Dimension viewPortSize, boolean force, boolean updateImage,
                                    ProgressTracker progressTracker, MapImageObserver imageObserver) {
        minScale = Math.min(viewPortSize.getHeight() / 1300000, viewPortSize.getWidth() / 700000);
        if (!force && viewPortSize.equals(this.viewPortSize)) {
            return false;
        }
        if (this.viewPortSize != null) {
            scale = Math.min(maxScale, Math.max(minScale, scale * Math.min(viewPortSize.getWidth() / this.viewPortSize.getWidth(),
                    viewPortSize.getHeight() / this.viewPortSize.getHeight())));
        }
        else {
            MapImage mapImage = getMapImage();
            scale = Math.min(maxScale, Math.max(minScale, Math.min(viewPortSize.getWidth() / mapImage.getSize().getWidth(),
                    viewPortSize.getHeight() / mapImage.getSize().getHeight())));
        }
        this.viewPortSize = viewPortSize;
        if (updateImage) {
            updateImage(progressTracker, imageObserver);
        }
        fireMapViewTranslationChangedEvent();
        return true;
    }

    @Override
    public boolean setScale(double scale, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        return setScale(scale, true, progressTracker, imageObserver);
    }

    private boolean setScale(double scale, boolean updateImage, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        if (scale < minScale) {
            scale = minScale;
        }
        else if (scale > maxScale) {
            scale = maxScale;
        }
        if (Double.compare(scale, this.scale) == 0) {
            return false;
        }
        this.scale = scale;
        LOG_SCALE.debug("scale: {}", scale);
        if (updateImage) {
            updateImage(progressTracker, imageObserver);
        }
        fireMapViewTranslationChangedEvent();
        return true;
    }

    private void updateImage(final ProgressTracker progressTracker, final MapImageObserver imageObserver) {
        synchronized (cancellationTokenSync) {
            if (cancellationToken != null) {
                cancellationToken.cancel();
            }
            cancellationToken = new CancellationToken();
        }
        if (imageObserver == null) {
            updateImageOnThread(progressTracker, imageObserver, cancellationToken);
        }
        else {
            initializer.execute(new Runnable() {
                @Override
                public void run() {
                    updateImageOnThread(progressTracker, imageObserver, cancellationToken);
                }
            });
        }
    }

    private void updateImageOnThread(final ProgressTracker progressTracker, final MapImageObserver imageObserver,
                                     CancellationToken cancellationToken) {
        final MapImageImpl mapImage;
        final Rectangle visible;
        LOG_SYNC.debug("Awaiting currentImageSync");
        try {
            synchronized (currentImageSync) {
                LOG_SYNC.debug("Received currentImageSync");
                visible = getVisible();
                double requiredScale = Math.min(viewPortSize.getWidth() / visible.getWidth(),
                        viewPortSize.getHeight() / visible.getHeight());
                final OSMapType mapType = OSMapType.getMapType(requiredScale);
                MapImageImpl previousMapImage = getCurrentImage();
                mapImage = new MapImageImpl(
                        new BufferedImage(viewPortSize.width, viewPortSize.height, BufferedImage.TYPE_INT_ARGB),
                        visible, mapType, requiredScale);
                loadPreviousMapIntoNewMapAndReportCleanGeoRect(previousMapImage, mapImage);
                try {
                    if (SwingUtilities.isEventDispatchThread()) {
                        setCurrentImage(mapImage);
                    }
                    else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                LOG_SYNC.debug("Awaiting currentImageSync");
                                try {
                                    synchronized (currentImageSync) {
                                        LOG_SYNC.debug("Received currentImageSync");
                                        setCurrentImage(mapImage);
                                        currentImageSync.notify();
                                    }
                                }
                                finally {
                                    LOG_SYNC.debug("Released currentImageSync");
                                }
                            }
                        });
                        LOG_SYNC.debug("Released currentImageSync");
                        LOG_SYNC.debug("Awaiting currentImageSync");
                        currentImageSync.wait();
                        LOG_SYNC.debug("Received currentImageSync");
                    }
                    if (imageObserver != null) {
                        imageObserver.imageUpdated(
                                currentImage,
                                new Rectangle(0, 0, currentImage.getMap().getWidth(), currentImage.getMap().getHeight()),
                                false);
                    }
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released currentImageSync");
        }
        paintMapImage(mapImage, visible, progressTracker, imageObserver, cancellationToken);
    }

    private void loadPreviousMapIntoNewMapAndReportCleanGeoRect(MapImageImpl previousMapImage, MapImageImpl newMapImage) {
        BufferedImage map = newMapImage.getMap();
        Graphics2D g = (Graphics2D)map.getGraphics();
        g.setColor(OSMapLoaderImpl.getSeaColor(newMapImage.getOSMapType()));
        g.fillRect(0, 0, map.getWidth(), map.getHeight());
        if (previousMapImage != null) {
            Point drawFrom = newMapImage.getImageLocation(previousMapImage.getGeoTopLeft());
            Dimension size;
            if (newMapImage.getScale() == previousMapImage.getScale()) {
                size = previousMapImage.getSize();
            }
            else {
                size = DimensionExtensions.scale(previousMapImage.getSize(),
                        newMapImage.getScale() / previousMapImage.getScale());
            }
            g.drawImage(previousMapImage.getMap(), drawFrom.x, drawFrom.y, size.width, size.height, null);
            if (previousMapImage.getOSMapType() == newMapImage.getOSMapType() &&
                    newMapImage.getScale() < 1.01 * previousMapImage.getScale()) {
                newMapImage.getCleanRectangles().addAll(previousMapImage.getCleanRectangles());
            }
        }
    }

    private void setCurrentImage(MapImageImpl currentImage) {
        LOG_SYNC.debug("Awaiting currentImageSync");
        try {
            synchronized (currentImageSync) {
                LOG_SYNC.debug("Received currentImageSync");
                this.currentImage = currentImage;
            }
        }
        finally {
            LOG_SYNC.debug("Released currentImageSync");
        }
    }

    private MapImageImpl getCurrentImage() {
        LOG_SYNC.debug("Awaiting currentImageSync");
        try {
            synchronized (currentImageSync) {
                LOG_SYNC.debug("Received currentImageSync");
                return currentImage;
            }
        }
        finally {
            LOG_SYNC.debug("Released currentImageSync");
        }
    }

    private void paintMapImage(MapImageImpl mapImage, Rectangle visible,
                                        ProgressTracker progressTracker, MapImageObserver mapImageObserver,
                                        CancellationToken cancellationToken) {
        if (osMapLoader == null || osMapLocator == null) return;
        LOG_SYNC.debug("Awaiting paintImageSync");
        try {
            synchronized (paintImageSync) {
                LOG_SYNC.debug("Received paintImageSync");
                LOG.debug("size of mapImage.getMap(): {} x {}", mapImage.getMap().getWidth(), mapImage.getMap().getHeight());
                LOG.debug("paintMapImage({}, {})", new Object[] {visible, viewPortSize});
                Graphics2D imageGraphics = (Graphics2D)mapImage.getMap().getGraphics();
                Set<OSMap> maps;
                Collection<Rectangle> dirty =
                        RectangleExtensions.getSurrounding(mapImage.getGeoCoverage(), mapImage.getCleanRectangles());
                maps = new HashSet<OSMap>();
                for (Rectangle rectangle : dirty) {
                     maps.addAll(osMapLocator.getMaps(mapImage.getOSMapType(), rectangle));
                }
                if (maps.isEmpty()) {
                    LOG.warn("Some how there are not any maps to draw, this should be investigated");
                    return;
                }
                progressTracker.startSubsection(maps.size() * incrementsPerImage);
                Dimension imageSize = osMapLocator.getImageSize(mapImage.getOSMapType());
                Dimension geoSize = osMapLocator.getRepresentedSize(mapImage.getOSMapType());
                LOG.debug("imageSize: {}", imageSize);
                double imageScale = Math.min(viewPortSize.getWidth() / visible.getWidth(),
                        viewPortSize.getHeight() / visible.getHeight()) / mapImage.getOSMapType().getScale();
                imageGraphics.setTransform(AffineTransform.getScaleInstance(imageScale, imageScale));
                Dimension targetSize = DimensionExtensions.scale(imageSize, imageScale);
                LOG.debug("imageScale: {}", imageScale);
                for (OSMap map : maps) {
                    if (cancellationToken.isCancelled()) {
                        progressTracker.endSubsection();
                        return;
                    }
                    progressTracker.setMessage(String.format("Loading %s", map.getMapName()));
                    LOG_EACH_MAP.debug("{}", map);
                    Point mapBottomLeft = osMapLocator.getBottomLeftMapCoordinate(map);
                    LOG.debug("mapBottomLeft: {}", mapBottomLeft);
                    Rectangle geoRectangle = new Rectangle(mapBottomLeft, geoSize);
                    int x = (int)((mapBottomLeft.x - visible.x) * mapImage.getOSMapType().getScale());
                    int y = (int)((visible.y + visible.height - mapBottomLeft.y) * mapImage.getOSMapType().getScale()) - imageSize.height;
                    LOG.debug("(x, y): ({}, {})", x, y);
                    BufferedImage loadedMap = osMapLoader.loadMap(map, targetSize, progressTracker,
                            incrementsForImageLoad, cancellationToken);
                    if (cancellationToken.isCancelled()) {
                        progressTracker.endSubsection();
                        return;
                    }
                    try {
                        LOG_SYNC.debug("Awaiting mapImage.getMap()");
                        synchronized (mapImage.getMap()) {
                            LOG_SYNC.debug("Received mapImage.getMap()");
                            imageGraphics.drawImage(loadedMap, x, y, imageSize.width, imageSize.height, null);
                            mapImage.reportDrawn(geoRectangle);
                        }
                    }
                    finally {
                        LOG_SYNC.debug("Released mapImage.getMap()");
                    }
                    if (mapImageObserver != null) {
                        mapImageObserver.imageUpdated(mapImage, RectangleExtensions.getScaleInstance(
                                new Rectangle(x, y, imageSize.width, imageSize.height), new Point(0, 0), imageScale), false);
                    }
                    progressTracker.increment(incrementsPerImage - incrementsForImageLoad);
                }
                if (mapImageObserver != null) {
                    mapImageObserver.imageUpdated(mapImage, new Rectangle(0, 0,
                            mapImage.getMap().getWidth(), mapImage.getMap().getHeight()), true);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released paintImageSync");
        }
    }

    @Override
    public Point getGeoCenter() {
        return geoCenter;
    }

    @Override
    public Dimension getViewPortSize() {
        return viewPortSize;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public boolean scaleToFitRectangle(Rectangle rectangeToFit, ProgressTracker progressTracker, MapImageObserver imageObserver) {
        if (viewPortSize == null) {
            throw new NullPointerException("One cannot set scale to fit when the view port size is not set");
        }
        double requiredScale = Math.min(
                viewPortSize.getWidth() / rectangeToFit.getWidth(),
                viewPortSize.getHeight() / rectangeToFit.getHeight());
        Point desiredGeoCentre = RectangleExtensions.getCenter(rectangeToFit);
        boolean dirty = setGeoCenter(desiredGeoCentre, false, progressTracker, imageObserver);
        dirty &= setScale(requiredScale, false, progressTracker, imageObserver);
        if (dirty) {
            fireMapViewTranslationChangedEvent();
            updateImage(progressTracker, imageObserver);
        }
        return dirty;
    }

    @Override
    public boolean setScaleAndCenter(double newScale, Point newGeoCenter, ProgressTracker progressTracker,
                                     MapImageObserver imageObserver) {
        boolean hasChanged = setScale(newScale, false, progressTracker, imageObserver);
        hasChanged |= setGeoCenter(newGeoCenter, progressTracker, imageObserver);
        if (hasChanged) {
            updateImage(progressTracker, imageObserver);
            fireMapViewTranslationChangedEvent();
        }
        return hasChanged;
    }

    @Override
    public void addMapViewChangedListener(MapViewChangedTranslationListener l) {
        mapViewChangedTranslationListeners.add(l);
    }

    @Override
    public void removeMapViewChangedListener(MapViewChangedTranslationListener l) {
        mapViewChangedTranslationListeners.remove(l);
    }

    protected void fireMapViewTranslationChangedEvent() {
        fireMapViewTranslationChangedEvent(new MapViewTranslationChangedEvent(this));
    }

    protected void fireMapViewTranslationChangedEvent(MapViewTranslationChangedEvent e) {
        synchronized (mapViewChangedTranslationListeners) {
            for (MapViewChangedTranslationListener l : mapViewChangedTranslationListeners) {
                l.mapViewChanged(e);
            }
        }
    }

    @Override
    public MapView getView() {
        BufferedImage snapshotImage = getSnapshotImage();
        if (snapshotImage == null) {
            LOG.debug("mapImage: null");
        }
        else {
            LOG.debug("mapImage: {} x {}", snapshotImage.getWidth(), snapshotImage.getHeight());
        }
        return new MapViewImpl(snapshotImage, geoCenter, viewPortSize, scale, locationFactory, mapLabelFactory);
    }

    private BufferedImage getSnapshotImage() {
        final BufferedImage map;
        try {
            LOG_SYNC.debug("Awaiting currentImageSync");
            synchronized (currentImageSync) {
                LOG_SYNC.debug("Received currentImageSync");
                if (currentImage == null) {
                    return null;
                }
                else {
                    map = currentImage.getMap();
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released currentImageSync");
        }
        try {
            LOG_SYNC.debug("Awaiting currentImage.getMap()");
            synchronized (map) {
                LOG_SYNC.debug("Received currentImage.getMap()");
                BufferedImage snapshot = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
                snapshot.getGraphics().drawImage(map, 0, 0, null);
                return snapshot;
            }
        }
        finally {
            LOG_SYNC.debug("Released currentImage.getMap()");
        }
    }

    public Rectangle getVisible() {
        Dimension scaledViewport = getScaledViewPortSize();
        Point geoCentre = getGeoCenter();
        if (scaledViewport == null || geoCentre == null) return null;
        return new Rectangle(geoCentre.x - scaledViewport.width / 2, geoCentre.y - scaledViewport.height / 2,
                scaledViewport.width, scaledViewport.height);
    }

    private Dimension getScaledViewPortSize() {
        Dimension viewport = getViewPortSize();
        if (viewport == null) return null;
        double scale = getScale();
        return new Dimension((int)(viewport.getWidth() / scale), (int)(viewport.getHeight() / scale));
    }
}
