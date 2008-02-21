/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public abstract class PrimitiveHandler implements IndexableTypeHandler, BuiltinTypeHandler {
    
    protected final ObjectContainerBase _stream;
    
    protected ReflectClass _classReflector;
    
    private ReflectClass _primitiveClassReflector;
    
    public PrimitiveHandler(ObjectContainerBase stream) {
        _stream = stream;
    }

    public Object coerce(ReflectClass claxx, Object obj) {
        return Handlers4.handlerCanHold(this, claxx) ? obj : No4.INSTANCE;
    }
    
    public abstract Object defaultValue();

    public void delete(DeleteContext context) {
    	context.seek(context.offset() + linkLength());
    }
    
    public Object indexEntryToObject(Transaction trans, Object indexEntry){
        return indexEntry;
    }
    
    protected abstract Class primitiveJavaClass();
    
    protected Class javaClass(){
        if(NullableArrayHandling.disabled()){
            return defaultValue().getClass();
        }
        return Platform4.nullableTypeFor(primitiveJavaClass());
    }
    
    public abstract Object primitiveNull();

    /**
     * 
     * @param mf
     * @param buffer
     * @param redirect
     */
    public Object read(
        
        /* FIXME: Work in progress here, this signature should not be used */
        MarshallerFamily mf,
        
        
        StatefulBuffer buffer, boolean redirect) throws CorruptionException {
    	return read1(buffer);
    }

    abstract Object read1(ByteArrayBuffer reader) throws CorruptionException;

    public Object readIndexEntry(ByteArrayBuffer buffer) {
        try {
            return read1(buffer);
        } catch (CorruptionException e) {
        }
        return null;
    }
    
    public Object readIndexEntry(MarshallerFamily mf, StatefulBuffer a_writer) throws CorruptionException{
        return read(mf, a_writer, true);
    }
    
    public ReflectClass classReflector(){
        ensureClassReflectorLoaded();
    	return _classReflector;  
    }
    
    public ReflectClass primitiveClassReflector(){
        ensureClassReflectorLoaded();
    	return _primitiveClassReflector;  
    }
    
    private void ensureClassReflectorLoaded(){
        if(_classReflector != null){
            return;
        }
        _classReflector = _stream.reflector().forClass(javaClass());
        Class clazz = primitiveJavaClass();
        if(clazz != null){
            _primitiveClassReflector = _stream.reflector().forClass(clazz);
        }
    }

    public abstract void write(Object a_object, ByteArrayBuffer a_bytes);
    
    public void writeIndexEntry(ByteArrayBuffer a_writer, Object a_object) {
        if (a_object == null) {
            a_object = primitiveNull();
        }
        write(a_object, a_writer);
    }
    
    // redundant, only added to make Sun JDK 1.2's java happy :(
    public abstract int linkLength();
    
    public final void defragment(DefragmentContext context) {
    	context.incrementOffset(linkLength());
    }
    
    public void defragIndexEntry(DefragmentContextImpl context) {
    	try {
			read1(context.sourceBuffer());
			read1(context.targetBuffer());
		} catch (CorruptionException exc) {
			Exceptions4.virtualException();
		}
    }

	protected PrimitiveMarshaller primitiveMarshaller() {
		return MarshallerFamily.current()._primitive;
	}
	
    public void write(WriteContext context, Object obj) {
        throw new NotImplementedException();
    }
    
    public Object read(ReadContext context) {
        throw new NotImplementedException();
    }
    
    public Object nullRepresentationInUntypedArrays(){
        return primitiveNull();
    }
    
	public PreparedComparison prepareComparison(final Object obj) {
		if(obj == null){
			return Null.INSTANCE;
		}
		return internalPrepareComparison(obj);
	}
	
	public abstract PreparedComparison internalPrepareComparison(final Object obj);


}