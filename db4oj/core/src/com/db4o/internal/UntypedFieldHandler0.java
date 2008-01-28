/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.ext.*;
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
			ByteArrayBuffer sourceBuffer = context.sourceBufferById(sourceId);
			Slot targetPointerSlot = context.allocateMappedTargetSlot(sourceId, Const4.POINTER_LENGTH);
			Slot targetPayloadSlot = context.allocateTargetSlot(sourceBuffer.length());
			ByteArrayBuffer pointerBuffer = new ByteArrayBuffer(Const4.POINTER_LENGTH);
			pointerBuffer.writeInt(targetPayloadSlot.address());
			pointerBuffer.writeInt(targetPayloadSlot.length());
			context.targetWriteBytes(targetPointerSlot.address(), pointerBuffer);

			DefragmentContextImpl payloadContext = new DefragmentContextImpl(sourceBuffer, (DefragmentContextImpl) context);

			int clazzId = payloadContext.copyIDReturnOriginalID();
			TypeHandler4 payloadHandler = payloadContext.typeHandlerForId(clazzId);
			TypeHandler4 versionedPayloadHandler = payloadContext.correctHandlerVersion(payloadHandler);
			versionedPayloadHandler.defragment(payloadContext);
			
			payloadContext.writeToTarget(targetPayloadSlot.address());
			return targetPointerSlot.address();
		}
		catch (IOException ioexc) {
			throw new Db4oIOException(ioexc);
		}
	}

}
