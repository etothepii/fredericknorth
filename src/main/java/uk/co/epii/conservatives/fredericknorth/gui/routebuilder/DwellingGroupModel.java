package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.*;

/**
 * User: James Robinson
 * Date: 06/07/2013
 * Time: 14:03
 */
class DwellingGroupModel extends AbstractTableModel {

    private static final Logger LOG = Logger.getLogger(DwellingGroupModel.class);

    private static final String DwellingGroupsColumnNameKey = "DwellingGroupsColumnName";
    private static final String DwellingGroupsDwellingsCountColumnNameKey = "DwellingGroupsDwellingsCountColumnName";



    private static final Class<?>[] COLUMN_CLASSES = new Class<?>[] {DwellingGroup.class, Integer.class};

    private final ArrayList<DwellingGroup> dwellingGroups;
    private final HashMap<DwellingGroup, Integer> dwellingGroupIndexMap;
    private final ListSelectionModel listSelectionModel;
    private final RowSorter<DwellingGroupModel> rowSorter;
    private final String[] columnNames;

    public DwellingGroupModel(ApplicationContext applicationContext) {
        this(new DefaultListSelectionModel(), applicationContext);
    }

    public DwellingGroupModel(ListSelectionModel listSelectionModel, ApplicationContext applicationContext) {
        this.dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroupIndexMap = new HashMap<DwellingGroup, Integer>();
        this.listSelectionModel = listSelectionModel;
        rowSorter = new TableRowSorter<DwellingGroupModel>(this);
        columnNames = new String[] {
                applicationContext.getProperty(DwellingGroupsColumnNameKey),
                applicationContext.getProperty(DwellingGroupsDwellingsCountColumnNameKey)
        };
        if (LOG.isDebugEnabled()) {
            listSelectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    LOG.debug(String.format("Selection %s", e.getValueIsAdjusting() ? "Changing" : "Changed"));
                    LOG.debug(String.format("DwellingGroups: %d", dwellingGroups.size()));
                    for (int i = 0; i < dwellingGroups.size(); i++) {
                        DwellingGroup dwellingGroup = dwellingGroups.get(i);
                        LOG.debug(String.format("%s: %s", dwellingGroup.getName(),
                                ((ListSelectionModel) e.getSource()).isSelectedIndex(rowSorter.convertRowIndexToView(i))));
                    }
                }
            });
        }
    }

    public void addAll(Collection<? extends DwellingGroup> dwellingGroups) {
        if (dwellingGroups.size() == 0) {
            return;
        }
        Set<DwellingGroup> uniqueDwellingGroups = new HashSet<DwellingGroup>(dwellingGroups);
        for (DwellingGroup dwellingGroup : uniqueDwellingGroups) {
            this.dwellingGroups.add(dwellingGroup);
        }
        updateIndexMap();
        fireTableDataChanged();
    }

    public Set<DwellingGroup> intersect(Collection<? extends DwellingGroup> dwellingGroups) {
        HashSet<DwellingGroup> intersection = new HashSet<DwellingGroup>(dwellingGroups.size());
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            if (dwellingGroupIndexMap.containsKey(dwellingGroup)) {
                intersection.add(dwellingGroup);
            }
        }
        return intersection;
    }

    public void removeAll(Collection<? extends DwellingGroup> dwellingGroups) {
        if (dwellingGroups.size() == 0) {
            return;
        }
        Set<DwellingGroup> uniqueDwellingGroups = new HashSet<DwellingGroup>(dwellingGroups);
        int[] dwellingGroupsIndicies = new int[uniqueDwellingGroups.size()];
        int counter = 0;
        for (DwellingGroup dwellingGroup : uniqueDwellingGroups) {
            dwellingGroupsIndicies[counter++] = dwellingGroupIndexMap.get(dwellingGroup);
        }
        Arrays.sort(dwellingGroupsIndicies);
        for (counter = dwellingGroupsIndicies.length - 1; counter >= 0; counter--) {
            this.dwellingGroups.remove(dwellingGroupsIndicies[counter]);
        }
        updateIndexMap();
        fireTableDataChanged();
    }

    public void clear() {
        dwellingGroups.clear();
        dwellingGroupIndexMap.clear();
        fireTableDataChanged();
    }

    public ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }

    public void setToContentsOf(Collection<? extends DwellingGroup> dwellingGroups) {
        listSelectionModel.clearSelection();
        this.dwellingGroups.clear();
        this.dwellingGroups.addAll(dwellingGroups);
        updateIndexMap();
        fireTableDataChanged();
    }

    public void setToContentsOf(List<? extends DwellingGroup> dwellingGroups, boolean[] selected) {
        int oldSize = this.dwellingGroups.size();
        if (oldSize == 0 && dwellingGroups.size() == 0) {
            return;
        }
        if (oldSize > 0) {
            this.dwellingGroups.clear();
            rowSorter.rowsDeleted(0, oldSize - 1);
        }
        if (dwellingGroups.size() > 0) {
            this.dwellingGroups.addAll(dwellingGroups);
            rowSorter.rowsInserted(0, dwellingGroups.size() - 1);
            updateIndexMap();
        }
        fireTableDataChanged();
        if (dwellingGroups.size() > 0) {
            listSelectionModel.setValueIsAdjusting(true);
            for (int i = 0; i < selected.length; i++) {
                int modelIndex = dwellingGroupIndexMap.get(dwellingGroups.get(i));
                int viewIndex = rowSorter.convertRowIndexToView(modelIndex);
                if (selected[i]) {
                    listSelectionModel.addSelectionInterval(viewIndex, viewIndex);
                }
            }
            listSelectionModel.setValueIsAdjusting(false);
        }
    }

    public int getTotalDwellingsCount() {
        int total = 0;
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            total += dwellingGroup.size();
        }
        return total;
    }

    private void updateIndexMap() {
        dwellingGroupIndexMap.clear();
        Collections.sort(dwellingGroups);
        int index = 0;
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            dwellingGroupIndexMap.put(dwellingGroup, index++);
        }
    }

    @Override
    public int getRowCount() {
        return dwellingGroups.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return dwellingGroups.get(rowIndex);
        }
        if (columnIndex == 1) {
            return dwellingGroups.get(rowIndex).size();
        }
        throw new IllegalArgumentException("You have asked for a non existant column");
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new IllegalArgumentException("Setting values is not currently supported");
    }

    public DwellingGroup getDwellingGroup(int index) {
        return dwellingGroups.get(index);
    }

    public RowSorter<? extends TableModel> getRowSorter() {
        return rowSorter;
    }

    public List<DwellingGroup> getSelected(SelectedState selectedState) {
        List<DwellingGroup> selected = new ArrayList<DwellingGroup>(dwellingGroups.size());
        for (int i = 0; i < dwellingGroups.size(); i++) {
            if (selectedState == SelectedState.ALL ||
                    (selectedState == SelectedState.UNSELECTED ^
                            listSelectionModel.isSelectedIndex(rowSorter.convertRowIndexToView(i)))) {
                selected.add(dwellingGroups.get(i));
            }
        }
        return selected;
    }

    public boolean contains(DwellingGroup dwellingGroup) {
        return dwellingGroupIndexMap.containsKey(dwellingGroup);
    }
}