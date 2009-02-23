/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public abstract class PrimitiveHandler implements IndexableTypeHandler, BuiltinTypeHandler, EmbeddedTypeHandler, QueryableTypeHandler {
    
    protected ReflectClass _classReflector;
    
    private ReflectClass _primitiveClassReflector;
    
    private Object _primitiveNull;
    
    public Object coerce(Reflector reflector, ReflectClass claxx, Object obj) {
        return Handlers4.handlerCanHold(this, reflector, claxx) ? obj : No4.INSTANCE;
    }
    
    public abstract Object defaultValue();

    public void delete(DeleteContext context) {
    	context.seek(context.offset() + linkLength());
    }
    
    public final Object indexEntryToObject(Context context, Object indexEntry){
        return indexEntry;
    }
    
    public abstract Class primitiveJavaClass();
    
    protected Class javaClass(){
        return Platform4.nullableTypeFor(primitiveJavaClass());
    }
    
    public boolean isSimple() {
    	return true;
    }
    
    public boolean canHold(ReflectClass type) {
    	return type.equals(classReflector())
			|| type.equals(primitiveClassReflector());
    }
    
    protected Object primitiveNull() {
    	if(_primitiveNull == null) {
        	ReflectClass claxx = (_primitiveClassReflector == null ? _classReflector : _primitiveClassReflector);
        	_primitiveNull = claxx.nullValue();
    	}
		return _primitiveNull;
    }

    /**
     * 
     * @param mf
     * @param buffer
     * @param redirect
     */
    public Object read(
        
        /* FIXME: Work in progress here, this signature should not be used from the outside */
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
    
    public final Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer a_writer) throws CorruptionException{
        return read(mf, a_writer, true);
    }
    
    public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException{
        return read(context);
    }
    
    public ReflectClass classReflector(){
    	return _classReflector;  
    }
    
    public ReflectClass primitiveClassReflector(){
    	return _primitiveClassReflector;  
    }
    
    public void registerReflector(Reflector reflector) {
        _classReflector = reflector.forClass(javaClass());
        Class clazz = primitiveJavaClass();
        if(clazz != null){
            _primitiveClassReflector = reflector.forClass(clazz);
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
    
	public PreparedComparison prepareComparison(Context context, final Object obj) {
		if(obj == null){
			return Null.INSTANCE;
		}
		return internalPrepareComparison(obj);
	}
	
	public abstract PreparedComparison internalPrepareComparison(final Object obj);


}