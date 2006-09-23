/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class MutableInt {
    
    private int _value;
    
    public MutableInt() {
        
    }
    
    public MutableInt(int value) {
        _value = value;
    }
    
    public void add(int addVal){
        _value += addVal;
    }
    
    public void substract(int substractVal){
        _value -= substractVal;
    }
    
    public int value(){
        return _value;
    }

}
