/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.QueryComparator;

/**
 * @exclude 
 */
public class ObjectSetFacade extends AbstractList implements ExtObjectSet{
    
    public final QueryResult _delegate;
    
    public ObjectSetFacade(QueryResult qResult){
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
    
    private Object streamLock(){
        return _delegate.streamLock();
    }
    
    private ObjectContainer objectContainer(){
        return _delegate.objectContainer();
    }
    
    public boolean contains(Object a_object) {
        return indexOf(a_object) >= 0;
    }

    public Object get(int index) {
        return _delegate.get(index);
    }

    public int indexOf(Object a_object) {
        synchronized(streamLock()){
            int id = (int)objectContainer().ext().getID(a_object);
            if(id <= 0){
                return -1;
            }
            return _delegate.indexOf(id);
        }
    }

    public int lastIndexOf(Object a_object) {
        return indexOf(a_object);
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }

	public void sort(QueryComparator cmp) {
		_delegate.sort(cmp);
	}
}
