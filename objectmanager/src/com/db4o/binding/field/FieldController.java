/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.field;

import org.eclipse.swt.widgets.Control;

import com.db4o.binding.dataeditors.IObjectEditor;
import com.db4o.binding.dataeditors.IPropertyEditor;
import com.db4o.binding.field.internal.CheckboxButtonFieldController;
import com.db4o.binding.field.internal.CheckboxListFieldController;
import com.db4o.binding.field.internal.ComboFieldController;
import com.db4o.binding.field.internal.TextFieldController;
import com.db4o.binding.swt.ICheckboxButtonField;
import com.db4o.binding.swt.ICheckboxListField;
import com.db4o.binding.swt.IComboField;
import com.db4o.reflect.ext.DuckType;

/**
 * FieldController.  Generic constructor factory for IFieldController objects. 
 *
 * @author djo
 */
public class FieldController {
    /**
     * Construct an IFieldController for some control, IObjectEditor, and IPropertyEditor
     * 
     * @param control The SWT control
     * @param objectEditor The IObjectEditor of the object to edit
     * @param propertyEditor The IPropertyEditor of the property to edit
     * 
     * @return The IFieldController that was constructed
     */
    public static IFieldController construct(Control control, IObjectEditor objectEditor, IPropertyEditor propertyEditor) {
        IFieldController result = null;
        if (DuckType.instanceOf(ICheckboxButtonField.class, control)) {
            result = new CheckboxButtonFieldController(control, objectEditor, propertyEditor);
        } else if (DuckType.instanceOf(ICheckboxListField.class, control)) {
            result = new CheckboxListFieldController(control, objectEditor, propertyEditor);
        } else if (DuckType.instanceOf(IComboField.class, control)) {
            result = new ComboFieldController(control, objectEditor, propertyEditor);
        } else {
            result = new TextFieldController(control, objectEditor, propertyEditor);
        }
        return result;
    } 
}
