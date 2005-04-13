/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.view;

import org.eclipse.swt.widgets.Text;

import com.db4o.browser.query.model.RelationalOperator;

public interface IConstraintRow {
    public void setFieldName(String fieldName);
    
    public RelationalOperator getRelationalOperator();
    
    public void setValue(String value);
    
    public String getValue();
    
    public boolean isValueEditable();
    
    /**
     * Returns the Text object used to edit the value if isValueEditable()
     * is true.
     * 
     * @return The ValueEditor text object if isValueEditable() else returns null
     */
    public Text getValueEditor();
}
