/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

public class ObjectSetIterator4Facade extends ObjectSetAbstractFacade {

    Iterator4 _delegate;

    public ObjectSetIterator4Facade(Iterator4 delegate_) {
        this._delegate = delegate_;
    }

    public boolean hasNext() {
        return _delegate.hasNext();
    }

    public Object next() {
        return _delegate.next();
    }

}
