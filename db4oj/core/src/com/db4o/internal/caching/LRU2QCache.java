/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.caching;

import java.util.*;

import com.db4o.foundation.*;

/**
 * @exclude
 * Algorithm taken from here:
 * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.34.2641
 * 
 * @decaf.ignore
 */
class LRU2QCache<K,V> implements Cache4<K,V>{
	
	private final LinkedList<K> _am;
	
	private final LinkedList<K> _a1;
	
	private final Map<K,V> _slots;
	
	private final int _maxSize;

	private final int _a1_threshold;
	
	LRU2QCache(int maxSize) {
		_maxSize = maxSize;
		_a1_threshold = _maxSize / 4;
		_am = new LinkedList<K>();
		_a1 = new LinkedList<K>();
		_slots = new HashMap<K, V>(maxSize);
	}
	
	public V produce(K key, Function4<K, V> producer, Procedure4<V> onDiscard) {
		
		if(key == null){
			throw new ArgumentNullException();
		}
		
		if(_am.remove(key)){
			_am.addLast(key);
			return _slots.get(key);
		}
		
		if(_a1.remove(key)){
			_am.addLast(key);
			return _slots.get(key);
		}
		
		if(_slots.size() >= _maxSize){
			discardPage(onDiscard);
		}
		
		final V value = producer.apply(key);
		_slots.put(key, value);
		_a1.add(key);
		return value;
	}

	private void discardPage(Procedure4<V> onDiscard) {
	    if(_a1.size() >= _a1_threshold) {
	    	discardPageFrom(_a1, onDiscard);
	    } else {
	    	discardPageFrom(_am, onDiscard);
	    }
    }

	private void discardPageFrom(final LinkedList<K> list, Procedure4<V> onDiscard) {
	    discard(list.getFirst(), onDiscard);
	    list.removeFirst();
    }

	private void discard(K key, Procedure4<V> onDiscard) {
		if (null != onDiscard) {
			onDiscard.apply(_slots.get(key));
		}
	    _slots.remove(key);
    }

	public String toString() {
		return "LRU2QCache(am=" + toString(_am)  + ", a1=" + toString(_a1) + ")";
	}

	private String toString(Collection<K> list) {
		return Iterators.toString(Iterators.iterate(list));
	}

	public Iterator<V> iterator() {
		return _slots.values().iterator();
    }
}
