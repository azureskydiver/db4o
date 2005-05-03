/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.controllers;

import java.util.HashMap;
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
import com.db4o.reflect.ReflectField;

public class QueryBuilderPaneController {

    private QueryBuilderModel queryModel;
    private QueryBrowserPane queryView;
    
    private int numEditors = 0;
    
    private static final int SPACING=8;

    public QueryBuilderPaneController(QueryBuilderModel queryModel, QueryBrowserPane queryView) {
        this.queryModel = queryModel;
        this.queryView = queryView;
        
        QueryPrototypeInstance root = queryModel.getRootInstance();
        buildEditor(root, null);
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
    
    private void buildEditor(QueryPrototypeInstance root, String fieldName) {
        class EditorRow {
            public ReflectField field;
            public IConstraintRow rowEditor;

            public EditorRow(ReflectField field, IConstraintRow rowEditor) {
                this.field = field;
                this.rowEditor = rowEditor;
            }
        }

        if (root == null || root.getType() == null) {
            return;
        }
        
        ++numEditors;
        
        PrototypeInstanceEditor editor = new PrototypeInstanceEditor(queryView.getQueryArea(), SWT.NULL);
        
        // compute a nice title for the editor
        String className = root.getType().getName();
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex > 0) {
            className = className.substring(lastDotIndex+1);
        }
        editor.setTypeName(fieldName == null ? className : fieldName + " : " + className, root.getType().isInterface());

        // Now expand the fields
        HashMap priorRows = new HashMap();
        ReflectField[] fields = root.getFields();
        
        for (int i = 0; i < fields.length; i++) {
            FieldConstraint field = root.getConstraint(fields[i]);
            
            String curFieldName = field.field.getName();
            EditorRow priorRow = (EditorRow) priorRows.get(curFieldName);
            if (priorRow != null) {
                curFieldName = "(" + field.field.getType() + ") " + curFieldName;
                String oldFieldName = "(" + priorRow.field.getType().getName() + ") " + priorRow.field.getName();
                priorRow.rowEditor.setFieldName(oldFieldName);
            }
            
            final ReflectClass fieldType = field.field.getType();
            IConstraintRow newRow = null;
            
            if (fieldType.isSecondClass()) {
                newRow = editor.addPrimitiveTypeRow(curFieldName, field.field.isPublic());
                // Relational operator...
                IFieldController controller;
                controller = new FieldConstraintRelationalOperatorFieldController(newRow.getRelationEditor(), field);
                controllers.add(controller);
                
                // Value...
                controller = new FieldConstraintValueFieldController((Text)newRow.getValueEditor(), field, queryModel.getDatabase());
                controllers.add(controller);
            } else {
                newRow = editor.addObjectReferenceRow(curFieldName, field.field.isPublic());
                newRow.setValue(fieldType.getName() + " >>>");
                Button expandEditor = (Button) newRow.getValueEditor();
                expandEditor.addSelectionListener(new ExpandEditor(field, editor, newRow));
//                buildEditor(field.valueProto());
            }
            
            if (priorRow == null) {
                priorRows.put(curFieldName, new EditorRow(fields[i], newRow));
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
            buildEditor(field.valueProto(), field.field.getName());
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
