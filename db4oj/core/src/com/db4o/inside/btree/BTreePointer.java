/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreePointer{
	
	public static BTreePointer max(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return x;
		}
		if (y == null) {
			return y;
		}
		if (x.compareTo(y) > 0) {
			return x;
		}
		return y;
	}

	public static BTreePointer min(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return y;
		}
		if (y == null) {
			return x;
		}
		if (x.compareTo(y) < 0) {
			return x;
		}
		return y;
	}
    
    private final BTreeNode _node;
    
    private final int _index;

	private final Transaction _transaction;
   
    public BTreePointer(Transaction transaction, BTreeNode node, int index) {
        if(transaction == null || node == null){
            throw new ArgumentNullException();
        }
        _transaction = transaction;
        _node = node;
        _index = index;
    }
    
    public Transaction transaction() {
    	return _transaction;
    }
    
    public int index(){
        return _index;
    }
    
    public BTreePointer next(){
        int indexInMyNode = _index + 1;
        while(indexInMyNode < _node.count()){
            if(_node.indexIsValid(_transaction, indexInMyNode)){
                return new BTreePointer(_transaction, _node, indexInMyNode);
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
            nextNode.prepareRead(_transaction);
            newIndex = nextNode.firstKeyIndex(_transaction);
        }
        return new BTreePointer(_transaction, nextNode, newIndex);
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

	public Object key() {
		return node().key(_transaction, prepareRead(), _index);
	}

	public Object value() {
		return node().value(prepareRead(), index());
	}
	
	private YapReader prepareRead() {
		return node().prepareRead(_transaction);
	}
    
    public String toString() {
        return "BTreePointer(index=" + _index + ", node=" + node() + ")";      
    }

	public int compareTo(BTreePointer y) {
		if (null == y) {
			throw new ArgumentNullException();
		}
		if (btree() != y.btree()) {
			throw new IllegalArgumentException();
		}		
		return btree().compareKeys(key(), y.key());
	}

	private BTree btree() {
		return _node.btree();
	}

	public static boolean lessThan(BTreePointer x, BTreePointer y) {
		return BTreePointer.min(x, y) == x
			&& !equals(x, y);
	}

	public static boolean equals(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}    
}
