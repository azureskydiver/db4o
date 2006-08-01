/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.events;

import com.db4o.events.Event4;

public class EventRecord {
	public Event4 e;

	public Object args;

	public EventRecord(Event4 e, Object query) {
		this.e = e;
		this.args = query;
	}
}