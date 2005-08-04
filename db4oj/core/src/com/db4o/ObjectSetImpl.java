/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

/**
 * @exclude 
 */
public class ObjectSetImpl implements ExtObjectSet{
    
    public final QResult _delegate;
    
    public ObjectSetImpl(QResult qResult){
        _delegate = qResult;
    }

    public long[] getIDs() {
        return _delegate.getIDs();
    }

    public ExtObjectSet ext() {
        return this;
    }

    public boolean hasNext() {
        return _delegate.hasNext();
    }

    public Object next() {
        return _delegate.next();
    }

    public void reset() {
        _delegate.reset();
    }

    public int size() {
        return _delegate.size();
    }
    
}
