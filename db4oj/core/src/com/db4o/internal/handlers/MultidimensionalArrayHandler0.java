/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class MultidimensionalArrayHandler0 extends MultidimensionalArrayHandler {

    public MultidimensionalArrayHandler0(TypeHandler4 template) {
        super(template);
    }
    
    public Object read(ReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        
        BufferImpl buffer = readIndirectedBuffer(context); 
        if (buffer == null) {
            return null;
        }
        
        // With the following line we ask the context to work with 
        // a different buffer. Should this logic ever be needed by
        // a user handler, it should be implemented by using a Queue
        // in the UnmarshallingContext.
        
        // The buffer has to be set back from the outside!  See below
        Buffer contextBuffer = context.buffer(buffer);
        
        Object array = super.read(context);
        
        // The context buffer has to be set back.
        context.buffer(contextBuffer);
        
        return array;
    }

}
