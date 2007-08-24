/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;


/**
 * this interface is passed to {@link TypeHandler4}
 * during marshalling and provides methods to marshall
 * objects. 
 */
public interface WriteContext extends Context, WriteBuffer {

    /**
     * makes sure the object is stored and writes the ID of
     * the object to the context.
     * @param obj the object.
     */
    void writeObject(Object obj);

}
