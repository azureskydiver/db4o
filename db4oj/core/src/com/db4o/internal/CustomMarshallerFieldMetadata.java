/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.marshall.MarshallerFamily;
import com.db4o.internal.marshall.ObjectHeaderAttributes;;


/**
 * @exclude
 */
final class CustomMarshallerFieldMetadata extends FieldMetadata {

	private final ObjectMarshaller _marshaller;
	
	public CustomMarshallerFieldMetadata(ClassMetadata containingClass, ObjectMarshaller marshaller) {
		super(containingClass, marshaller);
		_marshaller = marshaller;
	}
	
	public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, Object obj) {
		header.addBaseLength(linkLength());
	}
	
	public boolean hasIndex() {
		return false;
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
	
	public void marshall(ObjectReference yo, Object obj, MarshallerFamily mf,
		StatefulBuffer writer, Config4Class config, boolean isNew) {
		_marshaller.writeFields(obj, writer._buffer, writer._offset);
		incrementOffset(writer);
	}
	
	public int linkLength() {
		return _marshaller.marshalledFieldLength();
	}
	

}
