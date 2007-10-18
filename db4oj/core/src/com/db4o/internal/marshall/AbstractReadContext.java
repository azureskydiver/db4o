/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;


/**
 * @exclude
 */
public abstract class AbstractReadContext implements InternalReadContext {
    
    protected final Transaction _transaction;
    
    protected Buffer _buffer;
    
    protected ActivationDepth _activationDepth;
    
    protected AbstractReadContext(Transaction transaction){
        _transaction = transaction;
    }
    
    protected AbstractReadContext(Transaction transaction, Buffer buffer){
        _transaction = transaction;
        _buffer = buffer;
    }
    
    public Buffer buffer(Buffer buffer) {
        Buffer temp = _buffer;
        _buffer = buffer;
        return temp;
    }
    
    public Buffer buffer() {
        return _buffer;
    }

    public ObjectContainerBase container(){
        return _transaction.container();
    }

    public ObjectContainer objectContainer() {
        return (ObjectContainer) container();
    }

    public Transaction transaction() {
        return _transaction;
    }

    public byte readByte() {
        return _buffer.readByte();
    }

    public void readBytes(byte[] bytes) {
        _buffer.readBytes(bytes);
    }

    public int readInt() {
        return _buffer.readInt();
    }

    public long readLong() {
        return _buffer.readLong();
    }

    public Object read(TypeHandler4 handlerType) {
        TypeHandler4 handler = correctHandlerVersion(handlerType);
        if(! isIndirected(handler)){
            return handler.read(this);
        }
        int indirectedOffSet = readInt();
        readInt(); // length, not needed
        int offset = offset();
        seek(indirectedOffSet);
        Object obj = handler.read(this);
        seek(offset);
        return obj;
    }


    public Object readObject() {
        int id = readInt();
        if (id == 0) {
        	return null;
        }
        
        if (peekPersisted()) {
            return container().peekPersisted(transaction(), id, activationDepth().descend(classMetadataForId(id), ActivationMode.PEEK), false);
        }

        Object obj = container().getByID2(transaction(), id);
        
        ActivationDepth depth = activationDepth().descend(classMetadataForId(id), ActivationMode.ACTIVATE);

        // this is OK for primitive YapAnys. They will not be added
        // to the list, since they will not be found in the ID tree.
        container().stillToActivate(transaction(), obj, depth);

        return obj;
    }

    private ClassMetadata classMetadataForId(int id) {
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
    
    public Object readObject(TypeHandler4 handlerType) {
        TypeHandler4 handler = correctHandlerVersion(handlerType);
        if(! isIndirected(handler)){
            return handler.read(this);
        }
        int payLoadOffset = readInt();
        readInt(); // length - never used
        if(payLoadOffset == 0){
            return null;
        }
        int savedOffset = offset();
        seek(payLoadOffset);
        Object obj = handler.read(this);
        seek(savedOffset);
        return obj;
    }
    
    public ActivationDepth activationDepth() {
        return _activationDepth;
    }
    
    public void activationDepth(ActivationDepth depth){
        _activationDepth = depth;
    }
    
    public int offset() {
        return _buffer.offset();
    }

    public void seek(int offset) {
        _buffer.seek(offset);
    }

    public boolean isIndirected(TypeHandler4 handler) {
        if(handlerVersion() == 0){
            return false;
        }
        return handlerRegistry().isVariableLength(handler);
    }
    
    private HandlerRegistry handlerRegistry(){
        return container().handlers();
    }

    public boolean oldHandlerVersion() {
        return handlerVersion() != MarshallingContext.HANDLER_VERSION;
    }

    public TypeHandler4 correctHandlerVersion(TypeHandler4 handler){
        if(! oldHandlerVersion()){
            return handler;
        }
        return container().handlers().correctHandlerVersion(handler, handlerVersion());
    }
    
    public abstract int handlerVersion();


}
