package com.db4o.events.impl;

import com.db4o.events.*;
import com.db4o.foundation.*;

public class Event4Impl implements Event4 {

	private Collection4 _listeners;
	
	public void addListener(EventListener4 listener) {
		if (null == listener) {
			throw new IllegalArgumentException("listener can't be null");
		}
		
		if (null == _listeners) {
			_listeners = new Collection4();
		}
		_listeners.add(listener);
	}

	public void removeListener(EventListener4 listener) {
		if (null == listener) {
			throw new IllegalArgumentException("listener can't be null");
		}
		
		if (null != _listeners) {
			_listeners.remove(listener);
			if (0 == _listeners.size()) {
				_listeners = null;
			}
		}
	}
	
	public void trigger(EventArgs args) {
		if (null == _listeners) {
			return;
		}
		
		Iterator4 iterator = _listeners.strictIterator();
		while (iterator.hasNext()) {
			((EventListener4)iterator.next()).onEvent(this, args);
		}
	}

}
