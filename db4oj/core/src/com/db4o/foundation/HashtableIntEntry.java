/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;


/**
 * 
 */
class HashtableIntEntry implements DeepClone {
    
    int i_key;
    Object i_object;
    HashtableIntEntry i_next;
    
    protected HashtableIntEntry() {}
    
    HashtableIntEntry(int a_hash, Object a_object) {
        i_key = a_hash;
        i_object = a_object;
    }

    public Object deepClone(Object obj) {
    	return deepCloneInternal(new HashtableIntEntry(), obj);
    }
    
    protected HashtableIntEntry deepCloneInternal(HashtableIntEntry hie,Object obj) {
        hie.i_key=i_key;
        hie.i_next=i_next;
        if(i_object instanceof DeepClone) {
        	hie.i_object = ((DeepClone)i_object).deepClone(obj);
        }
        else {
        	hie.i_object = i_object;
        }
        if(i_next != null){
            hie.i_next = (HashtableIntEntry)i_next.deepClone(obj);    
        }
        return hie;
    }
}
