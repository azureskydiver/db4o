/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.Hashtable4;

/**
 * @exclude
 */
public class MigrationConnection {

    private final Hashtable4 _referenceMap;

    MigrationConnection() {
        _referenceMap = new Hashtable4(1);
    }

    public void mapReference(Object obj, YapObject ref) {
        
        // FIXME: Identityhashcode is not unique
        
        // ignored for now, since it is on most VMs.
        
        // This should be fixed by adding 
        // putIdentity and getIdentity methods to Hashtable4,
        // using the actual object as the parameter and 
        // checking for object identity in addition to the
        // hashcode
        
        _referenceMap.put(System.identityHashCode(obj), ref);
    }

    public YapObject referenceFor(Object obj) {
        int hcode = System.identityHashCode(obj);
        YapObject ref = (YapObject) _referenceMap.get(hcode);
        _referenceMap.remove(hcode);
        return ref;
    }

}
