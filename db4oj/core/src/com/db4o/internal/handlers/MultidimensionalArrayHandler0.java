/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class MultidimensionalArrayHandler0 extends MultidimensionalArrayHandler {

    public MultidimensionalArrayHandler0(ArrayHandler template, HandlerRegistry registry, int version) {
        super(template, registry, version);
    }
    
    public Object read(ReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        
        BufferImpl buffer = (BufferImpl) context.readIndirectedBuffer();
        if (buffer == null) {
            return null;
        }
        
        // With the following line we ask the context to work with 
        // a different buffer. Should this logic ever be needed by
        // a user handler, it should be implemented by using a Queue
        // in the UnmarshallingContext.
        
        // The buffer has to be set back from the outside!  See below
        ReadWriteBuffer contextBuffer = context.buffer(buffer);
        
        Object array = super.read(context);
        
        // The context buffer has to be set back.
        context.buffer(contextBuffer);
        
        return array;
    }

    // FIXME copied from ArrayHandler0
    public void defragment(DefragmentContext context) {
    	int sourceAddress = context.sourceBuffer().readInt();
    	int length = context.sourceBuffer().readInt();
    	if(sourceAddress == 0 && length == 0) {
        	context.targetBuffer().writeInt(0);
        	context.targetBuffer().writeInt(0);
        	return;
    	}
    	Slot slot = context.allocateMappedTargetSlot(sourceAddress, length);
    	BufferImpl sourceBuffer = null;
    	try {
			sourceBuffer = context.sourceBufferByAddress(sourceAddress, length);
		}
    	catch (IOException exc) {
    		throw new Db4oIOException(exc);
		}
    	DefragmentContextImpl payloadContext = new DefragmentContextImpl(sourceBuffer, (DefragmentContextImpl) context);
    	defrag1(payloadContext);
    	payloadContext.writeToTarget(slot.address());
    	context.targetBuffer().writeInt(slot.address());
    	context.targetBuffer().writeInt(length);
    }

}
