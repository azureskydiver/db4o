/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.util.*;

/**
 * 
 * @decaf.ignore.jdk11
 * @sharpen.ignore
 */
public class Iterator4JdkIterator implements Iterator{
    
    private static final Object BEFORE_START = new Object();
    
    private static final Object BEYOND_END = new Object();
    
    private final Iterator4 _delegate;
    
    private Object _current;
    
    public Iterator4JdkIterator(Iterator4 i){
        _delegate = i;
        _current = BEFORE_START; 
    }

    public boolean hasNext() {
        checkBeforeStart();
        return _current != BEYOND_END;
    }

    public Object next() {
        checkBeforeStart();
        if (_current == BEYOND_END){
            throw new NoSuchElementException();
        }
        Object result = _current;
        if(_delegate.moveNext()){
            _current = _delegate.current();
        }else{
            _current = BEYOND_END;
        }
        return result;
    }
    
    private void checkBeforeStart(){
        if(_current != BEFORE_START){
            return;
        }
        if(_delegate.moveNext()){
            _current = _delegate.current();
        }else{
            _current = BEYOND_END;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException(); 
    }
    
}
