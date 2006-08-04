/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;


/**
 * @exclude
 */
public class Searcher {
    
    private int _lower;
    
    private int _upper;
    
    private int _cursor;
    
    private int _cmp;
    
    private final SearchTarget _target;
    
    public Searcher(SearchTarget target, int count){
        _target = target;
        if(count == 0){
            complete();
            return;
        }
        _upper = count - 1;
        _cursor = -1;
        adjustCursor();
    }
    
    private void adjustCursor(){
        int oldCursor = _cursor;
        if(_upper - _lower <= 1){
            _cursor = _upper;
        }else{
            _cursor = _lower + ((_upper - _lower) / 2);
        }
        if(_cursor == oldCursor){
            complete();
        }
    }
    
    public boolean beforeFirst() {
        return _cmp > 0;
    }

    public boolean beyondLast(){
        return _cmp < 0;
    }
    
    private void complete(){
        _upper = -2;
    }
    
    public int cursor() {
        return _cursor;
    }

    public boolean foundMatch(){
        return _cmp == 0;
    }
    
    public boolean incomplete() {
        return _upper >= _lower;
    }
    
    public void moveForward() {
        _cursor++;
    }

    public void resultIs(int cmp){
        _cmp = cmp;
        if(cmp > 0){
            _upper = _cursor - 1;
            if (_upper < _lower) {
                _upper = _lower;
            }
        }else if (cmp < 0) {
            _lower = _cursor + 1;
            if (_lower > _upper) {
                _lower = _upper;
            }
        }else{
            if(_target == SearchTarget.ANY){
                _lower = _cursor;
                _upper = _cursor;
            }else if(_target == SearchTarget.HIGHEST){
                _lower = _cursor;
            }else if(_target == SearchTarget.LOWEST){
                _upper = _cursor;
            }else{
                throw new IllegalStateException("Unknown target");
            }
        }
        adjustCursor();
    }
 
}
