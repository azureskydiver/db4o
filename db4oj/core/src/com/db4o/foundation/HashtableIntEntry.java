/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * 
 */
class HashtableIntEntry implements Entry4, DeepClone  {

	int _key;

	Object _object;

	HashtableIntEntry _next;

	HashtableIntEntry(int a_hash, Object a_object) {
		_key = a_hash;
		_object = a_object;
	}

	protected HashtableIntEntry() {
	}

	public Object key() {
		return new Integer(_key);
	}
	
	public Object value(){
		return _object;
	}

	public Object deepClone(Object obj) {
		return deepCloneInternal(new HashtableIntEntry(), obj);
	}

	public boolean sameKeyAs(HashtableIntEntry other) {
		return _key == other._key;
	}

	protected HashtableIntEntry deepCloneInternal(HashtableIntEntry entry, Object obj) {
		entry._key = _key;
		entry._next = _next;
		if (_object instanceof DeepClone) {
			entry._object = ((DeepClone) _object).deepClone(obj);
		} else {
			entry._object = _object;
		}
		if (_next != null) {
			entry._next = (HashtableIntEntry) _next.deepClone(obj);
		}
		return entry;
	}
}
