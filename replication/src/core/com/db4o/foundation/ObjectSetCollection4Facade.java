/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

public class ObjectSetCollection4Facade extends ObjectSetAbstractFacade {

    Collection4 _delegate;
    private Iterator4 _currentIterator;

    public ObjectSetCollection4Facade(Collection4 delegate_) {
        this._delegate = delegate_;
    }

    public boolean hasNext() {
        return currentIterator().hasNext();
    }

    public Object next() {
        return currentIterator().next();
    }

    public boolean contains(Object obj) {
        return _delegate.contains(obj);
    }
    
    public int size() {
        return _delegate.size();
    }
   
    private Iterator4 currentIterator() {
        if (_currentIterator == null) _currentIterator = _delegate.iterator();
        return _currentIterator;
    }
}
