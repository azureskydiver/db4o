/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.QueryComparator;

/**
 * @exclude 
 */
public class StatefulQueryResult {
    
    private final QueryResult _delegate;
    private final Iterable4Adaptor _iterable;
    
    public StatefulQueryResult(QueryResult queryResult){
        _delegate = queryResult;
        _iterable = new Iterable4Adaptor(queryResult);
    }

    public Object get(int index) {
        return _delegate.get(reverseIndex(index));
    }
    
    public long[] getIDs() {
    	long[] ids = new long[size()];
        int i = 0;
        final IntIterator4 iterator = _delegate.iterateIDs();
        while (iterator.moveNext()) {
        	ids[i++] = iterator.currentInt();
        }
        return ids;
    }

    public boolean hasNext() {
        return _iterable.hasNext();
    }

    public Object next() {
        return _iterable.next();
    }

    public void reset() {
        _iterable.reset();
    }

    public int size() {
        return _delegate.size();
    }

	public void sort(QueryComparator cmp) {
		_delegate.sort(cmp);
	}	
		
	Object streamLock() {
		// used on the .net version
		return objectContainer().lock();
	}
	
	ExtObjectContainer objectContainer() {
		return _delegate.objectContainer();
	}
	
	public int indexOf(Object a_object) {	
		synchronized(streamLock()){
	        int id = (int)objectContainer().getID(a_object);
	        if(id <= 0){
	            return -1;
	        }
	        return reverseIndex(_delegate.indexOf(id));
	    }
	}

	// TODO: get rid of this
	private int reverseIndex(int idx) {
	    return size()-idx-1;
	}

	public Iterator4 iterateIDs() {
		return _delegate.iterateIDs();
	}

	public Iterator4 iterator() {
		return _delegate.iterator();
	}
}
