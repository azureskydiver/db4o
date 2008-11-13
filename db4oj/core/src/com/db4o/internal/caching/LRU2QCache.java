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
	
	private final List<K> _am;
	
	private final List<K> _a1;
	
	private final Map<K,V> _slots;
	
	private final int _maxSize;
	
	LRU2QCache(int maxSize) {
		_maxSize = maxSize;
		_am = new ArrayList<K>();
		_a1 = new ArrayList<K>(a1Threshold());
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
		
		final V newValue = producer.apply(key);
		if(_slots.size() < _maxSize){
			_slots.put(key, newValue);
		} else if(_a1.size() >= a1Threshold()){
			K freedSlot = _a1.remove(0);
			discard(freedSlot, onDiscard);
			_slots.put(key, newValue);
		} else {
			K freedSlot = _am.remove(0);
			discard(freedSlot, onDiscard);
			_slots.put(key, newValue);
		}
		_a1.add(key);
		
		
		return newValue;
	}

	private void discard(K key, Procedure4<V> onDiscard) {
	    final V discardedPage = _slots.remove(key);
	    if (null != onDiscard) {
	    	onDiscard.apply(discardedPage);
	    }
    }

	private int a1Threshold() {
		return _maxSize / 4;
	}
	
	public String toString(){
		return "LRU2QCache(am=" + toString(_am)  + ", a1=" + toString(_a1) + ")";
	}

	private String toString(Collection<K> list) {
		return Iterators.toString(new JdkCollectionIterator4(list));
	}

	public Iterator<V> iterator() {
		return _slots.values().iterator();
    }
	
}
