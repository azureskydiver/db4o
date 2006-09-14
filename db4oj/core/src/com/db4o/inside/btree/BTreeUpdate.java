/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.inside.btree;

/**
 * @exclude
 */
import com.db4o.Transaction;
import com.db4o.foundation.No4;

public abstract class BTreeUpdate extends BTreePatch {

	protected BTreeUpdate _next;

	public BTreeUpdate(Transaction transaction, Object obj) {
		super(transaction, obj);
	}

	protected boolean hasNext() {
		return _next != null;
	}

	public BTreePatch forTransaction(Transaction trans) {
	    if(_transaction == trans){
	        return this;
	    }
	    if(_next == null){
	        return null;
	    }
	    return _next.forTransaction(trans);
	}

	public BTreeUpdate removeFor(Transaction trans) {
		if (_transaction == trans) {
			return _next;
		}
		if (_next == null) {
			return this;
		}
		return _next.removeFor(trans);
	}

	public void append(BTreeUpdate patch) {
	    if(_transaction == patch._transaction){
	    	// don't allow two patches for the same transaction
	        throw new IllegalArgumentException();
	    }
	    if(!hasNext()){
	        _next = patch;
	    }else{
	        _next.append(patch);
	    }
	}

	protected abstract void committed(BTree btree);

	public Object commit(Transaction trans, BTree btree) {
		final BTreePatch patch = forTransaction(trans);
		if (patch instanceof BTreeCancelledRemoval) {
			Object obj = patch.getObject();
			applyKeyChange(obj);
		} 
	    return internalCommit(trans, btree);
	}

	private void applyKeyChange(Object obj) {
		_object = obj;
		if (hasNext()) {
			_next.applyKeyChange(obj);		
		}
	}

	protected Object internalCommit(Transaction trans, BTree btree) {
		if(_transaction == trans){	        
	        committed(btree);
	        if (hasNext()){
	            return _next;
	        }
	        return No4.INSTANCE;
	    }
	    if(hasNext()){
	        Object newNext = _next.internalCommit(trans, btree);
	        if(newNext == No4.INSTANCE){	        	
	            _next = null;
	        } else{
	        	_next = (BTreeUpdate)newNext;
	        }
	    }
	    return this;
	}

}