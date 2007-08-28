/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.internal.*;


/**
 * this interface is passed to {@link TypeHandler4} during marshalling
 * and provides methods to marshall objects. 
 */
public interface WriteContext extends Context, WriteBuffer {

    /**
     * writes any type of object, first class objects and primitive
     * types.
     * The type information is stored in the slot, to allow it to 
     * be reconstructed, for instance for objects in untyped fields.
     * For first class objects where the type is known, use 
     * {@link #writeObject(Object)} instead, since it is more efficient.   
     * @param obj the object to write.
     */
    void writeAny(Object obj);

    /**
     * makes sure the object is stored and writes the ID of
     * the object to the context.
     * Use this method for first class objects only (objects that
     * have an identity in the database). If the object can potentially
     * be a primitive type, do not use this method bue use 
     * {@link #writeAny(Object)} instead.
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
