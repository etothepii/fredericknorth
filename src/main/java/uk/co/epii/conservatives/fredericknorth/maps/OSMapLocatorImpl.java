package uk.co.epii.conservatives.fredericknorth.maps;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.BufferedResourceReader;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class OSMapLocatorImpl implements OSMapLocator {

    private final String[][] squares;
    private final HashMap<String, Point> largeSquareBottomLefts = new HashMap<String, Point>();

    private static final Logger LOG = Logger.getLogger(OSMapLocator.class);

    OSMapLocatorImpl(String largeSquareLocatorResource) {
        ArrayList<String[]> mapKeys = new ArrayList<String[]>();
        for (String line : new BufferedResourceReader(
                OSMapLocator.class.getResource(largeSquareLocatorResource))) {
            mapKeys.add(parse(line));
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
    public OSMap getMap(Point p) {
        int x = p.x / 100000;
        int y = p.y / 100000;
        String largeSquare = squares[y][x];
        x = (p.x % 100000) / 10000;
        y = (p.y % 100000) / 10000;
        int square = x * 10 + y;
        boolean westernHalf = (p.x % 10000) < 5000;
        boolean southernHalf = (p.y % 10000) < 5000;
        String quadrant = (southernHalf ? "s" : "n") + (westernHalf ? "w" : "e");
        return new OSMapImpl(largeSquare, square, quadrant);
    }

    @Override
    public List<OSMap> getMaps(Rectangle r) {
        List<OSMap> maps = new ArrayList<OSMap>();
        Point bottomLeft = getBottomLeftMapCoordinate(getMap(r.getLocation()));
        Point bottomLeftOfTopRight = getBottomLeftMapCoordinate(getMap(new Point(r.x + r.width, r.y + r.height)));
        for (int x = bottomLeft.x; x <= bottomLeftOfTopRight.x; x += 5000) {
            for (int y = bottomLeft.y; y <= bottomLeftOfTopRight.y; y += 5000) {
                OSMap map = getMap(new Point(x, y));
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
        Point bottomLeft = largeSquareBottomLefts.get(map.getLargeSquare());
        bottomLeft = new Point(bottomLeft.x + (map.getSquare() / 10) * 10000, bottomLeft.y + (map.getSquare() % 10) * 10000);
        return new Point(bottomLeft.x + (map.getQuadrant().contains("e") ? 5000 : 0),
                bottomLeft.y + (map.getQuadrant().contains("n") ? 5000 : 0));
    }

    @Override
    public OSMap create(String largeSquare, int square, String quadrant) {
        return new OSMapImpl(largeSquare, square, quadrant);
    }
}
