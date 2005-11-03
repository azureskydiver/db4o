/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;


public class NIxPath extends Tree {
    
    
    NIxPathNode _head;
    
    boolean _takePreceding;
    
    boolean _takeSubsequent;
    
    
    public NIxPath(){
        
    }
    
    public NIxPath(NIxPathNode head, boolean takePreceding, boolean takeSubsequent){
        _head = head;
        _takePreceding = takePreceding;
        _takeSubsequent = takeSubsequent;
    }


    
    
    public int compare(Tree a_to) {
        return _head.compare(((NIxPath)a_to)._head);
    }
    
    
    
    

}
