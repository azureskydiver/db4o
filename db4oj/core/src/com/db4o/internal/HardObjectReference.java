/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class HardObjectReference {
	
	public static final HardObjectReference INVALID = new HardObjectReference(null, null);
	
	public final ObjectReference _reference;
	
	public final Object _object;

	public HardObjectReference(ObjectReference ref, Object obj) {
		_reference = ref;
		_object = obj;
	}
	
	public static HardObjectReference peekPersisted(Transaction trans, int id, int depth) {
		ObjectReference ref = new ObjectReference(id);
		Object obj = ref.peekPersisted(trans, depth);
		return new HardObjectReference(ref, obj);
	}
}
