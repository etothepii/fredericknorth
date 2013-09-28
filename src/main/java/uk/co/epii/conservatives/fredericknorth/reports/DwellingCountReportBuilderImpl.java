package uk.co.epii.conservatives.fredericknorth.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;
import uk.co.epii.conservatives.fredericknorth.maps.ImageAndGeoPointTranslator;
import uk.co.epii.conservatives.fredericknorth.maps.MapView;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 05/08/2013
 * Time: 15:56
 */
class DwellingCountReportBuilderImpl implements DwellingCountReportBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DwellingCountReportBuilderImpl.class);

    private final PostcodeDatumFactory postcodeDatumFactory;
    private final MapViewGenerator mapViewGenerator;
    private Map<BoundedAreaType, Color> colours = new HashMap<BoundedAreaType, Color>();

    public DwellingCountReportBuilderImpl(PostcodeDatumFactory postcodeDatumFactory,
                                          MapViewGenerator mapViewGenerator) {
        this.postcodeDatumFactory = postcodeDatumFactory;
        this.mapViewGenerator = mapViewGenerator;
    }

    @Override
    public Map<BoundedArea, int[]> countDwellings(BoundedArea boundedArea) {
        return countDwellings(boundedArea, postcodeDatumFactory.getPostcodes());
    }

    @Override
    public List<BoundedArea> flatten(BoundedArea boundedArea) {
        List<BoundedArea> boundedAreas = new ArrayList<BoundedArea>();
        boundedAreas.add(boundedArea);
        for (BoundedArea child : boundedArea.getChildren()) {
            boundedAreas.addAll(flatten(child));
        }
        return boundedAreas;
    }

    @Override
    public BufferedImage getImage(BoundedArea masterArea, List<BoundedArea> boundedAreas,
                          Map<BoundedAreaType, Color> colours, Dimension dimension) {
        this.colours = colours;
        rectangles.clear();
        Rectangle masterAreaBounds = PolygonExtensions.getBounds(masterArea.getAreas());
        mapViewGenerator.setGeoCenter(RectangleExtensions.getCenter(masterAreaBounds), NullProgressTracker.NULL, null);
        mapViewGenerator.setViewPortSize(
                new Dimension((int)(masterAreaBounds.width * 1.2), (int)(masterAreaBounds.height * 1.2)),
                NullProgressTracker.NULL, null);
        mapViewGenerator.setScale(1d, NullProgressTracker.NULL, null);
        MapView mapView = mapViewGenerator.getView();
        Map<Polygon[], Integer> identifiers =
                new HashMap<Polygon[], Integer>(boundedAreas.size());
        Map<BoundedAreaType, List<Polygon[]>> groupedPolygons =
                new HashMap<BoundedAreaType, List<Polygon[]>>(boundedAreas.size());
        int index = 0;
        for (BoundedArea boundedArea : boundedAreas) {
            Polygon[] imagePolygons = getImagePolygons(boundedArea.getAreas(), mapView);
            identifiers.put(imagePolygons, (++index));
            LOG.debug("{}: {}", new Object[] {index, boundedArea.getName()});
            List<Polygon[]> polygons = groupedPolygons.get(boundedArea.getBoundedAreaType());
            if (polygons == null) {
                polygons = new ArrayList<Polygon[]>();
                groupedPolygons.put(boundedArea.getBoundedAreaType(), polygons);
            }
            polygons.add(imagePolygons);
        }
        BoundedAreaType[] boundedAreaTypes = masterArea.getBoundedAreaType().getAllPossibleDecendentTypes();
        BufferedImage map = mapView.getMap();
        Graphics2D g = (Graphics2D)map.getGraphics();
        g.setColor(new Color(255, 255, 255, 228));
        g.fillRect(0, 0, map.getWidth(), map.getHeight());
        int stroke = 20;
        Font font = g.getFont();
        int fontSize = 72;
        BasicStroke boundaryStroke;
        Stroke originalStroke = g.getStroke();
        HashMap<String, Point2D.Float> drawAt = calculateDrawAtLocations(
                g, font, fontSize, boundedAreaTypes, groupedPolygons, identifiers);
        reverse(boundedAreaTypes);
        for (BoundedAreaType boundedAreaType : boundedAreaTypes) {
            g.setColor(colours.get(boundedAreaType));
            boundaryStroke = new BasicStroke(stroke);
            fontSize *= 3;
            fontSize /= 2;
            g.setFont(new Font(font.getName(), font.getStyle(), fontSize));
            List<Polygon[]> allPolygons = groupedPolygons.get(boundedAreaType);
            if (allPolygons == null) {
                continue;
            }
            for (Polygon[] polygons : allPolygons) {
                g.setStroke(boundaryStroke);
                for (Polygon polygon : polygons) {
                    g.draw(polygon);
                }
                g.setStroke(originalStroke);
                String value = identifiers.get(polygons).toString();
                Point2D.Float drawStringAt = drawAt.get(value);
                g.drawString(value, drawStringAt.x, drawStringAt.y);
            }
        }
        return map;
    }

    private HashMap<String, Point2D.Float> calculateDrawAtLocations(Graphics2D g, Font font, int fontSize,
                        BoundedAreaType[] boundedAreaTypes, Map<BoundedAreaType, List<Polygon[]>> groupedPolygons,
                        Map<Polygon[], Integer> identifiers) {
        HashMap<String, Point2D.Float> drawAt = new HashMap<String, Point2D.Float>();
        for (BoundedAreaType boundedAreaType : boundedAreaTypes) {
            fontSize *= 3;
            fontSize /= 2;
            g.setFont(new Font(font.getName(), font.getStyle(), fontSize));
            List<Polygon[]> allPolygons = groupedPolygons.get(boundedAreaType);
            if (allPolygons == null) {
                continue;
            }
            for (Polygon[] polygons : allPolygons) {
                Point2D.Float centreOfGravity = PolygonExtensions.getCentreOfGravity(polygons);
                String value = identifiers.get(polygons).toString();
                drawAt.put(value, drawIndex(value, g, centreOfGravity, boundedAreaType));
            }
        }
        return drawAt;
    }

    ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();

    private Point2D.Float drawIndex(String value, Graphics2D g, Point2D.Float centreOfGravity, BoundedAreaType boundedAreaType) {
        Rectangle bounds = getPixelBounds(value, g);
        int x = (int)(centreOfGravity.x - bounds.width / 2f);
        int y = (int)(centreOfGravity.y - bounds.height / 2f);
        int offset = 0;
        offsetBreak: while(offset++ < Integer.MAX_VALUE) {
            Rectangle rectangle = new Rectangle(x - 15, y - offset - 15, bounds.width + 30, bounds.height + 30);
            for (Rectangle compare : rectangles) {
                if (compare.intersects(rectangle)) {
                    continue offsetBreak;
                }
            }
            break offsetBreak;
        }
        Rectangle rectangle = new Rectangle(x - 15, y - offset - 15, bounds.width + 30, bounds.height + 30);
        rectangles.add(rectangle);
        Point2D.Float drawFrom = new Point2D.Float(
                centreOfGravity.x - bounds.width / 2f - bounds.x,
                centreOfGravity.y - bounds.height / 2f - bounds.y);
        return new Point2D.Float(drawFrom.x, drawFrom.y - offset);
    }


    private Rectangle getPixelBounds(String string, Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        return g.getFont().createGlyphVector(frc, string).getPixelBounds(frc, 0, 0);
    }

    public static <T> void reverse(T[] t) {
        for (int i = 0; i < t.length / 2; i++) {
            T temp = t[i];
            t[i] = t[t.length - i - 1];
            t[t.length - i - 1] = temp;
        }
    }

    private Polygon getImagePolygon(Polygon geoPolygon, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        int[] xpoints = new int[geoPolygon.npoints];
        int[] ypoints = new int[geoPolygon.npoints];
        for (int i = 0; i < geoPolygon.npoints; i++) {
            Point imagePoint = imageAndGeoPointTranslator.getImageLocation(
                    new Point(geoPolygon.xpoints[i], geoPolygon.ypoints[i]));
            xpoints[i] = imagePoint.x;
            ypoints[i] = imagePoint.y;
        }
        return new Polygon(xpoints, ypoints, geoPolygon.npoints);
    }

    private Polygon[] getImagePolygons(Polygon[] geoPolygons, ImageAndGeoPointTranslator imageAndGeoPointTranslator) {
        Polygon[] imagePolygons = new Polygon[geoPolygons.length];
        for (int i = 0; i < imagePolygons.length; i++) {
            imagePolygons[i] = getImagePolygon(geoPolygons[i], imageAndGeoPointTranslator);
        }
        return imagePolygons;
    }

    private Map<BoundedArea, int[]>
                countDwellings(BoundedArea boundedArea, Collection<? extends PostcodeDatum> postcodes) {
        LOG.debug("{}: {}", new Object[] {boundedArea.getName(), postcodes.size()});
        HashMap<BoundedArea, int[]> countMap = new HashMap<BoundedArea, int[]>();
        List<PostcodeDatum> containedPostcodes = new ArrayList<PostcodeDatum>(postcodes.size());
        int[] count = new int[10];
        for (PostcodeDatum postcodeDatum : postcodes) {
            if (postcodeDatum.getPoint() == null) {
                LOG.debug("{}", postcodeDatum.getPostcode());
                continue;
            }
            if (PolygonExtensions.contains(boundedArea.getAreas(), postcodeDatum.getPoint())) {
                containedPostcodes.add(postcodeDatum);
                int[] dwellings = postcodeDatum.getCouncilBandCount();
                for (int i = 0; i < 9; i++) {
                    count[i] += dwellings[i];
                    count[9] += dwellings[i];
                }
            }
        }
        countMap.put(boundedArea, count);
        for (BoundedArea child : boundedArea.getChildren()) {
            countMap.putAll(countDwellings(child, containedPostcodes));
        }
        return countMap;
    }
}
