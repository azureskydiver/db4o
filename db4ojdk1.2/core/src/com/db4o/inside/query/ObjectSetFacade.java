/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import java.util.AbstractList;

import com.db4o.ext.ExtObjectSet;

/**
 * @exclude 
 */
public class ObjectSetFacade extends AbstractList implements ExtObjectSet {
    
    public final StatefulQueryResult _delegate;
    
    public ObjectSetFacade(QueryResult qResult){
        _delegate = new StatefulQueryResult(qResult);
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
    
    public boolean contains(Object a_object) {
        return indexOf(a_object) >= 0;
    }

    public Object get(int index) {
        return _delegate.get(index);
    }

    public int indexOf(Object a_object) {
    	return _delegate.indexOf(a_object);
    }
    
    public int lastIndexOf(Object a_object) {
        return indexOf(a_object);
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
