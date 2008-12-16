package com.db4o.internal.io;

import com.db4o.foundation.*;
import com.db4o.internal.*;

public class BlockSizeImpl implements BlockSize {
	
	private final ListenerRegistry<Integer> _listenerRegistry = ListenerRegistry.newInstance();
	private int _value;

	public void register(Listener<Integer> listener) {
		_listenerRegistry.register(listener);
	}

	public void set(int newValue) {
		_value = newValue;
		_listenerRegistry.notifyListeners(_value);
	}

	public int value() {
		return _value;
	}
}
