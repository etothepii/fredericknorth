package uk.co.epii.conservatives.fredericknorth.geometry;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 09/09/2013
 * Time: 00:30
 */
public class ClippedPolygonFactory {

    private final Rectangle clip;
    private final TreeMap<Point, ClippedSegment> internalSegments;
    private final Comparator<Point> clockwiseComparator;

    boolean clockwise;
    Shape[] polygons = null;

    public ClippedPolygonFactory(Polygon polygon, Rectangle clip, List<ClippedSegment> clippedSegments) {
        this.clip = clip;
        clockwiseComparator = new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return clockwiseDistance(o1) - clockwiseDistance(o2);
            }
        };
        this.internalSegments = new TreeMap<Point, ClippedSegment>(clockwiseComparator);
        clockwise = PolygonExtensions.isClockwise(polygon);
        for (ClippedSegment clippedSegment : clippedSegments) {
            if (clippedSegment.isInside()) {
                if (internalSegments.containsKey(clippedSegment.first())) {
                    throw new UnsupportedOperationException("Unable to build factory as multiple points " +
                            "hit the boundary at the same point");
                }
                this.internalSegments.put(clippedSegment.first(), clippedSegment);
            }
        }
    }

    private int clockwiseDistance(Point point) {
        int distance = 0;
        int edge = RectangleExtensions.getEdge(clip, point);
        if (edge == SwingConstants.NORTH_WEST) {
            return distance;
        }
        if (edge == SwingConstants.NORTH) {
            return distance + point.x - clip.x;
        }
        distance += clip.width;
        if (edge == SwingConstants.NORTH_EAST) {
            return distance;
        }
        if (edge == SwingConstants.EAST) {
            return distance + point.y - clip.y;
        }
        distance += clip.height;
        if (edge == SwingConstants.SOUTH_EAST) {
            return distance;
        }
        if (edge == SwingConstants.SOUTH) {
            return distance + clip.x + clip.width - point.x;
        }
        distance += clip.width;
        if (edge == SwingConstants.SOUTH_WEST) {
            return distance;
        }
        if (edge == SwingConstants.WEST) {
            return distance + clip.y + clip.height - point.y;
        }
        throw new IllegalArgumentException(String.format("Point %s not on edge of %s", point.toString(), clip.toString()));
    }

    public Shape[] build() {
        if (polygons == null) {
            List<Shape> polygons = new ArrayList<Shape>();
            polygons.add(getNextPolygon());
            while (!internalSegments.isEmpty()) {
                polygons.add(getNextPolygon());
            }
            this.polygons = polygons.toArray(new Shape[polygons.size()]);
        }
        return polygons;
    }

    private Polygon getNextPolygon() {
        List<Point> points = new ArrayList<Point>();
        ClippedSegment initialSegment = removeNextClippedSegment(new Point(clip.x, clip.y));
        Point first = initialSegment.first();
        Point last = initialSegment.last();
        points.addAll(initialSegment.getPoints());
        while (!internalSegments.isEmpty()) {
            ClippedSegment clippedSegment = getNextClippedSegment(last);
            if (isCBetweenAAndB(last, clippedSegment.first(), first)) {
                break;
            }
            removeNextClippedSegment(last);
            points.addAll(Arrays.asList(getPointsBetween(last, clippedSegment.first())));
            points.addAll(clippedSegment.getPoints());
            last = clippedSegment.last();
        }
        points.addAll(Arrays.asList(getPointsBetween(last, first)));
        return PolygonExtensions.removeRedundancies(PolygonExtensions.construct(points));
    }

    private Point[] getPointsBetween(Point a, Point b) {
        if (clockwise) {
            return getClockwisePointsBetween(a, b);
        }
        return getAnticlockwisePointsBetween(a, b);
    }

    private ClippedSegment removeNextClippedSegment(Point last) {
        if (clockwise) {
            return removeNextClockwiseClippedSegment(last);
        }
        else {
            return removeNextAnticlockwiseClippedSegment(last);
        }
    }


    private ClippedSegment removeNextClockwiseClippedSegment(Point last) {
        Point next = internalSegments.higherKey(last);
        if (next == null) {
            next = internalSegments.firstKey();
        }
        return internalSegments.remove(next);
    }

    private ClippedSegment removeNextAnticlockwiseClippedSegment(Point last) {
        Point previous = internalSegments.lowerKey(last);
        if (previous == null) {
            previous = internalSegments.lastKey();
        }
        return internalSegments.remove(previous);
    }

    private ClippedSegment getNextClippedSegment(Point last) {
        if (clockwise) {
            return getNextClockwiseClippedSegment(last);
        }
        else {
            return getNextAnticlockwiseClippedSegment(last);
        }
    }


    private ClippedSegment getNextClockwiseClippedSegment(Point last) {
        Point next = internalSegments.higherKey(last);
        if (next == null) {
            next = internalSegments.firstKey();
        }
        return internalSegments.get(next);
    }

    private ClippedSegment getNextAnticlockwiseClippedSegment(Point last) {
        Point previous = internalSegments.lowerKey(last);
        if (previous == null) {
            previous = internalSegments.lastKey();
        }
        return internalSegments.get(previous);
    }

    private Point[] getAnticlockwisePointsBetween(Point a, Point b) {
        Point[] points = getClockwisePointsBetween(b, a);
        Point[] reversed = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            reversed[points.length - 1 - i] = points[i];
        }
        return reversed;
    }

    private Point[] getClockwisePointsBetween(Point a, Point b) {
        if (a.equals(b)) {
            return new Point[0];
        }
        int edgeA = RectangleExtensions.getEdge(clip, a);
        int edgeB = RectangleExtensions.getEdge(clip, b);
        if (edgeA == edgeB) {
            switch (edgeA) {
                case SwingConstants.NORTH:
                    if (a.x < b.x) {
                        return new Point[0];
                    }
                    return new Point[] {
                            getCorner(SwingConstants.NORTH_WEST), getCorner(SwingConstants.SOUTH_WEST),
                            getCorner(SwingConstants.SOUTH_EAST), getCorner(SwingConstants.NORTH_EAST)
                    };
                case SwingConstants.EAST:
                    if (a.y < b.y) {
                        return new Point[0];
                    }
                    return new Point[] {
                            getCorner(SwingConstants.NORTH_EAST), getCorner(SwingConstants.NORTH_WEST),
                            getCorner(SwingConstants.SOUTH_WEST), getCorner(SwingConstants.SOUTH_EAST)
                    };
                case SwingConstants.SOUTH:
                    if (a.x > b.x) {
                        return new Point[0];
                    }
                    return new Point[] {
                            getCorner(SwingConstants.SOUTH_EAST), getCorner(SwingConstants.NORTH_EAST),
                            getCorner(SwingConstants.NORTH_WEST), getCorner(SwingConstants.SOUTH_WEST)
                    };
                case SwingConstants.WEST:
                    if (a.y > b.y) {
                        return new Point[0];
                    }
                    return new Point[] {
                            getCorner(SwingConstants.SOUTH_WEST), getCorner(SwingConstants.SOUTH_EAST),
                            getCorner(SwingConstants.NORTH_EAST), getCorner(SwingConstants.NORTH_WEST)
                    };
            }
        }
        List<Point> points = new ArrayList<Point>(4);
        switch (edgeA) {
            case SwingConstants.NORTH_WEST:
            case SwingConstants.NORTH:
                if (edgeB == SwingConstants.NORTH || edgeB == SwingConstants.NORTH_EAST) {
                    return points.toArray(new Point[0]);
                }
                points.add(getCorner(SwingConstants.NORTH_EAST));
                if (edgeB == SwingConstants.EAST || edgeB == SwingConstants.SOUTH_EAST) {
                    return points.toArray(new Point[1]);
                }
                points.add(getCorner(SwingConstants.SOUTH_EAST));
                if (edgeB == SwingConstants.SOUTH || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[2]);
                }
                points.add(getCorner(SwingConstants.SOUTH_WEST));
                if (edgeB == SwingConstants.WEST || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[3]);
                }
                points.add(getCorner(SwingConstants.NORTH_WEST));
                return points.toArray(new Point[4]);
            case SwingConstants.NORTH_EAST:
            case SwingConstants.EAST:
                if (edgeB == SwingConstants.EAST || edgeB == SwingConstants.SOUTH_EAST) {
                    return points.toArray(new Point[0]);
                }
                points.add(getCorner(SwingConstants.SOUTH_EAST));
                if (edgeB == SwingConstants.SOUTH || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[1]);
                }
                points.add(getCorner(SwingConstants.SOUTH_WEST));
                if (edgeB == SwingConstants.WEST || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[2]);
                }
                points.add(getCorner(SwingConstants.NORTH_WEST));
                if (edgeB == SwingConstants.NORTH || edgeB == SwingConstants.NORTH_EAST) {
                    return points.toArray(new Point[3]);
                }
                points.add(getCorner(SwingConstants.NORTH_EAST));
                return points.toArray(new Point[4]);
            case SwingConstants.SOUTH_EAST:
            case SwingConstants.SOUTH:
                if (edgeB == SwingConstants.SOUTH || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[0]);
                }
                points.add(getCorner(SwingConstants.SOUTH_WEST));
                if (edgeB == SwingConstants.WEST || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[1]);
                }
                points.add(getCorner(SwingConstants.NORTH_WEST));
                if (edgeB == SwingConstants.NORTH || edgeB == SwingConstants.NORTH_EAST) {
                    return points.toArray(new Point[2]);
                }
                points.add(getCorner(SwingConstants.NORTH_EAST));
                if (edgeB == SwingConstants.EAST || edgeB == SwingConstants.SOUTH_EAST) {
                    return points.toArray(new Point[3]);
                }
                points.add(getCorner(SwingConstants.SOUTH_EAST));
                return points.toArray(new Point[4]);
            case SwingConstants.SOUTH_WEST:
            case SwingConstants.WEST:
                if (edgeB == SwingConstants.WEST || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[0]);
                }
                points.add(getCorner(SwingConstants.NORTH_WEST));
                if (edgeB == SwingConstants.NORTH || edgeB == SwingConstants.NORTH_EAST) {
                    return points.toArray(new Point[1]);
                }
                points.add(getCorner(SwingConstants.NORTH_EAST));
                if (edgeB == SwingConstants.EAST || edgeB == SwingConstants.SOUTH_EAST) {
                    return points.toArray(new Point[2]);
                }
                points.add(getCorner(SwingConstants.SOUTH_EAST));
                if (edgeB == SwingConstants.SOUTH || edgeB == SwingConstants.SOUTH_WEST) {
                    return points.toArray(new Point[3]);
                }
                points.add(getCorner(SwingConstants.SOUTH_WEST));
                return points.toArray(new Point[4]);
        }
        throw new IllegalArgumentException("At least one of the points provided is not on the edge of the clip.");
    }

    private Point getCorner(int corner) {
        switch (corner) {
            case SwingConstants.NORTH_WEST:
                return new Point(clip.x, clip.y);
            case SwingConstants.NORTH_EAST:
                return new Point(clip.x + clip.width, clip.y);
            case SwingConstants.SOUTH_EAST:
                return new Point(clip.x + clip.width, clip.y + clip.height);
            case SwingConstants.SOUTH_WEST:
                return new Point(clip.x, clip.y + clip.height);
            default:
                throw new IllegalArgumentException("Not a corner");
        }
    }

    private boolean isCBetweenAAndB(Point a, Point b, Point c) {
        if (clockwise) {
            return isCBetweenAAndBClockwise(a, b, c);
        }
        return isCBetweenAAndBAnticlockwise(a, b, c);
    }

    private boolean isCBetweenAAndBAnticlockwise(Point a, Point b, Point c) {
        return !isCBetweenAAndBClockwise(a, b, c);
    }

    private boolean isCBetweenAAndBClockwise(Point a, Point b, Point c) {
        int da = clockwiseDistance(a);
        int db = clockwiseDistance(b);
        int dc = clockwiseDistance(c);
        if (db < da) db += clip.width * 2 + clip.height * 2;
        if (dc < da) dc += clip.width * 2 + clip.height * 2;
        return dc < db;
    }




}
