/**
 * 
 */
package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.foundation.No4;

class BTreeRangeIterator implements KeyValueIterator {
	
	private final BTreeRangeImpl _range;
	
	private BTreePointer _cursor;
	private BTreeNode _lastNode = null;
	private BTreePointer _current;
	
	public BTreeRangeIterator(BTreeRangeImpl range) {
		_range = range;
		_cursor = range.first();
	}

	public boolean moveNext() {
		
		while(! reachedEnd(_cursor)){
            
            BTreeNode node = _cursor.node();
            
            if(node != _lastNode){
                node.prepareWrite(transaction());
                
                // Alternative: work in read mode, hold the reader here.
                
                _lastNode = node;
            }
            
            Object obj = _cursor.key(transaction());
            
            if(obj != No4.INSTANCE){
                _current = _cursor;
                _cursor = _cursor.next(transaction());
                return true;
            }
            
            _cursor = _cursor.next(transaction());
        }
		_current = null;
		return false;
	}
	
	public Object key() {
		return current().key(_range.transaction());
	}
	
	public Object value() {
		return current().value();
	}
	
	private BTreePointer current() {
		if (null == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}
	
	private boolean reachedEnd(BTreePointer cursor){
        if(cursor == null){
            return true;
        }
        if(_range.end() == null){
            return false;
        }
        return _range.end().equals(cursor);
    }
    
    private Transaction transaction(){
        return _range.transaction();
    }
}