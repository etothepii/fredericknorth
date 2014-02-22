package uk.co.epii.conservatives.fredericknorth.gui.meetingpointselector;

import javax.swing.table.AbstractTableModel;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 00:11
 */
public class MeetingPointsModel extends AbstractTableModel {

    private List<MeetingPoint> locations;

    public MeetingPointsModel(List<MeetingPoint> locations) {
        this.locations = locations;
    }

    @Override
    public int getRowCount() {
        return locations.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex != 0) {
            throw new IllegalArgumentException("No such column: " + columnIndex);
        }
        return locations.get(rowIndex).getName();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex != 0) {
            throw new IllegalArgumentException("No such column: " + columnIndex);
        }
        if (aValue instanceof String) {
            locations.get(rowIndex).setName((String)aValue);
        }
        else {
            throw new IllegalArgumentException("Can only set the name with a String: " + columnIndex);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void clear() {
        int size = locations.size();
        locations.clear();
        fireTableRowsDeleted(0, size);
    }

    public void addAll(Collection<MeetingPoint> meetingPoints) {
        int size = locations.size();
        locations.addAll(meetingPoints);
        fireTableRowsInserted(size, size + meetingPoints.size());
    }



}
