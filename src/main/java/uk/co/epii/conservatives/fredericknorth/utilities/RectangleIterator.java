package uk.co.epii.conservatives.fredericknorth.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Iterator;

/**
 * User: James Robinson
 * Date: 28/09/2014
 * Time: 23:32
 */
public class RectangleIterator implements Iterable<Rectangle>, Iterator<Rectangle> {

  private static final Logger LOG = LoggerFactory.getLogger(RectangleIterator.class);

  private Rectangle bounds;
  private int sqrt;
  private int x, y;

  public RectangleIterator(Rectangle bounds, int parts) {
    LOG.debug("bounds: {}", bounds);
    this.bounds = bounds;
    sqrt = (int)Math.sqrt(parts);
    if (parts != sqrt * sqrt) {
      throw new IllegalArgumentException("The parts must be a square number");
    }
    if (bounds.width < sqrt || bounds.height < sqrt)
    {
      throw new IllegalArgumentException("The rectanglenmust be wider and taller than the sqrt of the number of parts");
    }
  }

  @Override
  public Iterator<Rectangle> iterator() {
    x = 0;
    y = 0;
    return this;
  }

  @Override
  public boolean hasNext() {
    return y < sqrt - 1 || x < sqrt;
  }

  @Override
  public Rectangle next() {
    if (x == sqrt) {
      x = 0;
      y++;
    }
    int X = x * bounds.width / sqrt;
    int Y = y * bounds.height / sqrt;
    int X_1 = (x + 1) * bounds.width / sqrt;
    int Y_1 = (y + 1) * bounds.height / sqrt;
    x++;
    Rectangle r = new Rectangle(bounds.x + X, bounds.y + Y, X_1 - X, Y_1 - Y);
    LOG.debug("next: {}", r);
    return r;
  }

  @Override
  public void remove() {

  }
}
