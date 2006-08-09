/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreePointer{
    
    private final BTreeNode _node;
    
    private final int _index;
    
    public BTreePointer(BTreeNode node, int index) {
        if(node == null){
            throw new ArgumentNullException();
        }
        _node = node;
        _index = index;
    }
    
    public int index(){
        return _index;
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
    
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(! (obj instanceof BTreePointer)){
            return false;
        }
        BTreePointer other = (BTreePointer) obj;
        
        if(_index != other._index){
            return false;
        }
        
        return _node.equals(other._node);
    }
    
}
