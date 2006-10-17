package com.db4o.objectManager.v2.tree;

import javax.swing.*;
import java.awt.Component;

/**
 * User: treeder
 * Date: Sep 29, 2006
 * Time: 3:11:48 PM
 */
public class ObjectTreeCellEditor extends DefaultCellEditor {

    public ObjectTreeCellEditor(final JTextField textField) {
        // from superclass
        super(textField);
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
               // System.out.println("value: " + value + " " + value.getClass());
                if (value instanceof ObjectTreeNode) {
                    ObjectTreeNode node = (ObjectTreeNode) value;
                    textField.setText((node.getObject() != null) ? node.getObject().toString() : "");
                } else {
                    textField.setText((value != null) ? value.toString() : "");
                }
            }

            public Object getCellEditorValue() {
                return textField.getText();
            }
        };
        textField.addActionListener(delegate);
    }

    public ObjectTreeCellEditor(JCheckBox checkBox) {
        super(checkBox);
    }

    public ObjectTreeCellEditor(JComboBox comboBox) {
        super(comboBox);
    }


    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        //System.out.println("value: " + value + " " + value.getClass());
        delegate.setValue(value);
        return editorComponent;
	}
}
