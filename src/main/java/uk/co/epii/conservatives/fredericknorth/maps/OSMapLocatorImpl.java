package uk.co.epii.conservatives.fredericknorth.maps;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.BufferedResourceReader;

import java.awt.*;
import java.util.*;
import java.util.List;

class OSMapLocatorImpl implements OSMapLocator {

    private final String[][] squares;
    private final HashMap<String, Point> largeSquareBottomLefts = new HashMap<String, Point>();
    private final Map<OSMapType, Dimension> mapDimensions;
    private final Map<OSMapType, Dimension> representedMapDimensions;

    private static final Logger LOG = Logger.getLogger(OSMapLocator.class);

    OSMapLocatorImpl(String largeSquareLocatorResource, Map<OSMapType, Dimension> mapDimensions) {
        this.mapDimensions = mapDimensions;
        ArrayList<String[]> mapKeys = new ArrayList<String[]>();
        for (String line : new BufferedResourceReader(
                OSMapLocator.class.getResource(largeSquareLocatorResource))) {
            mapKeys.add(parse(line));
        }
        representedMapDimensions = new EnumMap<OSMapType, Dimension>(OSMapType.class);
        for (Map.Entry<OSMapType, Dimension> entry : mapDimensions.entrySet()) {
            OSMapType osMapType = entry.getKey();
            Dimension mapDimension = entry.getValue();
            representedMapDimensions.put(osMapType, new Dimension(
                    (int)(mapDimension.width / osMapType.getScale()),
                    (int)(mapDimension.height / osMapType.getScale())));
        }
        squares = mapKeys.toArray(new String[0][]);
        for (int y = 0; y < squares.length; y++) {
            for (int x = 0; x < squares[y].length; x++) {
                largeSquareBottomLefts.put(squares[y][x], new Point(x * 100000, y * 100000));
            }
        }
    }

    private String[] parse(String in) {
        String[] squares = new String[in.length() / 2];
        for (int i = 0; i < in.length(); i += 2) {
            squares[i / 2] = in.charAt(i) + "" + in.charAt(i + 1);
            if (squares[i / 2].equals("  ")) squares[i / 2] = null;
        }
        return squares;
    }

    @Override
    public OSMap getMap(OSMapType osMapType, Point p) {
        String largeSquare = calculateLargeSquare(p);
        if (largeSquare == null) return null;
        if (osMapType == OSMapType.MINI) {
            return new OSMapImpl(OSMapType.MINI, largeSquare, null, null, null, null);
        }
        int square = calculateSquare(p);
        switch (osMapType) {
            case RASTER:
                return new OSMapImpl(OSMapType.RASTER, largeSquare, square, null, null, null);
            case VECTOR_MAP:
                int squareHundredth = calcuateSquareHundredth(p);
                return new OSMapImpl(OSMapType.VECTOR_MAP, largeSquare, square, null, squareHundredth, null);
            case STREET_VIEW:
                String quadrant = calculateQuadrant(p);
                int quadrantHundredth = calculateQuadrantHundredth(p);
                return new OSMapImpl(OSMapType.STREET_VIEW, largeSquare, square, quadrant, null, quadrantHundredth);
        }
        throw new RuntimeException("Some how you have fallend out of a case statement that hit every possible combination of values");
    }

    private String calculateQuadrant(Point p) {
        boolean westernHalf = (p.x % 10000) < 5000;
        boolean southernHalf = (p.y % 10000) < 5000;
        return (southernHalf ? "s" : "n") + (westernHalf ? "w" : "e");
    }

    private int calcuateSquareHundredth(Point p) {
        int x = (p.x % 10000) / 1000;
        int y = (p.y % 10000) / 1000;
        return x * 10 + y;
    }

    private int calculateQuadrantHundredth(Point p) {
        int x = (p.x % 5000) / 500;
        int y = (p.y % 5000) / 500;
        return x * 10 + y;
    }

    private String calculateLargeSquare(Point p) {
        int x = p.x / 100000;
        int y = p.y / 100000;
        return squares[y][x];
    }

    private int calculateSquare(Point p) {
        int x = (p.x % 100000) / 10000;
        int y = (p.y % 100000) / 10000;
        return x * 10 + y;
    }

    @Override
    public Set<OSMap> getMaps(OSMapType osMapType, Rectangle r) {
        Set<OSMap> maps = new HashSet<OSMap>();
        Point bottomLeft = getBottomLeftMapCoordinate(getMap(osMapType, r.getLocation()));
        Point bottomLeftOfTopRight = getBottomLeftMapCoordinate(getMap(osMapType, new Point(r.x + r.width, r.y + r.height)));
        Dimension representedDimension = representedMapDimensions.get(osMapType);
        for (int x = bottomLeft.x; x <= bottomLeftOfTopRight.x; x += representedDimension.width) {
            for (int y = bottomLeft.y; y <= bottomLeftOfTopRight.y; y += representedDimension.height) {
                OSMap map = getMap(osMapType, new Point(x, y));
                if (!maps.contains(map)) {
                    maps.add(map);
                    LOG.info("Adding map: " + map.toString());
                }
            }
        }
        return maps;
    }

    @Override
    public Point getBottomLeftMapCoordinate(OSMap map) {
        Point bottomLeft = new Point(largeSquareBottomLefts.get(map.getLargeSquare()));
        if (map.getOSMapType() == OSMapType.MINI) {
            return bottomLeft;
        }
        bottomLeft.x += (map.getSquare() / 10) * 10000;
        bottomLeft.y += (map.getSquare() % 10) * 10000;
        switch (map.getOSMapType()) {
            case VECTOR_MAP:
                bottomLeft.x += (map.getSquareHundredth() / 10) * 1000;
                bottomLeft.y += (map.getSquareHundredth() % 10) * 1000;
                break;
            case STREET_VIEW:
                bottomLeft.x += map.getQuadrant().charAt(1) == 'e' ? 5000 : 0;
                bottomLeft.y += map.getQuadrant().charAt(0) == 'n' ? 5000 : 0;
                bottomLeft.x += (map.getQuadrantHundredth() / 10) * 500;
                bottomLeft.y += (map.getQuadrantHundredth() % 10) * 500;
        }
        return bottomLeft;
    }

    @Override
    public Dimension getImageSize(OSMapType osMapType) {
        return mapDimensions.get(osMapType);
    }

    @Override
    public Dimension getRepresentedSize(OSMapType osMapType) {
        return representedMapDimensions.get(osMapType);
    }
}
