package com.db4o.objectManager.v2;

import com.db4o.ObjectContainer;
import com.db4o.objectmanager.api.DatabaseInspector;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * User: treeder
 * Date: Sep 3, 2006
 * Time: 4:37:35 PM
 */
public class ClassSummaryPanel extends JPanel {
    private ObjectContainer objectContainer;
    private DatabaseInspector databaseInspector;
    private String className;

    public ClassSummaryPanel(ObjectContainer objectContainer, DatabaseInspector databaseInspector, String className) {
        super(new BorderLayout());
        this.className = className;
        setOpaque(false);
        setBorder(Borders.DIALOG_BORDER);
        this.objectContainer = objectContainer;
        this.databaseInspector = databaseInspector;
        add(buildMain());
    }

    private Component buildMain() {
        Box box = new Box(BoxLayout.PAGE_AXIS);

        //box.add(buildConnectionInfo());

        return box;
    }

    public String getClassName() {
        return className;
    }
}
