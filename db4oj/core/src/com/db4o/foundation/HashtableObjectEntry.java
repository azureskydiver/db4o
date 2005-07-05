/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * 
 */
class HashtableObjectEntry extends HashtableIntEntry{
    
    Object i_objectKey;
    
    HashtableObjectEntry(Object a_key, Object a_object) {
        super(a_key.hashCode(), a_object);
        i_objectKey = a_key;
    }
    
    HashtableObjectEntry(int a_hash, Object a_key, Object a_object) {
        super(a_hash, a_object);
        i_objectKey = a_key;
    }
}
