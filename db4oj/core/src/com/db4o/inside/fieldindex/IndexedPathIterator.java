/**
 * 
 */
package com.db4o.inside.fieldindex;

import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.FieldIndexKey;

final class IndexedPathIterator implements KeyValueIterator {
	
	private IndexedPath _path;
	private KeyValueIterator _iterator;
	private KeyValueIterator _currentRangeIterator;

	public IndexedPathIterator(IndexedPath path, KeyValueIterator iterator) {
		_path = path;
		_iterator = iterator;
	}

	public boolean moveNext() {
		if (null == _currentRangeIterator) {
			if (!_iterator.moveNext()) {
				return false;
			}
			final FieldIndexKey key = (FieldIndexKey) _iterator.key();
			_currentRangeIterator = _path.search(new Integer(key.parentID())).iterator();
			return _currentRangeIterator.moveNext();
		}
		if (!_currentRangeIterator.moveNext()) {
			_currentRangeIterator = null;
			return moveNext();
		}
		return false;
	}

	public Object key() {
		return _currentRangeIterator.key();
	}

	public Object value() {
		return _currentRangeIterator.value();
	}
}