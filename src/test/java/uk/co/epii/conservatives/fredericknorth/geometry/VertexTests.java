package uk.co.epii.conservatives.fredericknorth.geometry;

import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 23/02/2014
 * Time: 15:20
 */
public class VertexTests {

    @Test
    public void precisionArccosIssues() {
        Vertex v = new Vertex(
                new Edge(null, new Point2D.Float(537554.0f, 184591.0f), new Point2D.Float(537578.0f, 184588.0f)),
                new Edge(null, new Point2D.Float(537578.0f, 184588.0f), new Point2D.Float(537570.0f, 184589.0f)));
        double result = v.getNonReflexAngle();
        double expected = 0;
        assertEquals(expected, result, 0.00000000001);
    }
}
