/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRemove extends BTreePatch {
	
	private BTreeRemove _next;

    public BTreeRemove(Transaction transaction, Object obj) {
        super(transaction, obj);
    }
    
    protected Object committed(BTree btree){
        btree.notifyRemoveListener(getObject());
        return No4.INSTANCE;
    }
    
    public String toString() {
        return "(-) " + super.toString();
    }
    
    public void append(BTreeRemove patch){
        if(_transaction == patch._transaction){
        	// don't allow two patches for the same transaction
            throw new IllegalArgumentException();
        }
        if(_next == null){
            _next = patch;
        }else{
            _next.append(patch);
        }
    }

	protected Object commit(Transaction trans, BTree btree) {
	    if(_transaction == trans){
	        if(hasNext()){
	            return _next;
	        }
	        return committed(btree);
	    }
	    if(hasNext()){
	        Object newNext = _next.commit(trans, btree);
	        if(newNext instanceof BTreeRemove){
	            _next = (BTreeRemove)newNext;
	        } else{
	            _next = null;
	        }
	    }
	    return this;
	}
	
	private boolean hasNext() {
		return _next != null;
	}
    
    public boolean isRemove() {
        return true;
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

	public Object rollback(Transaction trans, BTree btree) {
	    if(_transaction == trans){
	        if(hasNext()){
	            return _next;
	        }
	        return getObject();
	    }
	    if(hasNext()){
	        Object newNext = _next.rollback(trans, btree);
	        if(newNext instanceof BTreeRemove){
	            _next = (BTreeRemove)newNext;
	        } else{
	            _next = null;
	        }
	    }
	    return this;
	}

	public BTreeRemove removeFor(Transaction trans) {
		if (_transaction == trans) {
			return _next;
		}
		if (_next == null) {
			return this;
		}
		return _next.removeFor(trans);
	}
    
}
