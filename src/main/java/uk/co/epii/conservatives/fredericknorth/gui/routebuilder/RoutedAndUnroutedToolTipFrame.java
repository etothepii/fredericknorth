package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelListener;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 22:00
 */
class RoutedAndUnroutedToolTipFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(RoutedAndUnroutedToolTipFrame.class);

    private final JScrollPane dwellingGroupsScroller;
    private final JTable dwellingGroupsTable;
    private final JLabel totalDwellings;
    private final String totalDwellingsFormat = " Total Dwellings: %d";

    public RoutedAndUnroutedToolTipFrame(RoutedAndUnroutedToolTipModel routedAndUnroutedToolTipModel, int dwellingCountColumnWidth) {
        dwellingGroupsTable = new JTable(routedAndUnroutedToolTipModel.getDwellingGroupModel());
        totalDwellings = new JLabel();
        getContentPane().setBackground(Color.WHITE);
        dwellingGroupsScroller = new JScrollPane(dwellingGroupsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(dwellingGroupsScroller);
        getContentPane().add(totalDwellings, BorderLayout.NORTH);
        dwellingGroupsTable.setSelectionModel(
                routedAndUnroutedToolTipModel.getDwellingGroupModel().getListSelectionModel());
        dwellingGroupsTable.setRowSorter(routedAndUnroutedToolTipModel.getDwellingGroupModel().getRowSorter());
        dwellingGroupsTable.getColumnModel().getColumn(0).setCellRenderer(new DwellingGroupNameCellRendeder());
        dwellingGroupsTable.setDoubleBuffered(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        TableHelper.forceColumnWidth(dwellingGroupsTable.getColumnModel().getColumn(1), dwellingCountColumnWidth);
        routedAndUnroutedToolTipModel.addRoutedAndUnroutedToolTipDwellingGroupsUpdatedListener(
                new RoutedAndUnroutedToolTipDwellingGroupsUpdatedListener() {
            @Override
            public void routedAndUnroutedToolTipDataChanged(RoutedAndUnroutedToolTipDwellingGroupsUpdatedEvent e) {
                LOG.debug("Setting visibility");
                if (e.getSource().getDwellingGroupModel().getRowCount() > 0) {
                    setSize(new Dimension(300, Math.min(200, dwellingGroupsTable.getPreferredSize().height + 36)));
                    totalDwellings.setText(String.format(totalDwellingsFormat,
                            e.getSource().getDwellingGroupModel().getTotalDwellingsCount()));
                    setVisible(true);
                }
                else {
                    setVisible(false);
                }
            }
        });


    }

    public MouseWheelListener[] getScrollerListeners() {
        return dwellingGroupsScroller.getMouseWheelListeners();
    }
}
