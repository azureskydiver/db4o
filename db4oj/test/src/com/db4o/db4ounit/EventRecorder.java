/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit;

import java.util.Vector;

import com.db4o.events.*;

public class EventRecorder implements EventListener4 {
	
	final Vector _events = new Vector();
	private boolean _cancel;
	
	public void onEvent(Event4 e, EventArgs args) {
		if (_cancel && args instanceof CancellableEventArgs) {
			((CancellableEventArgs)args).cancel();
		}
		_events.addElement(new EventRecord(e, args));
	}

	public int size() {
		return _events.size();
	}

	public EventRecord get(int index) {
		return (EventRecord)_events.get(index);
	}

	public void clear() {
		_events.clear();
	}

	public void cancel(boolean flag) {
		_cancel = flag;
	}
}