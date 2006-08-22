/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;


/**
 * @exclude
 */
public class FieldIndexKey {
    
    private final int _parentID;
    
    private final Object _value;
    
    public FieldIndexKey(int parentID_, Object value_){
        _parentID = parentID_;
        _value = value_;
    }
    
    public int parentID(){
        return _parentID;
    }
    
    public Object value(){
        return _value;
    }
}
