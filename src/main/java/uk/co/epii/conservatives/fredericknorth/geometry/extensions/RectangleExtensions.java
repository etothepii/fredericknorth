package uk.co.epii.conservatives.fredericknorth.geometry.extensions;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 24/08/2013
 * Time: 17:17
 */
public class RectangleExtensions {

    public static Point getCenter(Rectangle rectangle) {
        return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
    }

}
