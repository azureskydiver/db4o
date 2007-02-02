/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.ext.*;
import com.db4o.inside.query.result.*;
import com.db4o.query.*;

/**
 * @exclude 
 * @sharpen.ignore
 */
public class ObjectSetFacade implements ExtObjectSet {
    
	// TODO: encapsulate field
    public final StatefulQueryResult _delegate;
    
    public ObjectSetFacade(QueryResult queryResult){
        _delegate = new StatefulQueryResult(queryResult);
    }

    public Object get(int index) {
        return _delegate.get(index);
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

	public void sort(QueryComparator cmp) {
		_delegate.sort(cmp);
	}	
}
