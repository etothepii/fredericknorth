package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * User: James Robinson
 * Date: 03/09/2013
 * Time: 22:55
 */
public class RectangleCollection implements Collection<Rectangle> {

    private final ArrayList<Rectangle> list;

    public RectangleCollection() {
        list = new ArrayList<Rectangle>();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Rectangle && contains((Rectangle) o);
    }

    private boolean contains(Rectangle o) {
        long intersectionSize = 0;
        for (Rectangle rectangle : getIntersections(o)) {
            intersectionSize += rectangle.width * (long)rectangle.height;
        }
        return o.width * (long)o.height == intersectionSize;
    }

    @Override
    public Iterator<Rectangle> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(Rectangle rectangle) {
        return addAllWithoutIntersectionCheck(
                RectangleExtensions.getSurrounding(rectangle, getIntersections(rectangle)));
    }

    private boolean addWithoutIntersectionCheck(Rectangle toAdd) {
        if (toAdd.width == 0 || toAdd.height == 0) {
            return false;
        }
        int j = list.size() - 1;
        outerLoop: while (j >= 0) {
            for (j = list.size() - 1; j >= 0; j--) {
                Rectangle addTo = list.get(j);
                if (toAdd.width == addTo.width && toAdd.x == addTo.x) {
                    if (toAdd.y + toAdd.height == addTo.y || addTo.y + addTo.height == toAdd.y) {
                        list.remove(j);
                        toAdd = new Rectangle(toAdd.x, Math.min(toAdd.y, addTo.y), toAdd.width, toAdd.height + addTo.height);
                        continue outerLoop;
                    }
                }
                else if (toAdd.height == addTo.height && toAdd.y == addTo.y) {
                    if (toAdd.x + toAdd.width == addTo.x || addTo.x + addTo.width == toAdd.x) {
                        list.remove(j);
                        toAdd = new Rectangle(Math.min(toAdd.x, addTo.x), toAdd.y, toAdd.width + addTo.width, toAdd.height);
                        continue outerLoop;
                    }
                }
            }
        }
        return true;
    }

    private boolean remove(Rectangle toRemove) {
        long coverage = getCoverage();
        for (Rectangle removeFrom : removeIntersecting(toRemove)) {
            for (Rectangle residue : RectangleExtensions.getSurrounding(removeFrom, removeFrom.intersection(toRemove))) {
                add(residue);
            }
        }
        return coverage != getCoverage();
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Rectangle) {
            return remove((Rectangle)o);
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (!contains(object)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Rectangle> c) {
        boolean changed = false;
        for (Rectangle rectangle : c) {
            changed |= add(rectangle);
        }
        return changed;
    }

    private boolean addAllWithoutIntersectionCheck(Collection<? extends Rectangle> c) {
        boolean changed = false;
        for (Rectangle rectangle : c) {
            changed |= addWithoutIntersectionCheck(rectangle);
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object object : c) {
            changed |= remove(object);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Rectangle> old = new ArrayList<Rectangle>(list);
        long startCoverage = getCoverage();
        list.clear();
        for (Object o : c) {
            if (!(o instanceof Rectangle)) {
                continue;
            }
            for (Rectangle rectangle : old) {
                Rectangle intersection = rectangle.intersection((Rectangle)o);
                if (intersection.width * (long)intersection.height > 0) {
                    add(intersection);
                }
            }
        }
        if (old.size() != list.size()) return false;
        return !(startCoverage == getCoverage());
    }

    @Override
    public void clear() {
        list.clear();
    }

    private List<Rectangle> getIntersections(Rectangle master) {
        ArrayList<Rectangle> intersections = new ArrayList<Rectangle>(list.size());
        for (Rectangle rectangle : list) {
            Rectangle intersection = master.intersection(rectangle);
            if (intersection.width * (long)intersection.height > 0) {
                intersections.add(intersection);
            }
        }
        return intersections;
    }

    private List<Rectangle> removeIntersecting(Rectangle master) {
        ArrayList<Rectangle> intersecting = new ArrayList<Rectangle>(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            Rectangle intersection = master.intersection(list.get(i));
            if (intersection.width * (long)intersection.height > 0) {
                intersecting.add(list.remove(i));
            }
        }
        return intersecting;
    }

    public long getCoverage() {
        long totalArea = 0;
        for (Rectangle rectangle : list) {
            totalArea += rectangle.width * (long)rectangle.height;
        }
        return totalArea;
    }
}
