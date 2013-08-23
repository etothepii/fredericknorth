package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * User: James Robinson
 * Date: 06/07/2013
 * Time: 14:29
 */
abstract class LeftRightRenderer extends JPanel implements ListCellRenderer {

    private static Color FOREGROUND = Color.BLACK;
    private static Color FOREGROUND_SELECTED = Color.WHITE;
    private static Color BACKGROUND = Color.WHITE;
    private static Color BACKGROUND_SELECTED = new Color(0, 98, 182);
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private JLabel left;
    private JLabel right;
    protected NumberFormat integerNumberFormat = NumberFormat.getInstance();

    public LeftRightRenderer() {
        super(new GridBagLayout());
        left = new JLabel();
        right = new JLabel();
        add(left, new GridBagConstraints(0, 0, 1, 1, 1d, 0d, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 2, 0, 5), 0, 0));
        add(right, new GridBagConstraints(1, 0, 1, 1, 0d, 0d, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 2), 0, 0));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        String[] leftAndRight = getLeftAndRight(value);
        left.setText(leftAndRight[LEFT]);
        right.setText(leftAndRight[RIGHT]);
        setBackground(isSelected ? BACKGROUND_SELECTED : BACKGROUND);
        setOpaque(isSelected);
        setForeground(isSelected ? FOREGROUND_SELECTED : FOREGROUND);
        left.setForeground(getForeground());
        right.setForeground(getForeground());
        left.setPreferredSize(new Dimension(0,0));
        return this;
    }

    protected abstract String[] getLeftAndRight(Object value);
}
