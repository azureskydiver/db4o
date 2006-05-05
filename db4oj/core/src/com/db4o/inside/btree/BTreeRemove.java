/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;


public class BTreeRemove extends BTreePatch {

    public BTreeRemove(Transaction transaction, Object obj) {
        super(transaction, obj);
    }
    
    public Object getObject(Transaction trans) {
        if(trans == _transaction){
            return Null.INSTANCE;
        }
        return super.getObject(trans);
    }
    
    public String toString() {
        return "-B " + super.toString();
    }


}
