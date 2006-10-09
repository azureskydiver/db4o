/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * Adapts Iterable4/Iterator4 iteration model (moveNext, current) to the old db4o
 * and jdk model (hasNext, next).
 * 
 * @exclude
 */
public class Iterable4Adaptor {
	
	private static final Object EOF = new Object();
	private static final Object BOF = new Object();
	
	private final Iterable4 _delegate;
    
    private Iterator4 _iterator; 
    
    private Object _current;
    
    public Iterable4Adaptor(Iterable4 delegate) {
    	_delegate = delegate;
    }
    
    public boolean hasNext() {
    	if (_current == BOF) {
    		return moveNext();
    	}
    	return _current != EOF;
    }
    
    public Object next() {
    	if (!hasNext()) {
    		throw new IllegalStateException();
    	}
        Object returnValue = _current;
        moveNext();
        return returnValue;
    }

    private boolean moveNext() {
    	if (null == _iterator) {
    		_iterator = _delegate.iterator();
    	}
    	if (_iterator.moveNext()) {
    		_current = _iterator.current();
    		return true;
    	}
    	_current = EOF;
    	return false;
	}

	public void reset() {
        _iterator = null;
        _current = BOF;
    }
}
