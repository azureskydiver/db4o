/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.marshall.MarshallerFamily;


/**
 * @exclude
 */
final class CustomMarshallerFieldMetadata extends FieldMetadata {

	private final ObjectMarshaller _marshaller;
	
	public CustomMarshallerFieldMetadata(ClassMetadata containingClass, ObjectMarshaller marshaller) {
		super(containingClass, marshaller);
		_marshaller = marshaller;
	}
	
    public void defragField(MarshallerFamily mf,BufferPair readers) {
    	readers.incrementOffset(linkLength());
    }
	
    public void delete(MarshallerFamily mf, StatefulBuffer a_bytes, boolean isUpdate) {
    	incrementOffset(a_bytes);
    }
	
	public boolean hasIndex() {
		return false;
	}
	
	public Object getOn(Transaction a_trans, Object obj) {
		return obj;
	}
	 
	public Object getOrCreate(Transaction trans, Object onObject) {
		return onObject;
	}
	
	public void set(Object onObject, Object obj) {
		// do nothing
	}
	
	public void instantiate(MarshallerFamily mf, ObjectReference ref, Object onObject,
		StatefulBuffer reader) throws CorruptionException {
		_marshaller.readFields(onObject, reader._buffer, reader._offset);
		incrementOffset(reader);
	}
	
	protected int linkLength() {
		return _marshaller.marshalledFieldLength();
	}
	

}
