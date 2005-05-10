/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors;

import com.db4o.binding.verifier.IVerifier;

/**
 * IPropertyEditor.  An abstraction for a property.  Implementations may follow
 * JavaBeans naming specifications, may simply treat POJO fields as properties,
 * may represent a property of a remote object, etc...
 *
 * @author djo
 */
public interface IPropertyEditor {
    
    /**
     * Method getName.  Return's the property's name.
     * 
     * @return The property's name.
     */
    public String getName();
    
	/**
     * Method get().  Return the value of the property.
     * 
	 * @return the value of the property as an Object
	 */
	public Object get();
    
    /**
     * Method set().  Set the value of the property.
     * 
     * @param newValue The new value to set.
     */
    public void set(Object newValue);
    
    /**
     * Method isReadOnly().  Returns if the property is read-only.
     * 
     * @return true if the property can only be read but not written.
     */
    public boolean isReadOnly();
    
    /**
     * Method getType().  Returns the underlying property's type.
     * 
     * @return Class The property's type.
     */
    public Class getType();
    
    /**
     * Method getVerifier().  Returns the property's verifier or the
     * default verifier for the property's type if the property has no
     * verifier.
     * 
     * @return IVerifier The property's verifier.
     */
    public IVerifier getVerifier();
    
    /**
     * Method getUIValues().  Returns the set of user interface choices 
     * corresponding to the property's valid values.  This is either 
     * defined as a part of the property or is derived by applying
     * toString() to each element in the valid values array.  If
     * no set of valid values has been defined, this method returns
     * null.
     * 
     * @return String[] the array of user interface choices corresponding
     * to the array of valid values.
     */
    public String[] getUIValues();
    
    /**
     * Method getLegalValues.  Returns an array containing all valid values
     * that this property can accept if the programmer has defined a
     * get<Prop>LegalValues() method for this property.  Otherwise, returns 
     * null.
     * 
     * @return Object[] an array containing all valid values that this
     * property can accept if the programmer has defined a validValues
     * method or null otherwise.
     */
    public Object[] getLegalValues();
}

