/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.view;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/*

This Composite implements the following layout:

<composite x:style="BORDER">
    <layout x:class="gridLayout" numColumns="3"/>
    <x:children>
        <label text="Car">
            <layoutData x:class="gridData" grabExcessHorizontalSpace="true" horizontalAlignment="GridData.FILL" horizontalSpan="3"/>
        </label>
        <label x:style="SEPARATOR|HORIZONTAL">
            <layoutData x:class="gridData" grabExcessHorizontalSpace="true" horizontalAlignment="GridData.FILL" horizontalSpan="3"/>
        </label>
        
        <!-- Primitive type field example -->
        <label text="model"/>
        <combo text="="/>
        <text x:style="BORDER"/>
        
        <!-- Object type reference -->
        <label text="pilot"/>
        <combo visible="false"/>
        <label text=">>>>>>>>>"/>
        
        <!-- Collection reference -->
        <label text="history"/>
        <combo visible="false"/>
        <button text="Select type..."/>

    </x:children>
</composite>

*/

public class PrototypeInstanceEditor extends Composite {
    
    private Label typeName;

    public PrototypeInstanceEditor(Composite parent, int style) {
        super(parent, style | SWT.BORDER);
        setLayout(new GridLayout(3, false));
        
        typeName = new Label(this, SWT.CENTER);
        typeName.setLayoutData(horizontalSpanData());
        
        new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(horizontalSpanData());
    }
    
    private GridData horizontalData() {
        return new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
    }
    
    private GridData horizontalSpanData() {
        GridData gd = horizontalData();
        gd.horizontalSpan = 3;
        return gd;
    }
    
    public void setTypeName(String typeName) {
        this.typeName.setText(typeName);
    }
    
    private HashMap rows = new HashMap();
    
    public IConstraintRow addPrimitiveTypeRow(String fieldName) {
        IConstraintRow row = new PrimitiveConstraintRow(this);
        row.setFieldName(fieldName);
        rows.put(fieldName, row);
        return row;
    }
    
    public IConstraintRow addObjectReferenceRow(String fieldName) {
        IConstraintRow row = new ObjectReferenceRow(this);
        row.setFieldName(fieldName);
        rows.put(fieldName, row);
        return row;
    }
    
    public IConstraintRow getConstraintRow(String fieldName) {
        return (IConstraintRow) rows.get(fieldName);
    }
}
