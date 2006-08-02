/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.foundation.Visitor4;

public class CountingVisitor implements Visitor4{
    
    private final Visitor4 _visitor;
    
    private int _counter;
    
    public CountingVisitor(Visitor4 visitor){
        _visitor = visitor;
    }

    public void visit(Object obj) {
        _counter ++;
        _visitor.visit(obj);
    }
    
    public int count(){
        return _counter;
    }

}
