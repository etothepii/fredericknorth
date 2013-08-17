package uk.co.epii.conservatives.fredericknorth.maps;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 21:10
 */
class MapLabelImpl implements MapLabel {

    private final String name;
    private final Rectangle rectangle;
    private final Corner corner;
    private final Font labelFont;
    private final int padding;
    private final int dotRadius;

    MapLabelImpl(String name, Rectangle rectangle, Corner corner, Font labelFont, int padding, int dotRadius) {
        this.name = name;
        this.rectangle = rectangle;
        this.corner = corner;
        this.labelFont = labelFont;
        this.padding = padding;
        this.dotRadius = dotRadius;
    }

    @Override
    public Corner getCorner() {
        return corner;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void paint(Graphics g) {
        g.setFont(labelFont);
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(new Color(255, 255, 255, 192));
        g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        g.setColor(Color.BLACK);
        g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        g.drawString(name, rectangle.x + padding + fontMetrics.getLeading(), rectangle.y + padding + fontMetrics.getAscent());
        drawDot(g, corner.getCorner(rectangle));
    }

    private void drawDot(Graphics g, Point p) {
        g.setColor(Color.YELLOW);
        g.fillOval(p.x - dotRadius, p.y - dotRadius, dotRadius * 2, dotRadius * 2);
        int smallerRadius = dotRadius * 4 / 5;
        g.setColor(Color.RED);
        g.fillOval(p.x - smallerRadius, p.y - smallerRadius, smallerRadius * 2, smallerRadius * 2);
    }
}
