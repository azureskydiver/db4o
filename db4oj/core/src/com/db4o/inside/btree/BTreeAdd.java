/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public class BTreeAdd extends BTreePatch{

    public BTreeAdd(Transaction transaction, Object obj) {
        super(transaction, obj);
    }

    protected Object rolledBack(BTree btree){
        btree.notifyRemoveListener(getObject());
        return No4.INSTANCE;
    }
    
    public String toString() {
        return "(+) " + super.toString();
    }

	public Object commit(Transaction trans, BTree btree) {
	    if(_transaction == trans){
	    	return getObject();
	    }
	    return this;
	}

	public BTreePatch forTransaction(Transaction trans) {
	    if(_transaction == trans){
	        return this;
	    }
	    return null;
	}
	
	public Object key(Transaction trans) {
		if (_transaction != trans) {
			return No4.INSTANCE;
		}
		return getObject();
	}

	public Object rollback(Transaction trans, BTree btree) {
	    if(_transaction == trans){
	        return rolledBack(btree);
	    }
	    return this;
	}

    public boolean isAdd() {
        return true;
    }

}
