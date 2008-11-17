package com.db4o.internal.caching;

import java.util.*;

import com.db4o.foundation.*;

/**
 */
class LRUCache<K, V> implements Cache4<K, V> {
	
	private final Map<K, V> _slots;
	private final CircularBuffer4<K> _lru;
	private final int _maxSize;

	LRUCache(int size) {
		_maxSize = size;
		_slots = new HashMap<K, V>(size);
		_lru = new CircularBuffer4<K>(size);
	}

	public V produce(K key, Function4<K, V> producer, Procedure4<V> onDiscard) {
		final V value = _slots.get(key);
		if (value == null) {
			if (_slots.size() >= _maxSize) {
				final V discarded = _slots.remove(_lru.removeLast());
				if (null != onDiscard) {
					onDiscard.apply(discarded);
				}
			}
			final V newValue = producer.apply(key);
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
}

