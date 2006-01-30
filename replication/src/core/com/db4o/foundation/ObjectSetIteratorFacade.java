/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.util.*;


public class ObjectSetIteratorFacade extends ObjectSetAbstractFacade {

    Iterator _delegate;

    public ObjectSetIteratorFacade(Iterator delegate_) {
        this._delegate = delegate_;
    }

    public boolean hasNext() {
        return _delegate.hasNext();
    }

    public Object next() {
        return _delegate.next();
    }

}
