/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * 
 */
class HashtableIntEntry implements Cloneable, DeepClone {
    
    int i_key;
    Object i_object;
    HashtableIntEntry i_next;
    
    HashtableIntEntry(int a_hash, Object a_object) {
        i_key = a_hash;
        i_object = a_object;
    }

    public Object deepClone(Object obj) {
        HashtableIntEntry hie = null;
        try {
            hie = (HashtableIntEntry)clone();
        } catch (CloneNotSupportedException e) {
            // wont happen
        }
        hie.i_object = ((DeepClone)i_object).deepClone(obj);
        if(i_next != null){
            hie.i_next = (HashtableIntEntry)i_next.deepClone(obj);    
        }
        return hie;
    }
}
