/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.field;

import org.eclipse.swt.widgets.Control;

import com.db4o.binding.field.internal.TextFieldController;
import com.db4o.binding.reflect.PropertyFactory;

/**
 * FieldControlerFactory.  Given an SWT control, constructs an appropriate
 * IFieldController encapsulating that control and managing its events.
 *
 * @author djo
 */
public class FieldControllerFactory {
    /**
     * Analyze the specified control and (object, property) and return an
     * appropriate IFieldController for editing that (object, property)
     * pair using the specified control.
     * <p>
     * If we eventually implement masked editing, a masked edit implementation
     * will simply be another IFieldController implementation than the
     * standard IVerifier-based one, and will be automatically selected
     * and returned from this factory based on the presence of a masked-edit
     * based IVerifier on the property.
     * 
     * @param control
     * @param object
     * @param propertyName
     * @return
     */
    public static IFieldController getWidgetBinding(Control control, Object object, String propertyName) {
        return new TextFieldController(control, PropertyFactory.construct(object, propertyName));
    }
}
