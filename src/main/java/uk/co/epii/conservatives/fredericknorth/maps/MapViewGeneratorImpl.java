package uk.co.epii.conservatives.fredericknorth.maps;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

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

    private static Dimension DEFAULT_SIZE = new Dimension(640,480);

    private final LocationFactory locationFactory;
    private final MapLabelFactory mapLabelFactory;
    private final OSMapLoader osMapLoader;
    private final OSMapLocator osMapLocator;
    private Point geoCenter;
    private OSMapType activeType;
    private Dimension viewPortSize;
    private double scale;
    private final Map<OSMapType, Rectangle> loaded;
    private final Map<OSMapType, Rectangle> universe;
    private final Map<OSMapType, MapImage> mapCache;

    MapViewGeneratorImpl(Map<OSMapType, MapImage> mapCache, LocationFactory locationFactory, MapLabelFactory mapLabelFactory) {
        this(mapCache, null, null, locationFactory, mapLabelFactory, OSMapType.MINI);
    }

    public MapViewGeneratorImpl(OSMapLoader osMapLoader, OSMapLocator osMapLocator, LocationFactory locationFactory,
                                MapLabelFactory mapLabelFactory) {
        this(null, osMapLoader, osMapLocator, locationFactory, mapLabelFactory, OSMapType.MINI);
    }

    private MapViewGeneratorImpl(Map<OSMapType, MapImage> mapCache, OSMapLoader osMapLoader, OSMapLocator osMapLocator,
                                 LocationFactory locationFactory, MapLabelFactory mapLabelFactory, OSMapType activeType) {
        this.mapCache = mapCache == null ?
                createMapCache() : mapCache;
        this.activeType = activeType;
        this.osMapLoader = osMapLoader;
        this.osMapLocator = osMapLocator;
        this.locationFactory = locationFactory;
        this.mapLabelFactory = mapLabelFactory;
        loaded = new EnumMap<OSMapType, Rectangle>(OSMapType.class);
        universe = new EnumMap<OSMapType, Rectangle>(OSMapType.class);
        setGeoCenter(new Point(
                this.mapCache.get(activeType).getGeoTopLeft().x + this.mapCache.get(activeType).getSize().width / 2,
                this.mapCache.get(activeType).getGeoTopLeft().y - this.mapCache.get(activeType).getSize().height / 2));
        setViewPortSize(DEFAULT_SIZE);
    }

    private Map<OSMapType, MapImage> createMapCache() {
        Map<OSMapType, MapImage> empty = new EnumMap<OSMapType, MapImage>(OSMapType.class);
        for (OSMapType osMapType : OSMapType.values()) {
            empty.put(osMapType, new MapImageImpl(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(-1, -1)));
        }
        return empty;
    }

    private BufferedImage getNullImage() {
        BufferedImage nullImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = nullImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1, 1);
        return nullImage;
    }

    private MapImage getMapImage() {
        synchronized (mapCache) {
            return mapCache.get(getActiveType());
        }
    }

    @Override
    @NotNull
    public boolean setGeoCenter(Point geoCenter) {
        if (!geoCenter.equals(this.geoCenter)) {
            this.geoCenter = geoCenter;
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public boolean setViewPortSize(Dimension viewPortSize) {
        return setViewPortSize(viewPortSize, false);
    }

    private boolean setViewPortSize(Dimension viewPortSize, boolean force) {
        if (!force && viewPortSize.equals(this.viewPortSize)) {
            return false;
        }
        if (this.viewPortSize != null) {
            scale *= Math.min(viewPortSize.getWidth() / this.viewPortSize.getWidth(),
                    viewPortSize.getHeight() / this.viewPortSize.getHeight());
        }
        else {
            MapImage mapImage = getMapImage();
            scale = Math.min(viewPortSize.getWidth() / mapImage.getSize().getWidth(),
                    viewPortSize.getHeight() / mapImage.getSize().getHeight());
        }
        this.viewPortSize = viewPortSize;
        return true;
    }

    @Override
    public boolean setScale(double scale) {
        if (Double.compare(scale, this.scale) == 0) {
            return false;
        }
        this.scale = scale;
        return true;
    }

    @Override
    public void loadUniverse(Rectangle universe) {
        loadUniverse(universe, null);
    }

    @Override
    public void loadUniverse(Rectangle universe, ProgressTracker progressTracker) {
        synchronized (mapCache) {
            double requiredScale = Math.min(viewPortSize.getWidth() / universe.getWidth(), viewPortSize.getHeight() / universe.getHeight());
            setActiveType(OSMapType.getMapType(requiredScale));
            setUniverse(universe);
            Rectangle loaded = getLoaded();
            if (loaded != null && loaded.contains(universe)) {
                return;
            }
            Point geoTopleft = new Point(universe.x, universe.y + universe.height);
            try {
                BufferedImage mapImage = buildMapImage(getActiveType(), universe, progressTracker);
                setMapCache(new MapImageImpl(mapImage, geoTopleft));
                setUniverse(universe);
                setLoaded(universe);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    private void setMapCache(MapImageImpl mapImage) {
        this.mapCache.put(getActiveType(), mapImage);
    }

    private void setLoaded(Rectangle universe) {
        this.loaded.put(getActiveType(), universe);
    }

    private Rectangle getLoaded() {
        return this.loaded.get(getActiveType());
    }

    private void setUniverse(Rectangle universe) {
        this.universe.put(getActiveType(), universe);
    }

    private OSMapType getActiveType() {
        return activeType;
    }

    private void setActiveType(OSMapType mapType) {
        this.activeType = mapType;
    }

    private BufferedImage buildMapImage(OSMapType mapType, Rectangle universe, ProgressTracker progressTracker) throws IOException, InterruptedException {
        BufferedImage universeImage = new BufferedImage(
                (int)(universe.width * mapType.getScale()),
                (int)(universe.height * mapType.getScale()),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = (Graphics2D)universeImage.getGraphics();
        Set<OSMap> maps = osMapLocator.getMaps(mapType, universe);
        if (progressTracker != null) {
            progressTracker.startSubsection(maps.size());
        }
        Dimension imageSize = osMapLocator.getImageSize(mapType);
        for (OSMap map : maps) {
            if (progressTracker != null) {
                progressTracker.setMessage(String.format("Loading %s", map.getMapName()));
            }
            LOG.debug("{}", map);
            Point mapBottomLeft = osMapLocator.getBottomLeftMapCoordinate(map);
            int x = (int)((mapBottomLeft.x - universe.x) * mapType.getScale());
            int y = (int)((universe.y + universe.height - mapBottomLeft.y) * mapType.getScale()) - imageSize.height;
            BufferedImage mapImage = osMapLoader.loadMap(map);
            imageGraphics.drawImage(mapImage, x, y, imageSize.width, imageSize.height, null);
            if (progressTracker != null) {
                progressTracker.increment();
            }
        }
        return universeImage;
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
    public boolean scaleToFitRectangle(Rectangle rectangeToFit) {
        if (viewPortSize == null) {
            throw new NullPointerException("One cannot set scale to fit when the view port size is not set");
        }
        boolean dirty = setGeoCenter(new Point(rectangeToFit.x + rectangeToFit.width / 2, rectangeToFit.y + rectangeToFit.height / 2));
        dirty &= setScale(Math.min(viewPortSize.getWidth() / rectangeToFit.getWidth(), viewPortSize.getHeight() / rectangeToFit.getHeight()));
        return dirty;
    }

    @Override
    public Rectangle getUniverse() {
        return this.universe.get(getActiveType());
    }

    @Override
    public MapView getView() {
        MapImage mapImage = getMapImage();
        LOG.debug("mapImage: {} x {}", mapImage.getMap().getWidth(), mapImage.getMap().getHeight());
        if (mapImage == null) return null;
        BufferedImage bufferedImage = new BufferedImage(viewPortSize.width, viewPortSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
        g.setTransform(scaleTransform);
        MapView mapView = new MapViewImpl(bufferedImage, geoCenter, viewPortSize, scale, locationFactory, mapLabelFactory);
        Point drawFromUnscaled = mapView.getImageLocation(mapImage.getGeoTopLeft());
        Point drawFrom = new Point((int)(drawFromUnscaled.x / scale), (int)(drawFromUnscaled.y / scale));
        Rectangle fillAround = scaleTransform.createTransformedShape(
                new Rectangle(drawFrom.x, drawFrom.y,
                        (int)(mapImage.getMap().getWidth() / getActiveType().getScale()),
                        (int)(mapImage.getMap().getHeight() / getActiveType().getScale()))).getBounds();
        g.drawImage(mapImage.getMap(), drawFrom.x, drawFrom.y,
                (int)(mapImage.getMap().getWidth() / getActiveType().getScale()),
                (int)(mapImage.getMap().getHeight() / getActiveType().getScale()), null);
        g.setTransform(AffineTransform.getScaleInstance(1d, 1d));
        g.setColor(Color.WHITE);
        for (Rectangle r : getRectanglesToClear(new Rectangle(viewPortSize), fillAround)) {
            LOG.debug("Filling White Rectangle: {}", r);
            g.fill(r);
        }
        return mapView;
    }

    static List<Rectangle> getRectanglesToClear(Rectangle base, Rectangle fillAround) {
        LOG.debug("base: {}", base);
        LOG.debug("fillAround: {}", fillAround);
        List<Rectangle> rectangles = new ArrayList<Rectangle>(4);
        if (fillAround.x > base.x) {
            rectangles.add(new Rectangle(base.x, base.y, fillAround.x - base.x, base.height));
        }
        if (fillAround.y > base.y) {
            rectangles.add(new Rectangle(base.x, base.y, base.width, fillAround.y - base.y));
        }
        if (base.width + base.x > fillAround.width + fillAround.x) {
            rectangles.add(new Rectangle(fillAround.x + fillAround.width, base.y,
                    base.width + base.x - fillAround.width - fillAround.x, base.height));
        }
        if (base.height + base.y > fillAround.height + fillAround.y) {
            rectangles.add(new Rectangle(base.x, fillAround.y + fillAround.height, base.width,
                    base.height + base.y - fillAround.height - fillAround.y));
        }
        return rectangles;
    }
}
