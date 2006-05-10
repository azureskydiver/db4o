/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * 
 */
class HashtableObjectEntry extends HashtableIntEntry {

	Object i_objectKey;

	HashtableObjectEntry(int a_hash, Object a_key, Object a_object) {
		super(a_hash, a_object);
		i_objectKey = a_key;
	}

	HashtableObjectEntry(Object a_key, Object a_object) {
		super(a_key.hashCode(), a_object);
		i_objectKey = a_key;
	}

	protected HashtableObjectEntry() {
		super();
	}

	public void acceptKeyVisitor(Visitor4 visitor) {
		visitor.visit(i_objectKey);
	}

	public Object deepClone(Object obj) {
        return deepCloneInternal(new HashtableObjectEntry(), obj);
	}
    
    protected HashtableIntEntry deepCloneInternal(HashtableIntEntry entry, Object obj) {
        ((HashtableObjectEntry)entry).i_objectKey = i_objectKey;
        return super.deepCloneInternal(entry, obj);
    }

	public boolean hasKey(Object key) {
		return i_objectKey.equals(key);
	}

	public boolean sameKeyAs(HashtableIntEntry other) {
		return other instanceof HashtableObjectEntry
			? hasKey(((HashtableObjectEntry) other).i_objectKey)
			: false;
	}
}
