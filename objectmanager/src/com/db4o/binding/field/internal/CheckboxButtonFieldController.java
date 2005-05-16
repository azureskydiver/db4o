/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.field.internal;

import com.db4o.binding.CannotSaveException;
import com.db4o.binding.dataeditors.IObjectEditor;
import com.db4o.binding.dataeditors.IPropertyEditor;
import com.db4o.binding.field.IFieldController;

public class CheckboxButtonFieldController implements IFieldController {

    public CheckboxButtonFieldController(Object control, IObjectEditor object, IPropertyEditor property) {
        // TODO Auto-generated constructor stub
    }

    public String getPropertyName() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setDirty(boolean dirty) {
        // TODO Auto-generated method stub

    }

    public void undo() {
        // TODO Auto-generated method stub

    }

    public void save() throws CannotSaveException {
        // TODO Auto-generated method stub

    }

    public boolean verify() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setInput(Object input) {
        // TODO Auto-generated method stub
        
    }

    public Object getInput() {
        // TODO Auto-generated method stub
        return null;
    }

}
