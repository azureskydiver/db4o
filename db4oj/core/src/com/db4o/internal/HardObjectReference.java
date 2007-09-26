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
	    Object obj = trans.container().peekPersisted(trans, id, depth, true);
	    if(obj == null){
	        return null;
	    }
	    ObjectReference ref = trans.referenceForId(id);
		return new HardObjectReference(ref, obj);
	}
}
