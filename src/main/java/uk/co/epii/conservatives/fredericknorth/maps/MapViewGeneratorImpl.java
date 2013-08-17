package uk.co.epii.conservatives.fredericknorth.maps;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

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

    private final Object mapCacheSync = new Object();
    private final LocationFactory locationFactory;
    private final MapLabelFactory mapLabelFactory;
    private final OSMapLoader osMapLoader;
    private final OSMapLocator osMapLocator;
    private MapImage mapCache;
    private Point geoCenter;
    private Dimension viewPortSize;
    private double scale;
    private Rectangle universe;
    private Rectangle loaded;

    MapViewGeneratorImpl(MapImage mapImage, LocationFactory locationFactory, MapLabelFactory mapLabelFactory) {
        this(mapImage, null, null, locationFactory, mapLabelFactory);
    }

    public MapViewGeneratorImpl(OSMapLoader osMapLoader, OSMapLocator osMapLocator, LocationFactory locationFactory,
                                MapLabelFactory mapLabelFactory) {
        this(null, osMapLoader, osMapLocator, locationFactory, mapLabelFactory);
    }

    private MapViewGeneratorImpl(MapImage mapImage, OSMapLoader osMapLoader, OSMapLocator osMapLocator,
                                 LocationFactory locationFactory, MapLabelFactory mapLabelFactory) {
        mapCache = mapImage == null ?
                new MapImageImpl(getNullImage(), new Point(0, 0)) : mapImage;
        this.osMapLoader = osMapLoader;
        this.osMapLocator = osMapLocator;
        this.locationFactory = locationFactory;
        this.mapLabelFactory = mapLabelFactory;
        setGeoCenter(new Point(
                mapCache.getGeoTopLeft().x + mapCache.getSize().width / 2,
                mapCache.getGeoTopLeft().y - mapCache.getSize().height / 2));
        setViewPortSize(DEFAULT_SIZE);
    }

    private BufferedImage getNullImage() {
        BufferedImage nullImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = nullImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1, 1);
        return nullImage;
    }

    private MapImage getMapImage() {
        synchronized (mapCacheSync) {
            return mapCache;
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
        synchronized (mapCacheSync) {
            this.universe = universe;
            if (this.loaded != null && this.loaded.contains(universe)) {
                return;
            }
            Point geoTopleft = new Point(universe.x, universe.y + universe.height);
            try {
                BufferedImage mapImage = buildMapImage(universe, progressTracker);
                this.mapCache = new MapImageImpl(mapImage, geoTopleft);
                this.universe = universe;
                this.loaded = universe;
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    private BufferedImage buildMapImage(Rectangle universe, ProgressTracker progressTracker) throws IOException, InterruptedException {
        BufferedImage universeImage = new BufferedImage(universe.width, universe.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = (Graphics2D)universeImage.getGraphics();
        List<OSMap> maps = osMapLocator.getMaps(universe);
        if (progressTracker != null) {
            progressTracker.startSubsection(maps.size());
        }
        for (OSMap map : maps) {
            if (progressTracker != null) {
                progressTracker.setMessage(String.format("Loading %s", map.getMapName()));
            }
            LOG.debug("{}", map);
            Point mapBottomLeft = osMapLocator.getBottomLeftMapCoordinate(map);
            int x = mapBottomLeft.x - universe.x;
            int y = universe.y + universe.height - mapBottomLeft.y - 5000;
            int w = 5000;
            int h = 5000;
            BufferedImage mapImage = osMapLoader.loadMap(map);
            imageGraphics.drawImage(mapImage, x, y, w, h, null);
            if (progressTracker != null) {
                progressTracker.increment();
            }
        }
        imageGraphics.setTransform(AffineTransform.getScaleInstance(1d, 1d));
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
        return universe;
    }

    @Override
    public MapView getView() {
        MapImage mapImage = getMapImage();
        if (mapImage == null) return null;
        BufferedImage bufferedImage = new BufferedImage(viewPortSize.width, viewPortSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
        g.setTransform(scaleTransform);
        MapView mapView = new MapViewImpl(bufferedImage, geoCenter, viewPortSize, scale, locationFactory, mapLabelFactory);
        Point drawFromUnscaled = mapView.getImageLocation(mapImage.getGeoTopLeft());
        Point drawFrom = new Point((int)(drawFromUnscaled.x / scale), (int)(drawFromUnscaled.y / scale));
        Rectangle fillAround = scaleTransform.createTransformedShape(
                new Rectangle(drawFrom.x, drawFrom.y, mapImage.getMap().getWidth(), mapImage.getMap().getHeight())).getBounds();
        g.drawImage(mapImage.getMap(), drawFrom.x, drawFrom.y, null);
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
