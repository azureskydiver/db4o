/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class UntypedFieldHandler0 extends UntypedFieldHandler {

    public UntypedFieldHandler0(ObjectContainerBase container) {
        super(container);
    }
    
    public Object read(ReadContext context) {
        return context.readObject();
    }
    
    public ObjectID readObjectID(InternalReadContext context){
        int id = context.readInt();
        return id == 0 ? ObjectID.IS_NULL : new ObjectID(id);
    }
    
    public void defragment(DefragmentContext context) {
        int sourceId = context.sourceBuffer().readInt();
        if(sourceId == 0) {
            context.targetBuffer().writeInt(0);
            return;
        }
        int targetId = 0;
        try {
        	targetId = context.mappedID(sourceId);
        }
        catch(MappingNotFoundException exc) {
        	targetId = copyDependentSlot(context, sourceId);
        }
        context.targetBuffer().writeInt(targetId);
    }

	private int copyDependentSlot(DefragmentContext context, int sourceId) {
		try {
			BufferImpl sourceBuffer = context.sourceBufferById(sourceId);
			Slot targetPointerSlot = context.allocateMappedTargetSlot(sourceId, Const4.POINTER_LENGTH);
			Slot targetPayloadSlot = context.allocateTargetSlot(sourceBuffer.length());
			BufferImpl pointerBuffer = new BufferImpl(Const4.POINTER_LENGTH);

// FIXME COR-770 work in progress

			pointerBuffer.writeInt(0);
			pointerBuffer.writeInt(0);

//			pointerBuffer.writeInt(targetPayloadSlot.address());
//			pointerBuffer.writeInt(targetPayloadSlot.length());
			context.targetWriteBytes(targetPointerSlot.address(), pointerBuffer);
//			context.targetWriteBytes(targetPayloadSlot.address(), sourceBuffer);
			return targetPointerSlot.address();
		}
		catch (IOException ioexc) {
			throw new Db4oIOException(ioexc);
		}
	}

}
