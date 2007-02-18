/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal.btree;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class BTreeCancelledRemoval extends BTreeUpdate {
    
    private final Object _newKey;
    
	public BTreeCancelledRemoval(Transaction transaction, Object originalKey, Object newKey, BTreeUpdate existingPatches) {
		super(transaction, originalKey);
		_newKey = newKey;
		if (null != existingPatches) {
			append(existingPatches);
		}
	}
	
	protected void committed(BTree btree) {
	}
	
	public boolean isCancelledRemoval() {
		return true;
	}
    
    public String toString() {
        return "(u) " + super.toString();
    }

	protected Object getCommittedObject() {
		return _newKey;
	}
	
	public BTreeUpdate replacePatch(BTreePatch patch, BTreeUpdate update) {
		if(patch == this){
			update._object = _object;
		}
		return super.replacePatch(patch, update);
	}
}
