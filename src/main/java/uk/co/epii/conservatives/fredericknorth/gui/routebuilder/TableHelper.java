package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import javax.swing.table.TableColumn;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 11:56
 */
class TableHelper {

    public static void forceColumnWidth(TableColumn column, int columnWidth) {
        column.setMinWidth(columnWidth);
        column.setMaxWidth(columnWidth);
        column.setWidth(columnWidth);
    }

}
