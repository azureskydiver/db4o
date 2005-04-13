/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.db4o.browser.query.model.RelationalOperator;

public class PrimitiveConstraintRow implements IConstraintRow {

    private Label fieldName;
    private Combo relationalOperatorChoices;
    private RelationalOperator relationalOperatorChoice;
    private Text fieldValue;
    
    public PrimitiveConstraintRow(PrototypeInstanceEditor editor) {
        fieldName = new Label(editor, SWT.NULL);
        
        relationalOperatorChoices = new Combo(editor, SWT.READ_ONLY);
        for (int i = 0; i < RelationalOperator.OPERATORS.length; i++) {
            relationalOperatorChoices.add(RelationalOperator.OPERATORS[i].name());
        }
        relationalOperatorChoices.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                relationalOperatorChoice = RelationalOperator.OPERATORS[relationalOperatorChoices.getSelectionIndex()];
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        relationalOperatorChoices.select(0);

        fieldValue = new Text(editor, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
        gd.minimumWidth = 75;
        fieldValue.setLayoutData(gd);
    }

    public void setFieldName(String fieldName) {
        this.fieldName.setText(fieldName);
    }

    public RelationalOperator getRelationalOperator() {
        return relationalOperatorChoice;
    }

    public void setValue(String value) {
        fieldValue.setText(value);
    }

    public String getValue() {
        return fieldValue.getText();
    }

    public boolean isValueEditable() {
        return true;
    }

    public Text getValueEditor() {
        return fieldValue;
    }

}
