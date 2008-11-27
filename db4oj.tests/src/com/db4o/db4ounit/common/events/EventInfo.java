/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import com.db4o.events.*;
import com.db4o.foundation.*;

class EventInfo {
	public EventInfo(String eventFirerName, Procedure4<EventRegistry> eventListenerSetter) {
		_listenerSetter = eventListenerSetter;
		_eventFirerName = eventFirerName;
	}
	
	public Procedure4<EventRegistry> listenerSetter() {
		return _listenerSetter;
	}

	public String eventFirerName() {
		return _eventFirerName;
	}

	
	private final Procedure4<EventRegistry> _listenerSetter;
	private final String _eventFirerName;
}