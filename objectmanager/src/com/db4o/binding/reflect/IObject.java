/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.reflect;

/**
 * IObject.  Encapsulates editing operations on an object.
 *
 * @author djo
 */
public interface IObject {
    
    /**
     * Method getPropertyNames.
     * 
     * Returns the names of all known properies on the specified object.
     * Implementations may choose any conventions they like for determining
     * what a property is (all fields, JavaBean conventions, etc).
     * 
     * @return String[] all known property names on this object.
     */
    public String[] getPropertyNames();
    
	/**
     * Method getProperty.
     * 
     * Returns an IProperty object specified by the given name.
     * 
	 * @param name The property name
	 * @return an IProperty bound to the specified object
     * @throws NoSuchMethodException if the property does not exist
	 */
	public IProperty getProperty(String name) throws NoSuchMethodException;
    
    /**
     * Method verifyObject.
     * 
     * Returns if the underlying object is in a consistent state so that
     * changes can be committed to a persistent store.  This method returns
     * the result of calling the verifyObject() method on the object if it 
     * exists on the underlying object, and returns true if the method does
     * not exist.
     * 
     * @return boolean true if all fields in the object have legal values
     * and the object itself is consistent.
     */
    public boolean verifyObject();
    
    /**
     * Method commit.
     * 
     * Commit any changes that have occurred to the specified object.
     */
    public void commit();
    
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

