package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * User: James Robinson
 * Date: 14/09/2014
 * Time: 18:33
 */
public class DwellingGroupNameCellRendeder implements TableCellRenderer {

  private JLabel label = new JLabel();
  private TableCellRenderer defaultRenderer;

  public DwellingGroupNameCellRendeder(TableCellRenderer defaultRenderer) {
    this.defaultRenderer = defaultRenderer;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    Component defualt = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (!(value instanceof DwellingGroup)) {
      throw new UnsupportedOperationException("value must be a DwellingGroup");
    }
    DwellingGroup dwellingGroup = (DwellingGroup)value;
    label.setText(dwellingGroup.getName());
    label.setToolTipText(dwellingGroup.getName());
    label.setBackground(defualt.getBackground());
    label.setForeground(defualt.getForeground());
    return label;
  }
}
