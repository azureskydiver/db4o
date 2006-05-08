/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;


public abstract class BTreePatch {
    
    protected BTreePatch _previous;
    
    protected BTreePatch _next;
    
    protected final Transaction _transaction;
    
    final Object _object;

    public BTreePatch(Transaction transaction, Object obj) {
        _transaction = transaction;
        _object = obj;
    }
    
    public BTreePatch append(BTreePatch patch){
        if(_transaction == patch._transaction){
            patch._next = _next;
            return patch;
        }
        if(_next == null){
            _next = patch;
        }else{
            _next = _next.append(patch);
        }
        return this;
    }
    
    public Object commit(Transaction trans){
        if(_transaction == trans){
            if(_next != null){
                return _next;
            }
            return getObject();
        }
        if(_next != null){
            Object newNext = _next.commit(trans);
            if(newNext instanceof BTreePatch){
                _next = (BTreePatch)newNext;
            } else{
                _next = null;
            }
        }
        return this;
    }
    
    public BTreePatch forTransaction(Transaction trans){
        if(_transaction == trans){
            return this;
        }
        if(_next == null){
            return null;
        }
        return _next.forTransaction(trans);
    }
    
    public Object getObject(Transaction trans){
        BTreePatch patch = forTransaction(trans);
        if(patch == null){
            return No4.INSTANCE;
        }
        return patch.getObject();
    }
    
    protected abstract Object getObject();
    
    
    public Object rollback(Transaction trans){
        if(_transaction == trans){
            if(_next != null){
                return _next;
            }
            return rolledBack();
        }
        if(_next != null){
            Object newNext = _next.rollback(trans);
            if(newNext instanceof BTreePatch){
                _next = (BTreePatch)newNext;
            } else{
                _next = null;
            }
        }
        return this;
    }
    
    protected abstract Object rolledBack();
    
    public String toString(){
        if(_object == null){
            return "[NULL]";
        }
        return _object.toString();
    }
    

}
