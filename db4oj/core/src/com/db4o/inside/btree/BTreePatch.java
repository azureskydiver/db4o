/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;


public abstract class BTreePatch {    
    
    protected final Transaction _transaction;
    
    private final Object _object;

    public BTreePatch(Transaction transaction, Object obj) {
        _transaction = transaction;
        _object = obj;
    }    
    
    public abstract Object commit(Transaction trans, BTree btree);

    public boolean isRemove() {
        return false;
    }

    public abstract BTreePatch forTransaction(Transaction trans);
    
    public Object getObject() {
        return _object;
    }
    
    public abstract Object rollback(Transaction trans, BTree btree);
    
    public String toString(){
        if(_object == null){
            return "[NULL]";
        }
        return _object.toString();
    }
    
    public boolean isAdd() {
        return false;
    }

    public Object key(Transaction trans){
        BTreePatch patch = forTransaction(trans);
        if(patch != null){
            if(patch.isAdd()){
                return patch.getObject();
            }
        }else{
            if(isRemove()){
                return getObject();
            }
        }
        return No4.INSTANCE;
    }
}
