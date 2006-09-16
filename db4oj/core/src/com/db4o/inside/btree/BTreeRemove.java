/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.Transaction;

/**
 * @exclude
 */
public class BTreeRemove extends BTreeUpdate {
	
	public BTreeRemove(Transaction transaction, Object obj) {
        super(transaction, obj);
    }
    
    protected void committed(BTree btree){
        btree.notifyRemoveListener(getObject());
    }
    
    public String toString() {
        return "(-) " + super.toString();
    }
    
    protected void applyKeyChange(Object obj) {
        _object = obj;
        if (hasNext()) {
            _next.applyKeyChange(obj);      
        }
    }
    
    public boolean isRemove() {
        return true;
    }
    
}
