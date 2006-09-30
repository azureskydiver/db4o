package com.db4o.objectManager.v2.tree;

import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
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
                System.out.println("value: " + value + " " + value.getClass());
                // this is a bit hokey, would be nice to get the ObjectTreeNode passed in here, not the value of the renderer's toString(), maybe make a custom Renderer too
                if (value instanceof ObjectTreeNode) {
                    // this never occurs currently because a String is passed in as the value, not the ObjectTreeNode
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
        System.out.println("value: " + value + " " + value.getClass());
        delegate.setValue(value);
        return editorComponent;
        //return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
    }
}
