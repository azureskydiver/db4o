/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;


/**
 * @exclude
 */
public class MigrationConnection {
    
    private final Hashtable4 _referenceMap;
    
    MigrationConnection(){
        _referenceMap = new Hashtable4(1);
    }
    
    public void mapReference(Object obj, YapObject ref) {
        _referenceMap.put(System.identityHashCode(obj), ref);
    }

    public YapObject referenceFor(Object obj){
        int hcode = System.identityHashCode(obj);
        YapObject ref = (YapObject)_referenceMap.get(hcode);
        _referenceMap.remove(hcode);
        return ref;
    }

}
