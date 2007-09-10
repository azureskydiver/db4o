/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class ArrayHandler0 extends ArrayHandler {

    public ArrayHandler0(TypeHandler4 template) {
        super(template);
    }

    public Object read(ReadContext readContext) {
        
        UnmarshallingContext context = (UnmarshallingContext) readContext;
        
        Buffer buffer = readIndirectedBuffer(context); 
        if (buffer == null) {
            return null;
        }
        if (Deploy.debug) {
            Debug.readBegin(buffer, identifier());
        }
        
        // With the following line we ask the context to work with 
        // a different buffer. Should this logic ever be needed by
        // a user handler, it should be implemented by using a Queue
        // in the UnmarshallingContext.
        
        // The buffer has to be set back from the outside!  See the
        // secondlast line of this method. 
        
        Buffer contextBuffer = context.buffer(buffer);

        IntByRef elements = new IntByRef();
        Object array = readCreate(context.transaction(), buffer, elements);
        if (array != null){
            if(handleAsByteArray(array)){
                buffer.readBytes((byte[])array);
            } else{
                for (int i = 0; i < elements.value; i++) {
                    arrayReflector().set(array, i, context.readObject(_handler));
                }
            }
        }
        
        if (Deploy.debug) {
            Debug.readEnd(buffer);
        }
        
        
        // The context buffer has to be set back.
        
        context.buffer(contextBuffer);
        
        return array;
    }
}
