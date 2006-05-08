/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRemove extends BTreePatch {

    public BTreeRemove(Transaction transaction, Object obj) {
        super(transaction, obj);
    }
    
    protected Object getObject() {
        return No4.INSTANCE;
    }
    
    protected Object rolledBack(){
        return _object;
    }
    
    public String toString() {
        return "-B " + super.toString();
    }
    
}
