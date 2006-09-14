/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.Transaction;

/**
 * @exclude
 */
public class BTreeCancelledRemoval extends BTreeUpdate {
	public BTreeCancelledRemoval(Transaction transaction, Object object, BTreeUpdate existingPatches) {
		super(transaction, object);
		append(existingPatches);
	}
	
	public Object rollback(Transaction trans, BTree btree) {
		throw new com.db4o.foundation.NotImplementedException();
	}

	protected void committed(BTree btree) {
		//_next.updateObject()
	}

}
