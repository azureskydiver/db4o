/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;


/**
 * interface to the internal reference that an ObjectContainer
 * holds for a stored object.
 */
public interface ObjectInfo {
    
    /**
     * returns the object that is referenced.
     * <br><br>This method may return null, if the object has
     * been garbage collected.
     * @return the referenced object or null, if the object has
     * been garbage collected.
     */
    public Object getObject();
    
    /**
     * returns a UUID representation of the referenced object.
     * @return the UUID of the referenced object.
     */
    public Db4oUUID getUUID();
    
    
    

}
