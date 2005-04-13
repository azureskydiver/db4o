/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;

import com.db4o.browser.query.model.FieldConstraint;
import com.db4o.browser.query.model.QueryBuilderModel;
import com.db4o.browser.query.model.QueryPrototypeInstance;
import com.db4o.browser.query.view.IConstraintRow;
import com.db4o.browser.query.view.PrototypeInstanceEditor;
import com.db4o.browser.query.view.QueryBrowserPane;
import com.db4o.reflect.ReflectClass;

public class QueryBuilderPaneController {

    private QueryBuilderModel queryModel;
    private QueryBrowserPane queryView;
    
    private int numEditors = 0;
    
    private static final int SPACING=8;

    public QueryBuilderPaneController(QueryBuilderModel queryModel, QueryBrowserPane queryView) {
        this.queryModel = queryModel;
        this.queryView = queryView;
        
        QueryPrototypeInstance root = queryModel.getRootInstance();
        buildEditor(root);
        final Canvas queryArea = queryView.getQueryArea();
        GridLayout layout = new GridLayout(numEditors, false);
        layout.horizontalSpacing = SPACING;
        layout.verticalSpacing = SPACING;
        layout.marginHeight = SPACING;
        layout.marginWidth = SPACING;
        queryArea.setLayout(layout);
        Point size = queryArea.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        queryArea.setBounds(0, 0, size.x, size.y);
        queryArea.layout(true);
    }

    private void buildEditor(QueryPrototypeInstance root) {
        if (root == null || root.getType() == null) {
            return;
        }
        
        ++numEditors;
        
        PrototypeInstanceEditor editor = new PrototypeInstanceEditor(queryView.getQueryArea(), SWT.NULL);
        editor.setTypeName(root.getType().getName());
        
        String[] fieldNames = root.getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            FieldConstraint field = root.getConstraint(fieldNames[i]);
            
            final ReflectClass fieldType = field.field.getType();
            
            if (fieldType.isSecondClass()) {
                editor.addPrimitiveTypeRow(field.field.getName());
                // TODO: Add data binding controllers here for relational operator/value...
                
            } else if (!fieldType.isCollection() && !fieldType.isArray()) {  // We don't handle collections yet
                IConstraintRow row = editor.addObjectReferenceRow(field.field.getName());
                row.setValue(fieldType.getName() + " >>>");
                buildEditor(field.valueProto());
            }
        }
    }

}
