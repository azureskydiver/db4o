/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeAdd extends BTreePatch{

    public BTreeAdd(Transaction transaction, Object obj) {
        super(transaction, obj);
    }

    protected Object getObject() {
        return _object;
    }
    
    protected Object rolledBack(){
        return No4.INSTANCE;
    }
    
    public String toString() {
        return "+B " + super.toString();
    }

}
