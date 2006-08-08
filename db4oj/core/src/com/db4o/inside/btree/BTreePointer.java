/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;


public class BTreePointer{
    
    private final BTreeNode _node;
    
    private final int _index;
    
    public BTreePointer(BTreeNode node, int index) {
        _node = node;
        _index = index;
    }
    
    public BTreePointer next(){
        if(_index >= _node.count() - 1){
            BTreeNode node = _node.nextNode();
            if(node == null){
                return null;
            }
            return new BTreePointer(node, 0);
        }
        return new BTreePointer(_node, _index + 1);
    }
    
    public BTreeNode node() {
        return _node;
    }
    
    public int index(){
        return _index;
    }
    
}
