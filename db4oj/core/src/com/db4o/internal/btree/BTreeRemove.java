/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.internal.btree;

import com.db4o.foundation.*;
import com.db4o.internal.*;

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
    
    public boolean isRemove() {
        return true;
    }

	protected Object getCommittedObject() {
		return No4.INSTANCE;
	}

    protected void adjustSizeOnRemovalByOtherTransaction(BTree btree) {
        // The size was reduced for this entry, let's change back.
        btree.sizeChanged(_transaction, +1);
    }
    
}
