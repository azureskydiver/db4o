/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * 
 */
class HashtableObjectEntry extends HashtableIntEntry {
	
	Object i_objectKey;
	
	private HashtableObjectEntry() {
		super();
    }
    
    HashtableObjectEntry(Object a_key, Object a_object) {
        super(a_key.hashCode(), a_object);
        i_objectKey = a_key;
    }
    
    HashtableObjectEntry(int a_hash, Object a_key, Object a_object) {
        super(a_hash, a_object);
        i_objectKey = a_key;
    }
    
    public Object deepClone(Object obj) {
    	HashtableObjectEntry ret=new HashtableObjectEntry();
    	deepCloneInternal(ret, obj);
    	ret.i_objectKey=i_objectKey;
    	return ret;
    }
    
    public boolean sameKeyAs(HashtableIntEntry other) {
    		return other instanceof HashtableObjectEntry
    			? hasKey(((HashtableObjectEntry)other).i_objectKey)
    			: false;
    }

	public boolean hasKey(Object key) {
		return i_objectKey.equals(key);
	}
	
	public void acceptKeyVisitor(Visitor4 visitor) {
		visitor.visit(i_objectKey);
	}
}
