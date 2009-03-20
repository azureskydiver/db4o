/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.collections;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * extends HashMap with Transparent Activation and
 * Transparent Persistence support
 * @exclude
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableHashMap<K,V> extends HashMap<K,V> implements Activatable {

	private transient Activator _activator;

	public ActivatableHashMap() {
	}

	public ActivatableHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ActivatableHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ActivatableHashMap(Map<K,V> map) {
		super(map);
	}

	public void activate(ActivationPurpose purpose) {
		if(_activator != null) {
			_activator.activate(purpose);
		}
	}

	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
		_activator = activator;
	}

	@Override
	public void clear() {
		activate(ActivationPurpose.WRITE);
		super.clear();
	}
	
	@Override
	public Object clone() {
		activate(ActivationPurpose.READ);
		ActivatableHashMap cloned = (ActivatableHashMap) super.clone();
		cloned._activator = null;
		return cloned;
	}
	
	@Override
	public boolean containsKey(Object key) {
		activate(ActivationPurpose.READ);
		return super.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		activate(ActivationPurpose.READ);
		return super.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		activate(ActivationPurpose.READ);
		return super.entrySet();
	}
	
	@Override
	public V get(Object key) {
		activate(ActivationPurpose.READ);
		return super.get(key);
	}

	@Override
	public boolean isEmpty() {
		activate(ActivationPurpose.READ);
		return super.isEmpty();
	}
	
	/**
	 * This method directly returns the set as provided by the super class.
	 * It relies on all modfications going through the public interface of the HashMap
	 * itself. If this is not the case, updates will get lost.
	 */
	@Override
	public Set<K> keySet() {
		activate(ActivationPurpose.READ);
		return super.keySet();
	}
	
	@Override
	public V put(K key, V value) {
		activate(ActivationPurpose.WRITE);
		return super.put(key, value);
	};
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		activate(ActivationPurpose.WRITE);
		super.putAll(m);
	}
	
	@Override
	public V remove(Object key) {
		activate(ActivationPurpose.WRITE);
		return super.remove(key);
	}
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}
	
	/**
	 * This method directly returns the collection as provided by the super class.
	 * It relies on all modfications going through the public interface of the HashMap
	 * itself. If this is not the case, updates will get lost.
	 */
	@Override
	public Collection<V> values() {
		activate(ActivationPurpose.READ);
		return super.values();
	}
	
	@Override
	public boolean equals(Object o) {
		activate(ActivationPurpose.READ);
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		activate(ActivationPurpose.READ);
		return super.hashCode();
	}

}
