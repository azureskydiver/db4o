/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.util.*;

/**
 * 
 * @decaf.ignore.jdk11
 */
public class JdkCollectionIterator4 implements Iterator4{
    
    private static final Object INVALID = new Object();
    
    private final Collection _collection;
    
    private Iterator _iterator;
    
    private Object _current;
    
    public JdkCollectionIterator4(Collection collection) {
        _collection = collection;
        reset();
    }

    public Object current() {
        if(_current == INVALID){
            throw new IllegalStateException();
        }
        return _current;
    }

    public boolean moveNext() {
        if(_iterator.hasNext()){
            _current = _iterator.next();
            return true;
        }
        _current = INVALID;
        return false;
    }

    public void reset() {
        _iterator = _collection.iterator();
        _current = INVALID; 
    }

}