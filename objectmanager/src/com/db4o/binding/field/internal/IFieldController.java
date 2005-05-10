/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.field.internal;

import com.db4o.binding.CannotSaveException;
import com.db4o.binding.dataeditors.IPropertyEditor;

/**
 * Interface IFieldController. An IFieldController object wraps a UI widget and
 * allows the rest of the framework to generically set and get values as
 * objects. The IWidgetBinding implementation worries about converting the
 * object value to a data type that can be displayed or edited by the user
 * interface, allowing the user to edit the value, validating the user's edited
 * version, and pushing changes back out to the persistence layer.
 * 
 * @author djo
 */
public interface IFieldController {
	/**
     * Method setInput()
     * 
     * Sets the input object on the widget.  This normally will trigger a
     * user interface refresh.
     * 
	 * @param input The new input object
	 */
	public void setInput(IPropertyEditor input) throws CannotSaveException;
    
    /**
     * Method getInput.
     * 
     * Return the current input value or null if the input is not currently
     * set to a valid value.
     * 
     * @return IPropertyEditor The current user input.
     */
    public IPropertyEditor getInput();
    
    /**
     * Method getPropertyName.
     * 
     * Return the name of the property being edited.
     * 
     * @return String the name of the property being edited.
     */
    public String getPropertyName();
    
    /**
     * Method isDirty.
     * 
     * Returns if the input value has been edited but not saved.
     *  
     * @return true if the user has changed the input value; false otherwise.
     */
    public boolean isDirty();
    
    /**
     * Method setDirty.
     * 
     * Sets the dirty flag.
     * 
     * @param dirty true if the input field should be considered dirty, false otherwise.
     */
    public void setDirty(boolean dirty);
    
    /**
     * Method undo.
     * 
     * Reverses changes the user may have made since the last save and resets
     * the dirty flag to false.
     */
    public void undo();
    
    /**
	 * Method save.
	 * 
	 * If the field is dirty, verifies and saves the new value.
	 * <p>
	 * Normally, this is performed automatically whenever the focus leaves the
	 * field. However, this method can force the controller to save the field's
	 * contents on demand.
	 */
    public void save() throws CannotSaveException;
    
    /**
     * Method verify.
     * 
     * Verify the current field value to see if it can be safely saved.
     * 
     * @return true if the current UI field value can be safely saved; false
     * otherwise.
     */
    public boolean verify();
}


