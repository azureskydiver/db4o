/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.caching;

import java.util.*;

import com.db4o.foundation.*;

/**
 * @exclude
 * Algorithm taken from here:
 * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.34.2641
 * 
 * @decaf.ignore.jdk11
 */
class LRU2QCache<K,V> implements Cache4<K,V>{
	
	private final List<K> _am;
	
	private final List<K> _a1;
	
	private final Map<K,V> _slots;
	
	private final int _maxSize;

	private final int _a1_threshold;
	
	LRU2QCache(int maxSize) {
		_maxSize = maxSize;
		_a1_threshold = _maxSize / 4;
		_am = new ArrayList<K>();
		_a1 = new ArrayList<K>(_a1_threshold);
		_slots = new HashMap<K, V>(maxSize);
	}
	
	public V produce(K key, Function4<K, V> producer, Procedure4<V> onDiscard) {
		
		if(key == null){
			throw new ArgumentNullException();
		}
		
		final int amIndex = _am.indexOf(key);
		if(amIndex >= 0){
			// move key ToFirst
			_am.remove(amIndex);
			_am.add(key);
			return _slots.get(key);
		}
		
		if(_a1.contains(key)){
			_a1.remove(key);
			_am.add(key);
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

	private void discardPageFrom(final List<K> list, Procedure4<V> onDiscard) {
	    discard(list.get(0), onDiscard);
	    list.remove(0);
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
