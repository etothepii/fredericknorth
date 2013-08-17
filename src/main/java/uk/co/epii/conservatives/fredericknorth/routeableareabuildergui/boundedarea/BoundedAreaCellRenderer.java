package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui.boundedarea;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;

import javax.swing.*;
import java.awt.*;

/**
 * User: James Robinson
 * Date: 29/07/2013
 * Time: 14:00
 */
class BoundedAreaCellRenderer extends JLabel implements ListCellRenderer {

    private final Color backgroundSelection = new Color(128, 128, 255);

    private Component getListCellRendererComponent(JList list, BoundedArea value, int index, boolean isSelected,
                                                   boolean cellHasFocus) {
        setText(value == null ? "None" : value.getName());
        if (isSelected) {
            setOpaque(true);
            setBackground(backgroundSelection);
            setForeground(Color.WHITE);
        }
        else {
            setOpaque(false);
            setForeground(Color.BLACK);
        }
        return this;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value == null || value instanceof BoundedArea) {
            return getListCellRendererComponent(list, (BoundedArea)value, index, isSelected, cellHasFocus);
        }
        throw new IllegalArgumentException("Only values of type BoundedArea are supported");
    }
}
