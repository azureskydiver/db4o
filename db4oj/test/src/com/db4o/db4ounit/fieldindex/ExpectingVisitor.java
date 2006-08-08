/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.foundation.*;


public class ExpectingVisitor implements Visitor4{
    
    private final Object[] _expected;
    
    private static final Object FOUND = new Object(); 
    
    public ExpectingVisitor(Object[] results){
        _expected = results;
    }

    public void visit(Object obj) {
        for (int i = 0; i < _expected.length; i++) {
            if(obj.equals(_expected[i])){
                _expected[i] = FOUND;
            }
        }
    }
    
    public boolean allFound(){
        for (int i = 0; i < _expected.length; i++) {
            if( _expected[i] != FOUND){
                return false;
            }
        }
        return true;
    }
    
}
