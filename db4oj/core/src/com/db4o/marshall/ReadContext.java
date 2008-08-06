/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.typehandlers.*;

/**
 * this interface is passed to internal class com.db4o.internal.TypeHandler4
 * when instantiating objects.
 */
public interface ReadContext extends Context, ReadBuffer {

    /**
     * Interprets the current position in the context as 
     * an ID and returns the object with this ID.
     * @return the object
     */
    public Object readObject();

    
    /**
     * reads sub-objects, in cases where the TypeHandler4
     * is known.
     */
    public Object readObject(TypeHandler4 handler);

}
