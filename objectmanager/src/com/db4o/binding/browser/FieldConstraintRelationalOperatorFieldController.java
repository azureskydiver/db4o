/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.browser;

import org.eclipse.swt.widgets.Combo;

import com.db4o.binding.CannotSaveException;
import com.db4o.binding.dataeditors.IPropertyEditor;
import com.db4o.binding.field.IFieldController;
import com.db4o.browser.query.model.FieldConstraint;
import com.db4o.browser.query.model.RelationalOperator;

/**
 * FieldConstraintRelationalOperatorFieldController.
 *
 * (cf. Donaudampfschiffskapitaensmuetzenbesitzer...)
 *
 * @author djo
 */
public class FieldConstraintRelationalOperatorFieldController implements IFieldController {

    private Combo ui;
    private FieldConstraint constraint;
    private boolean dirty=false;

    public FieldConstraintRelationalOperatorFieldController(Combo ui, FieldConstraint constraint) {
        this.ui = ui;
        this.constraint = constraint;
        
        for (int i = 0; i < RelationalOperator.OPERATORS.length; i++) {
            ui.add(RelationalOperator.OPERATORS[i].name());
        }
        ui.select(0);
    }
    

    public String getPropertyName() {
        return "RelationalOperator";
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void undo() {
        // Not needed for this implementation
    }

    public void save() throws CannotSaveException {
        constraint.relation = RelationalOperator.OPERATORS[ui.getSelectionIndex()];
    }

    public boolean verify() {
        return true;
    }


    public void setInput(IPropertyEditor input) throws CannotSaveException {
        // TODO Auto-generated method stub
        
    }


    public IPropertyEditor getInput() {
        // TODO Auto-generated method stub
        return null;
    }

}
