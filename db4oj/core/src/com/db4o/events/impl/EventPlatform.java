/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.EventArgs;
import com.db4o.events.ObjectEventArgs;
import com.db4o.events.QueryEventArgs;
import com.db4o.query.Query;

class EventPlatform {

	protected static void triggerQueryEvent(Event4Impl e, Query q) {
		triggerEvent(e, new QueryEventArgs(q));
	}
	
	protected static boolean triggerCancellableObjectEventArgs(Event4Impl e, Object o) {
		CancellableObjectEventArgs coea = new CancellableObjectEventArgs(o);
		triggerEvent(e, coea);
		return !coea.isCancelled();
	}
	
	protected static void triggerObjectEvent(Event4Impl e, Object o) {
		triggerEvent(e, new ObjectEventArgs(o));
	}
	
	private static void triggerEvent(Event4Impl e, EventArgs ea) {
		e.trigger(ea);
	}
}
