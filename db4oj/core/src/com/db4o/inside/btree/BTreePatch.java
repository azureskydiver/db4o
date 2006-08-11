/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;


public abstract class BTreePatch {    
    
    protected final Transaction _transaction;
    
    protected final Object _object;

    public BTreePatch(Transaction transaction, Object obj) {
        _transaction = transaction;
        _object = obj;
    }    
    
    protected abstract Object commit(Transaction trans, BTree btree);
    
    public boolean isRemove(Transaction trans) {
        BTreePatch patch = forTransaction(trans);
        if(patch == null){
            return false;
        }
        return patch instanceof BTreeRemove;
    }    
    
    public abstract BTreePatch forTransaction(Transaction trans);
    
    public Object getObject(Transaction trans){
        BTreePatch patch = forTransaction(trans);
        if(patch == null){
            return No4.INSTANCE;
        }
        return patch.getObject();
    }
    
    protected abstract Object getObject();
    
    public abstract Object rollback(Transaction trans, BTree btree);
    
    public String toString(){
        if(_object == null){
            return "[NULL]";
        }
        return _object.toString();
    }

	public boolean isAdd(Transaction trans) {
		return false;
	}
}
