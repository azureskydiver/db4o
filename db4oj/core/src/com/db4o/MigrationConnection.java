/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.Hashtable4;

/**
 * Refer to Klaus's posting at development newsgroup:
 * Does it not require the hashCode to be unique for each object?
 * (System.identityHashCode does not necessarily produce unique > hashCodes).
 * <p/>
 * You are correct.
 * <p/>
 * Let's add putIdentity and getIdentity methods to Hashtable4 to fix.
 * <p/>
 * Please ignore for now. Most VMs I ever tested on produce unique values as
 * long as an object is not gc'd.
 * <p/>
 * TODO FIXME
 */
public class MigrationConnection {

    private final Hashtable4 _referenceMap;

    MigrationConnection() {
        _referenceMap = new Hashtable4(1);
    }

    public void mapReference(Object obj, YapObject ref) {
        _referenceMap.put(System.identityHashCode(obj), ref);
    }

    public YapObject referenceFor(Object obj) {
        int hcode = System.identityHashCode(obj);
        YapObject ref = (YapObject) _referenceMap.get(hcode);
        _referenceMap.remove(hcode);
        return ref;
    }

}
