/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;


public class BTreeRange {
    
    private BTreePointer _start;
    
    private BTreePointer _end;

    public BTreeRange(BTreePointer start, BTreePointer end) {
        _start = start;
        _end = end;
    }

    public void traverse(Visitor4 visitor) {
        
        
        
    }
    
    
    

}
