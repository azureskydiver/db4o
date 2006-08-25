/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.Transaction;
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
    
    public BTreePointer next(Transaction trans){
        int indexInMyNode = _index + 1;
        while(indexInMyNode < _node.count()){
            if(_node.indexIsValid(trans, indexInMyNode)){
                return new BTreePointer(_node, indexInMyNode);
            }
            indexInMyNode ++;
        }
        int newIndex = -1;
        BTreeNode nextNode = _node;
        while(newIndex == -1){
            nextNode = nextNode.nextNode();
            if(nextNode == null){
                return null;
            }

            // TODO: Try to operate the node in read mode wherever
            //       that is possible
            nextNode.prepareWrite(trans);
            
            newIndex = nextNode.firstKeyIndex(trans);
        }
        return new BTreePointer(nextNode, newIndex);
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

	Object key(Transaction trans) {
		return node().key(trans, index());
	}
	
	Object value() {
		return node().value(index());
	}
    
    public String toString() {
        String key = "[Unavail]";
        try{
            key = key(null).toString();
        }catch(Exception e){
            
        }
        return "BTreePointer (" + _index + ") to " + key + " on" + node().toString();      
    }
    
}
