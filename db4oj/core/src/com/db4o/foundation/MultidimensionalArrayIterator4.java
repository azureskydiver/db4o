/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class MultidimensionalArrayIterator4 implements Iterator4 {
    
    private final Object[] _array;
    
    private int _currentElement;
    
    private Iterator4 _delegate;
    
    public MultidimensionalArrayIterator4(Object[] array) {
        _array = array;
        reset();
    }

    public Object current() {
        if(_delegate == null){
            return _array[_currentElement];
        }
        return _delegate.current();
    }

    public boolean moveNext() {
        if(_delegate != null){
            if(_delegate.moveNext()){
                return true;
            }
            _delegate = null;
        }
        _currentElement++;
        if(_currentElement >= _array.length){
            return false;
        }
        Object obj = _array[_currentElement];
        if(obj.getClass().isArray()){
            _delegate = new MultidimensionalArrayIterator4((Object[]) obj);
            return moveNext();
        }
        return true;
    }

    public void reset() {
        _currentElement = -1;
        _delegate = null;
    }

}
