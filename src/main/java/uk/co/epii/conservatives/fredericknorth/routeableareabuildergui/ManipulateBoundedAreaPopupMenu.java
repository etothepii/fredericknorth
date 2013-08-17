package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * User: James Robinson
 * Date: 30/07/2013
 * Time: 13:03
 */
public class ManipulateBoundedAreaPopupMenu extends JPopupMenu {

    private static final int SIBLING = 0;
    private static final int CHILD = 1;

    private final JMenuItem[] create;
    private final BoundedAreaType[] types;
    private final JMenuItem rename;
    private final JMenuItem delete;
    private final BoundedAreaType masterType;
    private BoundedArea boundedArea;

    public ManipulateBoundedAreaPopupMenu(BoundedAreaType masterType) {
        this.masterType = masterType;
        create = new JMenuItem[] {new JMenuItem(), new JMenuItem()};
        types = new BoundedAreaType[] {null, masterType};
        rename = new JMenuItem("Rename");
        delete = new JMenuItem("Delete");
        insert(create[0], 0);
        insert(create[1], 1);
        addSeparator();
        insert(rename, 3);
        insert(delete, 4);
        setBoundedArea(null);
    }

    public void setBoundedArea(BoundedArea boundedArea) {
        this.boundedArea = boundedArea;
        types[0] = boundedArea == null ? null : boundedArea.getBoundedAreaType();
        types[1] = types[0] == null ? masterType : types[0].getChildType();
        updateMenu(SIBLING);
        updateMenu(CHILD);
    }

    public BoundedArea getBoundedArea() {
        return boundedArea;
    }

    private void updateMenu(int relationship) {
        if (types[relationship] != null) {
            create[relationship].setText(String.format("New %s", types[relationship].getName()));
            create[relationship].setEnabled(true);
        }
        else {
            create[relationship].setText("");
            create[relationship].setEnabled(false);
        }
    }

    public void addDeleteActionListener(ActionListener actionListener) {
        delete.addActionListener(actionListener);
    }

    public void removeDeleteActionListener(ActionListener actionListener) {
        delete.removeActionListener(actionListener);
    }

    public void addRenameActionListener(ActionListener actionListener) {
        rename.addActionListener(actionListener);
    }

    public void removeRenameActionListener(ActionListener actionListener) {
        rename.removeActionListener(actionListener);
    }

    public void addCreateSiblingActionListener(ActionListener actionListener) {
        create[SIBLING].addActionListener(actionListener);
    }

    public void removeCreateSiblingActionListener(ActionListener actionListener) {
        create[SIBLING].removeActionListener(actionListener);
    }

    public void addCreateChildActionListener(ActionListener actionListener) {
        create[CHILD].addActionListener(actionListener);
    }

    public void removeCreateChildActionListener(ActionListener actionListener) {
        create[CHILD].removeActionListener(actionListener);
    }

}
