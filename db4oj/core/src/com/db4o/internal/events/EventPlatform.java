/* Copyright (C) 2006   Versant Inc.   http://www.db4o.com */

package com.db4o.internal.events;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * Platform dependent code for dispatching events.
 * 
 * @sharpen.ignore
 */
public class EventPlatform {

	public static void triggerQueryEvent(final Transaction transaction, final Event4Impl e, final Query q) {
		e.trigger(new QueryEventArgs(transaction, q));
	}

	public static void triggerClassEvent(final Event4Impl e, final ClassMetadata clazz) {
		e.trigger(new ClassEventArgs(clazz));
	}

	public static boolean triggerCancellableObjectEventArgs(final Transaction transaction, final Event4Impl e, final ObjectInfo objectInfo, final Object o) {
		return trigger(e, new Closure4<Boolean>() {
			public Boolean run() {
				CancellableObjectEventArgs args = new CancellableObjectEventArgs(transaction, objectInfo, o);
				e.trigger(args);
				return !args.isCancelled();
			}
		});		
	}
	
	public static void triggerObjectInfoEvent(final Transaction transaction, final Event4Impl e, final ObjectInfo o) {
		trigger(e, new Runnable() {
			public void run() {
				e.trigger(new ObjectInfoEventArgs(transaction, o));
			}
		});
	}

	public static void triggerCommitEvent(final Transaction transaction, final Event4Impl e, final CallbackObjectInfoCollections collections) {
		trigger(e, new Runnable() {
			public void run() {
				e.trigger(new CommitEventArgs(transaction, collections));
			}
		});
	}
	
	public static void triggerObjectContainerEvent(final ObjectContainer container, final Event4Impl e) {
		trigger(e, new Runnable() {
			public void run() {
				e.trigger(new ObjectContainerEventArgs(container));
			}
		});
	}
	
	private static boolean trigger(Event4Impl e, final Closure4<Boolean> code) {
		if (!e.hasListeners()) {
			return true;
		}
		
		final ByRef<Boolean> ret = ByRef.newInstance(false);
		InCallbackState._inCallback.with(true, new Runnable() {
			public void run() {
				ret.value = code.run();
			}
		});
		
		return ret.value;		
	}
	
	private static void trigger(Event4Impl e, final Runnable code) {
		if (!e.hasListeners()) {
			return;
		}
		
		InCallbackState._inCallback.with(true, new Runnable() {
			public void run() {
				code.run();
			}
		});
	}
	
	
	public static boolean hasListeners(Event4Impl e) {
		return e.hasListeners();
	}
}
