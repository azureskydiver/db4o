package com.db4o.objectManager.v2;

import com.db4o.ObjectContainer;
import com.db4o.objectManager.v2.custom.BorderedPanel;
import com.db4o.objectManager.v2.custom.BorderedFormPanel;
import com.db4o.objectmanager.api.DatabaseInspector;
import com.jgoodies.forms.factories.Borders;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
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

		box.add(buildClassStats());
		box.add(buildFieldInfo());

        return box;
    }

    private Component buildClassStats() {
        BorderedFormPanel builder = new BorderedFormPanel("Class Statistics");

        builder.append("Number of Objects:", new JLabel(databaseInspector.getNumberOfObjectsForClass(className) + ""));

        return builder.getPanel();
    }

	private Component buildFieldInfo() {
        BorderedPanel builder = new BorderedPanel("Fields");

        TableModel classModel = createFieldModel();
        JTable table = new JTable(classModel);
        TableColumn col = table.getColumnModel().getColumn(0);
        int width = 200;
        col.setPreferredWidth(width);
        JScrollPane scrollPane = new JScrollPane(table);
        builder.add(scrollPane);

        return builder.getPanel();
    }

    private TableModel createFieldModel() {
        TableModel tableModel = new FieldInfoTableModel(objectContainer, databaseInspector, className);
        return tableModel;
    }

    public String getClassName() {
        return className;
    }
}
