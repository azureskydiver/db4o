/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * Platform dependent code for dispatching events.
 * 
 * @sharpen.ignore
 */
public class EventPlatform {

	public static void triggerQueryEvent(Transaction transaction, Event4Impl e, Query q) {
		if(!e.hasListeners()) {
			return;
		}
		e.trigger(new QueryEventArgs(transaction, q));
	}

	public static void triggerClassEvent(Event4Impl e, ClassMetadata clazz) {
		if(!e.hasListeners()) {
			return;
		}
		e.trigger(new ClassEventArgs(clazz));
	}

	public static boolean triggerCancellableObjectEventArgs(Transaction transaction, Event4Impl e, Object o) {
		if(!e.hasListeners()) {
			return true;
		}
		CancellableObjectEventArgs args = new CancellableObjectEventArgs(transaction, o);
		e.trigger(args);
		return !args.isCancelled();
	}
	
	public static void triggerObjectEvent(Transaction transaction, Event4Impl e, Object o) {
		if(!e.hasListeners()) {
			return;
		}
		e.trigger(new ObjectEventArgs(transaction, o));
	}

	public static void triggerCommitEvent(Transaction transaction, Event4Impl e, CallbackObjectInfoCollections collections) {
		if(!e.hasListeners()) {
			return;
		}
		e.trigger(new CommitEventArgs(transaction, collections));
	}
	
	public static void triggerObjectContainerEvent(ObjectContainer container, Event4Impl e) {
		if(!e.hasListeners()) {
			return;
		}
		e.trigger(new ObjectContainerEventArgs(container));
	}
	
	public static boolean hasListeners(Event4Impl e) {
		return e.hasListeners();
	}
}
