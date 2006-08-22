/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.btree;

import com.db4o.foundation.*;

import db4ounit.Assert;


public class ExpectingVisitor implements Visitor4{
    
    private static final boolean DEBUG = false;
    
    private final Object[] _expected;
    
    private final boolean _obeyOrder;
    
    private boolean _unexpected;
    
    private int _cursor;
    
    private static final Object FOUND = new Object() {
    	public String toString() {
    		return "[FOUND]";
    	}
    };
    
    public ExpectingVisitor(Object[] results, boolean obeyOrder){
        _expected = results;
        _obeyOrder = obeyOrder;
    }
    
    public ExpectingVisitor(Object[] results){
        this(results, false);
    }
    
    public ExpectingVisitor(Object singleObject){
        this(new Object[] { singleObject });
    }

    public void visit(Object obj) {
        if(_obeyOrder){
            visitOrdered(obj);
        }else{
            visitUnOrdered(obj);
        }
    }
    
    private void visitOrdered(Object obj){
        if(_cursor < _expected.length){
            if(obj.equals(_expected[_cursor])){
                ods("Expected OK: " + obj.toString());
                _expected[_cursor] = FOUND;
                _cursor ++;
                return;
            }
        }
        _unexpected = true;
        ods("Unexpected: " + obj.toString());
    }
    
    private void visitUnOrdered(Object obj){
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
    
    public void assertExpectations(){
        Assert.isFalse(_unexpected);
        for (int i = 0; i < _expected.length; i++) {
            Assert.areSame(FOUND, _expected[i]);
        }
    }
    
}
