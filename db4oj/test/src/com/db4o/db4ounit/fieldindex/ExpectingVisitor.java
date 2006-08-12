/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.foundation.*;


public class ExpectingVisitor implements Visitor4{
    
    private static final boolean DEBUG = false;
    
    private final Object[] _expected;
    
    private boolean _unexpected;
    
    private static final Object FOUND = new Object();

    
    public ExpectingVisitor(Object[] results){
        _expected = results;
    }

    public void visit(Object obj) {
        for (int i = 0; i < _expected.length; i++) {
            if(obj.equals(_expected[i])){
                ods("Expected OK: " + obj.toString());
                _expected[i] = FOUND;
                return;
            }
        }
        _unexpected = true;
        ods("Unexpected: " + obj.toString());
    }
    
    private static void ods(String message) {
        if(DEBUG){
            System.out.println(message);
        }
    }
    
    public boolean allAsExpected(){
        if(_unexpected){
            return false;
        }
        for (int i = 0; i < _expected.length; i++) {
            if( _expected[i] != FOUND){
                return false;
            }
        }
        return true;
    }
    
}
