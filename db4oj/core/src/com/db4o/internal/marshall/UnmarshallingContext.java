/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;


/**
 * Wraps the low-level details of reading a Buffer, which in turn is a glorified byte array.
 * 
 * @exclude
 */
public class UnmarshallingContext extends ObjectReferenceContext implements HandlerVersionContext{
    
    private Object _object;
    
    private int _addToIDTree;
    
    private boolean _checkIDTree;
    
    public UnmarshallingContext(Transaction transaction, ByteArrayBuffer buffer, ObjectReference ref, int addToIDTree, boolean checkIDTree) {
        super(transaction, buffer, null, ref);
        _addToIDTree = addToIDTree;
        _checkIDTree = checkIDTree;
    }
    
    public UnmarshallingContext(Transaction transaction, ObjectReference ref, int addToIDTree, boolean checkIDTree) {
        this(transaction, null, ref, addToIDTree, checkIDTree);
    }
    
    public Object read(){
        return readInternal(false);
    }
    
    public Object readPrefetch(){
        return readInternal(true);
    }
    
    private final Object readInternal(boolean doAdjustActivationDepthForPrefetch){
        if(! beginProcessing()){
            return _object;
        }
        
        readBuffer(objectID());
        
        if(buffer() == null){
            endProcessing();
            return _object;
        }
        
        ClassMetadata classMetadata = readObjectHeader();
        if(classMetadata == null){
            endProcessing();
            return _object;
        }
        
        _reference.classMetadata(classMetadata);
        
        adjustActivationDepth(doAdjustActivationDepthForPrefetch);
        
        if(_checkIDTree){
            Object objectInCacheFromClassCreation = transaction().objectForIdFromCache(objectID());
            if(objectInCacheFromClassCreation != null){
                _object = objectInCacheFromClassCreation;
                endProcessing();
                return _object;
            }
        }
        
        if(peekPersisted()){
            _object = classMetadata().instantiateTransient(this);
        }else{
            _object = classMetadata().instantiate(this);
        }
        
        endProcessing();
        return _object;
    }

	private void adjustActivationDepth(boolean doAdjustActivationDepthForPrefetch) {
		if(doAdjustActivationDepthForPrefetch){
            adjustActivationDepthForPrefetch();
        } else {
        	if (UnknownActivationDepth.INSTANCE == _activationDepth) {
        		_activationDepth = container().defaultActivationDepth(classMetadata());
        	}
        }
	}

    private void adjustActivationDepthForPrefetch() {
        activationDepth(activationDepthProvider().activationDepthFor(classMetadata(), ActivationMode.PREFETCH));
    }
    
    private ActivationDepthProvider activationDepthProvider() {
    	return container().activationDepthProvider();
	}

	public Object readFieldValue (FieldMetadata field){
        readBuffer(objectID());
        if(buffer() == null){
            return null;
        }
        ClassMetadata classMetadata = readObjectHeader(); 
        if(classMetadata == null){
            return null;
        }
        return readFieldValue(classMetadata, field);
    }

	private ClassMetadata readObjectHeader() {
        _objectHeader = new ObjectHeader(container(), byteArrayBuffer());
        ClassMetadata classMetadata = _objectHeader.classMetadata();
        if(classMetadata == null){
            return null;
        }
        return classMetadata;
    }

    private void readBuffer(int id) {
        if (buffer() == null && id > 0) {
            buffer(container().readReaderByID(transaction(), id)); 
        }
    }
    
    private boolean beginProcessing() {
        return _reference.beginProcessing();
    }
    
    private void endProcessing() {
        _reference.endProcessing();
    }

    public void setStateClean() {
        _reference.setStateClean();
    }

    public Object persistentObject() {
        return _object;
    }

    public void setObjectWeak(Object obj) {
        _reference.setObjectWeak(container(), obj);
    }

    protected boolean peekPersisted() {
        return _addToIDTree == Const4.TRANSIENT;
    }
    
    public Config4Class classConfig() {
        return classMetadata().config();
    }

    public void persistentObject(Object obj) {
        _object = obj;
    }

    
}

