package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.geometry.Edge;
import uk.co.epii.conservatives.fredericknorth.geometry.Vertex;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 23/07/2013
 * Time: 23:46
 */
public class VertexExtensionsTest {

    private Vertex[] vertices = new Vertex[] {
            new Vertex(new Edge(null, new Point(3,4),new Point(3,1)),new Edge(null, new Point(3,1),new Point(1,1))), // 0
            new Vertex(new Edge(null, new Point(3,1),new Point(1,1)),new Edge(null, new Point(1,1),new Point(1,2))), // 1
            new Vertex(new Edge(null, new Point(1,1),new Point(1,2)),new Edge(null, new Point(1,2),new Point(2,2))), // 2
            new Vertex(new Edge(null, new Point(1,2),new Point(2,2)),new Edge(null, new Point(2,2),new Point(2,3))), // 3
            new Vertex(new Edge(null, new Point(2,2),new Point(2,3)),new Edge(null, new Point(2,3),new Point(1,3))), // 4
            new Vertex(new Edge(null, new Point(2,3),new Point(1,3)),new Edge(null, new Point(1,3),new Point(1,4))), // 5
            new Vertex(new Edge(null, new Point(1,3),new Point(1,4)),new Edge(null, new Point(1,4),new Point(3,4))), // 6
            new Vertex(new Edge(null, new Point(1,4),new Point(3,4)),new Edge(null, new Point(3,4),new Point(3,1))), // 7
            new Vertex(new Edge(null, new Point(1,2),new Point(2,2)),new Edge(null, new Point(2,2),new Point(3,1))), // 8
            new Vertex(new Edge(null, new Point(2,2),new Point(3,1)),new Edge(null, new Point(3,1),new Point(1,1))), // 9
            new Vertex(new Edge(null, new Point(2,2),new Point(2,3)),new Edge(null, new Point(2,3),new Point(3,4))), //10
            new Vertex(new Edge(null, new Point(2,3),new Point(3,4)),new Edge(null, new Point(3,4),new Point(3,1))), //11
            new Vertex(new Edge(null, new Point(3,4),new Point(3,1)),new Edge(null, new Point(3,1),new Point(2,2))), //12
            new Vertex(new Edge(null, new Point(3,1),new Point(2,2)),new Edge(null, new Point(2,2),new Point(2,3))), //13
            new Vertex(new Edge(null, new Point(1,3),new Point(2,3)),new Edge(null, new Point(2,3),new Point(3,4))), //14
            new Vertex(new Edge(null, new Point(2,3),new Point(3,4)),new Edge(null, new Point(3,4),new Point(1,4)))  //15
    };

    @Test
    public void getRightAngleTest1() {
        assertEquals(    Math.PI / 2, vertices[ 0].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest2() {
        assertEquals(    Math.PI / 2, vertices[ 1].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest3() {
        assertEquals(    Math.PI / 2, vertices[ 2].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest4() {
        assertEquals(3 * Math.PI / 2, vertices[ 3].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest5() {
        assertEquals(3 * Math.PI / 2, vertices[ 4].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest6() {
        assertEquals(    Math.PI / 2, vertices[ 5].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest7() {
        assertEquals(    Math.PI / 2, vertices[ 6].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest8() {
        assertEquals(    Math.PI / 2, vertices[ 7].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest9() {
        assertEquals(3 * Math.PI / 4, vertices[ 8].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest10() {
        assertEquals(    Math.PI / 4, vertices[ 9].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest11() {
        assertEquals(3 * Math.PI / 4, vertices[10].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest12() {
        assertEquals(    Math.PI / 4, vertices[11].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest13() {
        assertEquals(    Math.PI / 4, vertices[12].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest14() {
        assertEquals(3 * Math.PI / 4, vertices[13].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest15() {
        assertEquals(5 * Math.PI / 4, vertices[14].getRightAngle(), 0.000001d);
    }

    @Test
    public void getRightAngleTest16() {
        assertEquals(7 * Math.PI / 4, vertices[15].getRightAngle(), 0.000001d);
    }

}
