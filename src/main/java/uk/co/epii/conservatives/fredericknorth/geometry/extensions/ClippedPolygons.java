package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import uk.co.epii.conservatives.fredericknorth.geometry.ClippedSegment;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 24/02/2014
 * Time: 01:29
 */
public class ClippedPolygons {

    public final Shape[] clips;
    public final Point[][] firstAndLastWobblySegments;

    public ClippedPolygons(Point[][] firstAndLastWobblySegments, Shape[] clips) {
        this.firstAndLastWobblySegments = firstAndLastWobblySegments;
        this.clips = clips;
    }

    public Boolean contains(Point p, double tolerence) {
        for (Point[] edge : firstAndLastWobblySegments) {
            if (PointExtensions.isNearEdge(edge[0], edge[1], p, tolerence)) {
                return null;
            }
        }
        for (Shape clip : clips) {
            if (clip.contains(p)) {
                return true;
            }
        }
        return false;
    }

}
