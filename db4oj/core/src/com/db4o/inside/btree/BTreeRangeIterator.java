/**
 * 
 */
package com.db4o.inside.btree;

import com.db4o.foundation.KeyValueIterator;
import com.db4o.foundation.No4;

class BTreeRangeIterator implements KeyValueIterator {
	
	private final BTreeRangeImpl _range;
	
	private BTreePointer _cursor;
	private BTreeNode _lastNode = null;
	private BTreePointer _current;
	
	public BTreeRangeIterator(BTreeRangeImpl range) {
		_range = range;
		_cursor = range.start();
	}

	public boolean moveNext() {
		while(! reachedEnd(_cursor)){
            
            BTreeNode node = _cursor.node();
            
            if(node != _lastNode){
                node.prepareWrite(_range.transaction());
                
                // Alternative: work in read mode, hold the reader here.
                
                _lastNode = node;
            }
            
            Object obj = _cursor.key(_range.transaction());
            
            if(obj != No4.INSTANCE){
                _current = _cursor;
                _cursor = _cursor.next();
                return true;
            }
            
            _cursor = _cursor.next();
        }
		return false;
	}
	
	public Object key() {
		return _current.key(_range.transaction());
	}
	
	public Object value() {
		return _current.value();
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
}