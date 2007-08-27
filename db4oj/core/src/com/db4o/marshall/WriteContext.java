/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.internal.*;


/**
 * this interface is passed to {@link TypeHandler4}
 * during marshalling and provides methods to marshall
 * objects. 
 */
public interface WriteContext extends Context, WriteBuffer {

    /**
     * makes sure the object is stored and writes the ID of
     * the object to the context.
     * @param obj the object to write.
     */
    void writeObject(Object obj);

    /**
     * writes sub-objects, in cases where the {@link TypeHandler4}
     * is known.
     * @param obj the object to write
     */
    void writeObject(TypeHandler4 handler, Object obj);

}
