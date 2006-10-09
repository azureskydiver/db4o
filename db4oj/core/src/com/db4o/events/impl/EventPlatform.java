/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.events.impl;

import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.ObjectEventArgs;
import com.db4o.events.QueryEventArgs;
import com.db4o.query.Query;

/**
 * Platform dependent code for dispatching events.
 * 
 * @sharpen.ignore
 */
class EventPlatform {

	public static void triggerQueryEvent(Event4Impl e, Query q) {
		e.trigger(new QueryEventArgs(q));
	}
	
	public static boolean triggerCancellableObjectEventArgs(Event4Impl e, Object o) {
		CancellableObjectEventArgs args = new CancellableObjectEventArgs(o);
		e.trigger(args);
		return !args.isCancelled();
	}
	
	public static void triggerObjectEvent(Event4Impl e, Object o) {
		e.trigger(new ObjectEventArgs(o));
	}
}
