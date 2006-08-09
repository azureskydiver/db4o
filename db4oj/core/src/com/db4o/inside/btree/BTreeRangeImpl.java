/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreeRangeImpl implements BTreeRange {
    
    private final Transaction _trans;
    
    private final BTreePointer _start;
    
    private final BTreePointer _end; 

    public BTreeRangeImpl(Transaction trans, BTreePointer start, BTreePointer end) {
        _trans = trans;
        _start = start;
        _end = end;
    }

    public void traverseKeys(Visitor4 visitor) {
        
        BTreeNode oldNode = null;
        
        BTreePointer cursor = _start;
        
        while(! reachedEnd(cursor)){
            
            BTreeNode node = cursor.node();
            
            if(node != oldNode){
                node.prepareWrite(_trans);
                
                // Alternative: work in read mode, hold the reader here.
                
                oldNode = node;
            }
            
            visitor.visit(node.key(cursor.index()));
            
            cursor = cursor.next();
        }
    }
    
    private boolean reachedEnd(BTreePointer cursor){
        if(cursor == null){
            return true;
        }
        if(_end == null){
            return false;
        }
        return _end.equals(cursor);
    }

}
