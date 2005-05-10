/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.dataeditors.db4o;

/**
 * IDb4oBean. A relaxed duck interface for Db4o objects that are being edited.
 * These methods will be called at the appropriate times in the object's 
 * life cycle if they exist.
 *
 * @author djo
 */
public interface IDb4oBean {

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
