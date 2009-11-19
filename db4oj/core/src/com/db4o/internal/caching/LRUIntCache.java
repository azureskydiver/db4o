/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.caching;

import java.util.*;

import com.db4o.foundation.*;

/**
 * @exclude
 */
class LRUIntCache<V> implements PurgeableCache4<Integer, V> {
	
	private final Map<Integer, V> _slots;
	private final CircularIntBuffer4 _lru;
	private final int _maxSize;

	LRUIntCache(int size) {
		_maxSize = size;
		_slots = new HashMap<Integer, V>(size);
		_lru = new CircularIntBuffer4(size);
	}

	public V produce(Integer key, Function4<Integer, V> producer, Procedure4<V> finalizer) {
		final V value = _slots.get(key);
		if (value == null) {
			final V newValue = producer.apply(key);
			if (newValue == null) {
				return null;
			}
			if (_slots.size() >= _maxSize) {
				final V discarded = _slots.remove(_lru.removeLast());
				if (null != finalizer) {
					finalizer.apply(discarded);
				}
			}
			_slots.put(key, newValue);
			_lru.addFirst(key);
			return newValue;
		}
		
		_lru.remove(key); // O(N) 
		_lru.addFirst(key);
		return value;
	}

	public Iterator iterator() {
		return _slots.values().iterator();
	}

	public V purge(Integer key) {
		V removed = _slots.remove(key);
		if(removed == null){
			return null;
		}
		_lru.remove(key);
		return removed;
    }
}

