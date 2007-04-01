/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class Event4Impl implements Event4 {

	private Collection4 _listeners;
	
	public void addListener(EventListener4 listener) {
		validateListener(listener);
		
		if (null == _listeners) {
			_listeners = new Collection4();
		}
		_listeners.add(listener);
	}	

	public void removeListener(EventListener4 listener) {
		validateListener(listener);
		
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
		
		Iterator4 iterator = _listeners.iterator();
		while (iterator.moveNext()) {
			EventListener4 listener = (EventListener4)iterator.current();
			onEvent(listener, this, args);
		}
	}
	
	private void onEvent(EventListener4 listener, Event4 e, EventArgs args) {
		try {
			listener.onEvent(e, args);
		} catch (RuntimeException exc) {
			throw new Db4oUserException(exc);
		}
	}
	
	private void validateListener(EventListener4 listener) {
		if (null == listener) {
			throw new ArgumentNullException("listener");
		}
	}

	public boolean hasListeners() {
		return _listeners != null;
	}
}
