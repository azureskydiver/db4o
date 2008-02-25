/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.handlers.*;


/**
 * @exclude
 */
public abstract class AbstractReadContext extends BufferContext implements InternalReadContext {
    
    protected ActivationDepth _activationDepth = UnknownActivationDepth.INSTANCE;
    
    protected AbstractReadContext(Transaction transaction, ByteArrayBuffer buffer){
    	super(transaction, buffer);
    }
    
    protected AbstractReadContext(Transaction transaction){
    	this(transaction, null);
    }
    
    public final Object read(TypeHandler4 handlerType) {
        return readObject(handlerType);
    }
    
    public final Object readObject(TypeHandler4 handlerType) {
        TypeHandler4 handler = correctHandlerVersion(handlerType);
        if(! isIndirectedWithinSlot(handler)){
            return readAtCurrentSeekPosition(handler);
        }
        int payLoadOffset = readInt();
        readInt(); // length - never used
        if(payLoadOffset == 0){
            return null;
        }
        int savedOffset = offset();
        seek(payLoadOffset);
        Object obj = readAtCurrentSeekPosition(handler);
        seek(savedOffset);
        return obj;
    }
    
    public Object readAtCurrentSeekPosition(TypeHandler4 handler){
        if(ObjectHandlerRefactoring.enabled){
            if(handler instanceof ClassMetadata){
                ClassMetadata classMetadata = (ClassMetadata) handler;
                if(classMetadata.isValueType()){
                    return classMetadata.readValueType(transaction(), readInt(), activationDepth().descend(classMetadata));
                }
            }
        }
        if(FieldMetadata.useDedicatedSlot(this, handler, null)){
            return readObject();
        }
        return handler.read(this);
    }

    public final Object readObject() {
        int id = readInt();
        if (id == 0) {
        	return null;
        }
        
        final ClassMetadata classMetadata = classMetadataForId(id);
        if (null == classMetadata) {
        	return null;
        }
        
		ActivationDepth depth = activationDepth().descend(classMetadata);
        if (peekPersisted()) {
            return container().peekPersisted(transaction(), id, depth, false);
        }

        Object obj = container().getByID2(transaction(), id);
        if (null == obj) {
        	return null;
        }

        // this is OK for primitive YapAnys. They will not be added
        // to the list, since they will not be found in the ID tree.
        container().stillToActivate(transaction(), obj, depth);

        return obj;
    }

    private ClassMetadata classMetadataForId(int id) {
        
        // TODO: This method is *very* costly as is, since it reads
        //       the whole slot once and doesn't reuse it. Optimize.
        
    	HardObjectReference hardRef = container().getHardObjectReferenceById(transaction(), id);
    	if (null == hardRef || hardRef._reference == null) {
    		// com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
    		return null;
    	}
		return hardRef._reference.classMetadata();
	}

	protected boolean peekPersisted() {
        return false;
    }
    
    public ActivationDepth activationDepth() {
        return _activationDepth;
    }
    
    public void activationDepth(ActivationDepth depth){
        _activationDepth = depth;
    }
    
    public boolean isIndirectedWithinSlot(TypeHandler4 handler) {
        if(handlerVersion() == 0){
            return false;
        }
        return handlerRegistry().isVariableLength(handler);
    }
    
    private HandlerRegistry handlerRegistry(){
        return container().handlers();
    }
    
    public ReadWriteBuffer readIndirectedBuffer() {
        int address = readInt();
        int length = readInt();
        if(address == 0){
            return null;
        }
        return container().bufferByAddress(address, length);
    }


}
