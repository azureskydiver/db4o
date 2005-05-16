/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors;

import org.eclipse.swt.widgets.Control;

import com.db4o.binding.CannotSaveException;
import com.db4o.binding.field.IFieldController;


/**
 * IObjectEditor.  Encapsulates editing operations on an object.
 *
 * @author djo
 */
public interface IObjectEditor {
    
    /**
     * Method setInput.
     * 
     * Sets the input object for this object editor.  The new input object
     * must have all the same properties as were being edited on the old
     * input object.
     * 
     * @param input The new input object.
     * @return true if the input could be successfully set.  false otherwise.
     */
    public boolean setInput(Object input);
    
    /**
     * Method getInput.
     * 
     * Returns the current input object for this object editor.
     * 
     * @return Object the current input object or null if there is none.
     */
    public Object getInput();
    
	/**
     * Method getProperty.
     * 
     * Returns an IProperty object specified by the given name.
     * 
	 * @param name The property name
	 * @return an IProperty bound to the specified object
     * @throws NoSuchMethodException if the property does not exist
	 */
	public IPropertyEditor getProperty(String name) throws NoSuchMethodException;
    
    /**
     * Method bind.
     * 
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
     * @param control The control to use as an editor
     * @param propertyName The name of the property to edit
     * @return An IFieldController configured to edit propertyName on this object
     */
    public IFieldController bind(Control control, String propertyName);

    /**
     * Method verifyAndSaveEditedFields.
     * 
     * Make sure that all fields that have been edited can be safely saved
     * and saves them.
     * 
     * @return true if all fields may be safely saved.  false otherwise.
     */
    public boolean verifyAndSaveEditedFields();
    
    /**
     * Method verifyAndSaveObject.
     * 
     * Returns if the underlying object is in a consistent state so that
     * changes can be committed to a persistent store.  If the underlying
     * persistent store implements transactional semantics, the actual
     * save operation will be performed here.  Otherwise, it will be performed
     * in the commit() method.
     * 
     * @return boolean true if all fields in the object have legal values
     * and the object itself is consistent and the object was successfully
     * saved.
     */
    public boolean verifyAndSaveObject();
    
    /**
     * Method commit.
     * 
     * Commit any changes that have occurred to the specified object.
     * 
     * @throws CannotSaveException if the data could not be saved.
     */
    public void commit() throws CannotSaveException;
    
    /**
     * Method refresh.
     * 
     * Refresh the specified object from its (multiuser) persistent store.
     */
    public void refresh();
    
    /**
     * Method rollback.
     * 
     * Roll back changes to this object (if possible/applicable).
     */
    public void rollback();
    
    /**
     * Method delete.
     * 
     * Remove this object from the persistent store (if possible/applicable)
     */
    public void delete();
}

