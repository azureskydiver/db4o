/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRemove extends BTreePatch {
	
	protected BTreeRemove _next;

    public BTreeRemove(Transaction transaction, Object obj) {
        super(transaction, obj);
    }
    
    protected Object committed(BTree btree){
        btree.notifyRemoveListener(_object);
        return No4.INSTANCE;
    }
    
    protected Object getObject() {
        return No4.INSTANCE;
    }
    
    public String toString() {
        return "-B " + super.toString();
    }
    
    public BTreeRemove append(BTreeRemove patch){
        if(_transaction == patch._transaction){
        	// don't allow two patches for the same transaction
            throw new IllegalArgumentException();
        }
        if(_next == null){
            _next = patch;
        }else{
            _next = _next.append(patch);
        }
        return this;
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

	public Object rollback(Transaction trans, BTree btree) {
	    if(_transaction == trans){
	        if(hasNext()){
	            return _next;
	        }
	        return _object;
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

	public Object removeFor(Transaction trans) {
		if (_transaction == trans) {
			if (_next == null) {
				return _object;
			}
			return _next;
		}
		if (_next == null) {
			return this;
		}
		final Object newNext = _next.removeFor(trans);
		if (newNext instanceof BTreeRemove){
			_next = (BTreeRemove) newNext;
		}else{
			_next = null;
		}
		return this;
	}
    
}
