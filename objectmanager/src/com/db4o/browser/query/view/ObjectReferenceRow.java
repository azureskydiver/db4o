/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ObjectReferenceRow implements IConstraintRow {
    
    private Label fieldName;
    private Label type;

    public ObjectReferenceRow(PrototypeInstanceEditor editor) {
        fieldName = new Label(editor, SWT.NULL);
        
        type = new Label(editor, SWT.NULL);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        type.setLayoutData(gd);
    }

    public void setFieldName(String fieldName) {
        this.fieldName.setText(fieldName);
    }

    public void setValue(String value) {
        type.setText(value);
    }

    public String getValue() {
        return type.getText();
    }

    public boolean isValueEditable() {
        return false;
    }

    public Text getValueEditor() {
        return null;
    }

    public Combo getRelationEditor() {
        return null;
    }

}
