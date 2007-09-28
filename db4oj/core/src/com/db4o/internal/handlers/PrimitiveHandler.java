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

    private boolean i_compareToIsNull;

    public Object coerce(ReflectClass claxx, Object obj) {
        return Handlers4.handlerCanHold(this, claxx) ? obj : No4.INSTANCE;
    }
    
    public abstract Object defaultValue();

    public void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes) {
        a_bytes.incrementOffset(linkLength());
    }
    
    public Object indexEntryToObject(Transaction trans, Object indexEntry){
        return indexEntry;
    }
    
    protected abstract Class primitiveJavaClass();
    
    public abstract Object primitiveNull();
    
    public Object read(MarshallerFamily mf, StatefulBuffer buffer, boolean redirect) throws CorruptionException {
    	return read1(buffer);
    }

    abstract Object read1(Buffer reader) throws CorruptionException;

    public Object readIndexEntry(Buffer buffer) {
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
        _classReflector = _stream.reflector().forClass(defaultValue().getClass());
        Class clazz = primitiveJavaClass();
        if(clazz != null){
            _primitiveClassReflector = _stream.reflector().forClass(clazz);
        }
    }

    public abstract void write(Object a_object, Buffer a_bytes);
    
    public void writeIndexEntry(Buffer a_writer, Object a_object) {
        if (a_object == null) {
            a_object = primitiveNull();
        }
        write(a_object, a_writer);
    }
    
    public Comparable4 prepareComparison(Object obj) {
        if (obj == null) {
            i_compareToIsNull = true;
            return Null.INSTANCE;
        }
        i_compareToIsNull = false;
        prepareComparison1(obj);
        return this;
    }
    
    abstract void prepareComparison1(Object obj);
    
    public int compareTo(Object obj) {
        if (i_compareToIsNull) {
            if (obj == null) {
                return 0;
            }
            return 1;
        }
        if (obj == null) {
            return -1;
        }
        if (isEqual1(obj)) {
            return 0;
        }
        if (isGreater1(obj)) {
            return 1;
        }
        return -1;
    }

    abstract boolean isEqual1(Object obj);

    abstract boolean isGreater1(Object obj);

    abstract boolean isSmaller1(Object obj);

    // redundant, only added to make Sun JDK 1.2's java happy :(
    public abstract int linkLength();
    
    public final void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect) {
    	int linkLength = linkLength();
    	readers.incrementOffset(linkLength);
    }
    
    public void defragIndexEntry(BufferPair readers) {
    	try {
			read1(readers.source());
			read1(readers.target());
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
    


}