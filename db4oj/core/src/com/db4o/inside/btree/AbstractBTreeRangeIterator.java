package com.db4o.inside.btree;

import com.db4o.foundation.*;

public abstract class AbstractBTreeRangeIterator implements Iterator4 {

	private final BTreeRangeSingle _range;
	private BTreePointer _cursor;
	private BTreePointer _current;

	public AbstractBTreeRangeIterator(BTreeRangeSingle range) {
		_range = range;
		_cursor = range.first();
	}

	public boolean moveNext() {
		if (reachedEnd(_cursor)) {
			_current = null;
			return false;
		}
		_current = _cursor;
		_cursor = _cursor.next();
		return true;		
	}

	protected BTreePointer currentPointer() {
		if (null == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}

	private boolean reachedEnd(BTreePointer cursor) {
	    if(cursor == null){
	        return true;
	    }
	    if(_range.end() == null){
	        return false;
	    }
	    return _range.end().equals(cursor);
	}
}