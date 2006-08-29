package com.db4o.objectManager.v2;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.db4o.objectmanager.api.DatabaseInspector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;

/**
 * User: treeder
 * Date: Aug 28, 2006
 * Time: 2:51:41 PM
 */
public class DatabaseStatsPanel extends JPanel {
    private DatabaseInspector databaseInspector;

    String columLayout = "right:max(40dlu;pref), 3dlu, 120dlu, 7dlu";

    public DatabaseStatsPanel(DatabaseInspector databaseInspector) {
        super(new BorderLayout());
        this.databaseInspector = databaseInspector;
        setOpaque(false);
        setBorder(Borders.DIALOG_BORDER);
        add(buildMain());
    }

    private Component buildMain() {
        Box box = new Box(BoxLayout.PAGE_AXIS);

        box.add(buildMainDatabaseStats());
        box.add(buildReplicationStats());

        return box;
    }

    private Component buildMainDatabaseStats() {
        FormLayout layout = new FormLayout(
                    columLayout, // 1st major colum
                    "");                                         // add rows dynamically

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        builder.append("Size:", new JLabel(databaseInspector.getSize() + " bytes"));
        builder.append("Used Space: ", new JLabel(databaseInspector.getSpaceUsed() + " bytes"));
        builder.append("Free Space: ", new JLabel(databaseInspector.getSpaceFree() + " bytes"));
        builder.append("Lost Space: ", new JLabel(databaseInspector.getSpaceUnallocated() + " bytes"));

        JPanel p = builder.getPanel();
        Border b = new LineBorder(Color.GRAY, 1, true);
        TitledBorder b2 = new TitledBorder(b, "Database Statistics");
        p.setBorder(b2);
        return p;
    }

    private Component buildReplicationStats() {
        FormLayout layout = new FormLayout(
                           columLayout, // 1st major colum
                           "");                                         // add rows dynamically

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        builder.append("To/From Database: ", new JLabel("hostname or filename if possible"));
        builder.append("Last Replication: ", new JLabel("Date"));
        builder.append("To/From Database: ", new JLabel("hostname or filename if possible"));
        builder.append("Last Replication: ", new JLabel("Date"));
                
        JPanel p = builder.getPanel();
        p.setBorder(Borders.DIALOG_BORDER);

        JPanel outer = new JPanel();
        Border b = new LineBorder(Color.GRAY, 1, true);
        TitledBorder b2 = new TitledBorder(b, "Replication Info");
        outer.setBorder(b2);
        outer.add(p);

        return outer;
    }

}
