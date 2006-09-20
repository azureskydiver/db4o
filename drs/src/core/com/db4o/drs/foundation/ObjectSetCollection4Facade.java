/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.foundation;

import com.db4o.foundation.*;

public class ObjectSetCollection4Facade extends ObjectSetAbstractFacade {
	
    Collection4 _delegate;
    private Iterator4 _currentIterator;
    private boolean _endOfIteration;

    public ObjectSetCollection4Facade(Collection4 delegate_) {
        this._delegate = delegate_;
    }

    public boolean hasNext() {
    	currentIterator();
        return _endOfIteration;
    }	

    public Object next() {
    	Object nextItem = currentIterator().current();
    	moveNext();
    	return nextItem;
    }

    public boolean contains(Object obj) {
        return _delegate.contains(obj);
    }
    
    public int size() {
        return _delegate.size();
    }
   
    private Iterator4 currentIterator() {
        if (_currentIterator == null) {
        	_currentIterator = _delegate.iterator();
        	_endOfIteration = false;
        	moveNext();
        }
        return _currentIterator;
    }
    
    private void moveNext() {
    	_endOfIteration = _currentIterator.moveNext();
    }

	public void reset() {
		_currentIterator = null;
	}
}
