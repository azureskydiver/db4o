/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import java.util.Vector;

import com.db4o.events.*;

public class EventRecorder implements EventListener4 {
	
	private final Vector _events = new Vector();
	private boolean _cancel;
	
	public synchronized void onEvent(Event4 e, EventArgs args) {
		if (_cancel && args instanceof CancellableEventArgs) {
			((CancellableEventArgs)args).cancel();
		}
		_events.addElement(new EventRecord(e, args));
		notifyAll();
	}

	public int size() {
		return _events.size();
	}

	public EventRecord get(int index) {
		return (EventRecord)_events.elementAt(index);
	}

	public void clear() {
        _events.removeAllElements();
	}

	public void cancel(boolean flag) {
		_cancel = flag;
	}
}