package uk.co.epii.conservatives.fredericknorth.utilities;

import org.junit.Test;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: James Robinson
 * Date: 28/09/2014
 * Time: 23:43
 */
public class RectangleIteratorTest {

  @Test
  public void hitsEverywhereInNonEvenRectangle() {
    Rectangle bounds = new Rectangle(-2,4,15,61);
    int area = bounds.width * bounds.height;
    Set<Rectangle> set = new HashSet<Rectangle>();
    for (Rectangle rectangle : new RectangleIterator(bounds, 16)) {
      for (Rectangle other : set) {
        if (rectangle.intersects(other)) {
          fail("Rectangle " + rectangle.toString() + " intersects with " + other.toString());
        }
      }
      set.add(rectangle);
      area -= rectangle.width * rectangle.height;
    }
    assertEquals("Checking all area covered", 0, area);
  }
}
