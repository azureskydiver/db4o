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

	private Activator _activator;

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
	public V get(Object key) {
		activate(ActivationPurpose.READ);
		return super.get(key);
	}

	@Override
	public Set<K> keySet() {
		activate(ActivationPurpose.READ);
		return super.keySet();
	}
}
