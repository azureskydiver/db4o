/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.controllers;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Text;

import com.db4o.binding.CannotSaveException;
import com.db4o.binding.browser.FieldConstraintRelationalOperatorFieldController;
import com.db4o.binding.browser.FieldConstraintValueFieldController;
import com.db4o.binding.field.IFieldController;
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
        layout(queryView);
    }

    private void layout(QueryBrowserPane queryView) {
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
    
    private LinkedList controllers = new LinkedList();

    private void buildEditor(QueryPrototypeInstance root) {
        if (root == null || root.getType() == null) {
            return;
        }
        
        ++numEditors;
        
        PrototypeInstanceEditor editor = new PrototypeInstanceEditor(queryView.getQueryArea(), SWT.NULL);
        editor.setTypeName(root.getType().getName()); // FIXME: Make this reflect the parent field name
        
        String[] fieldNames = root.getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            FieldConstraint field = root.getConstraint(fieldNames[i]);
            
            final ReflectClass fieldType = field.field.getType();
            
            if (fieldType.isSecondClass()) {
                IConstraintRow row = editor.addPrimitiveTypeRow(field.field.getName());
                // Relational operator...
                IFieldController controller;
                controller = new FieldConstraintRelationalOperatorFieldController(row.getRelationEditor(), field);
                controllers.add(controller);
                
                // Value...
                controller = new FieldConstraintValueFieldController((Text)row.getValueEditor(), field, queryModel.getDatabase());
                controllers.add(controller);
            } else {
                IConstraintRow row = editor.addObjectReferenceRow(field.field.getName());
                row.setValue(fieldType.getName() + " >>>");
                Button expandEditor = (Button) row.getValueEditor();
                expandEditor.addSelectionListener(new ExpandEditor(field, editor, row));
//                buildEditor(field.valueProto());
            }
        }
    }
    
    private class ExpandEditor implements SelectionListener {

        private FieldConstraint field;
        private PrototypeInstanceEditor editor;
        private IConstraintRow row;

        public ExpandEditor(FieldConstraint field, PrototypeInstanceEditor editor, IConstraintRow row) {
            this.field = field;
            this.editor = editor;
            this.row = row;
        }

        public void widgetSelected(SelectionEvent e) {
            field.expand();
            buildEditor(field.valueProto());
            row.getValueEditor().setEnabled(false);
            layout(queryView);
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
        
    }
    
    public void save() throws CannotSaveException {
        for (Iterator controllerIter = controllers.iterator(); controllerIter.hasNext();) {
            IFieldController controller = (IFieldController) controllerIter.next();
            controller.save();
        }
    }

}
